package org.jbpt.petri;


import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jbpt.algo.graph.DirectedGraphAlgorithms;
import org.jbpt.graph.abs.AbstractDirectedGraph;
import org.jbpt.graph.abs.IDirectedEdge;

/**
 * Implementation of a state space of a net system.
 * 
 * @author Artem Polyvyanyy
 */
public class AbstractStateSpace<E extends IDirectedEdge<V>, V extends IState<F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>>
		extends AbstractDirectedGraph<E,V>
		implements IStateSpace<E,V,F,N,P,T,M> {
	
	private DirectedGraphAlgorithms<E,V> DGA = new DirectedGraphAlgorithms<>();
	
	private INetSystem<F,N,P,T,M> sys = null;
	
	Map<M,V> m2s = null;
	
	private boolean isComplete = false;
	
	/**
	 * Construct a state space of a net system.
	 * @param sys A net system.
	 */
	public AbstractStateSpace(INetSystem<F,N,P,T,M> sys) {
		this.sys = sys;
		
		this.m2s = new HashMap<>();
		
		this.construct(Integer.MAX_VALUE);
	}
	
	/**
	 * Construct a state space of a net system up to a given number of states.
	 * @param sys A net system. 
	 * @param maxSize Number of states to construct.
	 */
	public AbstractStateSpace(INetSystem<F,N,P,T,M> sys, int maxSize) {
		this.sys = sys;
		
		this.m2s = new HashMap<>();
		
		this.construct(maxSize);
	}

	private void construct(int maxSize) {
		if (maxSize<=0) {
			if (this.sys!=null) this.isComplete = false;
			return;
		}
		V ini = this.createState(this.sys.getMarking());
		this.addVertex(ini);
		
		Queue<V> queue = new ConcurrentLinkedQueue<>();
		queue.add(ini);
		
		m2s.put(this.sys.getMarking(),ini);
		
		while (!queue.isEmpty()) {
			V v = queue.poll();
			
			Set<T> enabled = this.sys.getEnabledTransitionsAtMarking(v.getMarking());
			
			for (T t : enabled) {
				@SuppressWarnings("unchecked")
				M freshMarking = (M) v.getMarking().clone();
				freshMarking.fire(t);
				
				if (m2s.containsKey(freshMarking)) {
					this.addEdge(v,m2s.get(freshMarking));
					continue;
				}
				
				if (m2s.size()>=maxSize) {
					if (!queue.isEmpty()) this.isComplete = false;
					return;
				}
					
				V state = this.createState(freshMarking);
				m2s.put(freshMarking,state);
				queue.add(state);
				this.addEdge(v,state);
			}
		}
		
		this.isComplete = true;
	}

	@Override
	public int size() {
		return this.vertices.size();
	}

	@Override
	public boolean isComplete() {
		return this.isComplete;
	}

	@Override
	public INetSystem<F,N,P,T,M> getNetSystem() {
		return this.sys;
	}
	
	@SuppressWarnings("unchecked")
	private V createState(M marking) {
		V state;
		try {
			state = (V) State.class.newInstance();
		} catch (Exception e) {
			return null;
		} 
		
		state.setMarking(marking);
		
		return state;
	}

	@Override
	public boolean isReachable(M marking) {
		return this.m2s.containsKey(marking);
	}

	@Override
	public boolean isReachable(M fromMarking, M toMarking) {
		if (!this.m2s.containsKey(fromMarking)) return false;
		if (!this.m2s.containsKey(toMarking)) return false;
		
		return this.DGA.hasPath(this,this.m2s.get(fromMarking),this.m2s.get(toMarking));
	}
	
}
