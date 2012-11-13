package org.jbpt.petri;


import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

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
	
	private INetSystem<F,N,P,T,M> sys = null;
	
	private boolean isComplete = false;
	
	public AbstractStateSpace(INetSystem<F,N,P,T,M> sys) {
		this.sys = sys;
		
		this.construct(Integer.MAX_VALUE);
	}
	
	public AbstractStateSpace(INetSystem<F,N,P,T,M> sys, int maxSize) {
		this.sys = sys;
		
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
		
		Set<M> markings = new HashSet<M>();
		markings.add(this.sys.getMarking());
		
		while (!queue.isEmpty()) {
			V v = queue.poll();
			
			Set<T> enabled = this.sys.getEnabledTransitionsAtMarking(v.getMarking());
			
			for (T t : enabled) {
				@SuppressWarnings("unchecked")
				M freshMarking = (M) v.getMarking().clone();
				freshMarking.fire(t);
				
				if (markings.contains(freshMarking))
					continue;
				
				if (markings.size()>=maxSize) {
					if (!queue.isEmpty()) this.isComplete = false;
					return;
				}
					
				V state = this.createState(freshMarking);
				markings.add(freshMarking);
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
	public boolean isReachable(IState<F,N,P,T,M> state) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isReachable(IState<F,N,P,T,M> fromState, IState<F,N,P,T,M> toState) {
		// TODO Auto-generated method stub
		return false;
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
	
}
