package org.jbpt.automaton;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jbpt.algo.graph.DirectedGraphAlgorithms;
import org.jbpt.graph.abs.AbstractMultiDirectedGraph;
import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;

public class AbstractAutomaton<ST extends IStateTransition<S,F,N,P,T,M>, S extends IState<F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> 
		extends AbstractMultiDirectedGraph<ST,S>
		implements IAutomaton<ST,S,F,N,P,T,M> {
	
	@SuppressWarnings("unchecked")
	@Override
	public ST addEdge(S s, S t) {
		if (s == null || t == null) return null;
		
		ST st = (ST) new AbstractStateTransition<S,F,N,P,T,M>(this,s,t);
		
		return st;
	}

	private DirectedGraphAlgorithms<ST,S> DGA = new DirectedGraphAlgorithms<ST,S>();
	
	private INetSystem<F,N,P,T,M> sys = null;
	
	private Map<M,S> m2s = null;
	
	private boolean isComplete = false;
	
	public AbstractAutomaton(INetSystem<F,N,P,T,M> sys) {
		this.construct(sys, Integer.MAX_VALUE);
	}
	
	public AbstractAutomaton(INetSystem<F,N,P,T,M> sys, int maxSize) {
		this.construct(sys, maxSize);
	}
	
	public AbstractAutomaton() {
	}

	@Override
	public boolean isComplete() {
		return this.isComplete;
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

	@Override
	public INetSystem<F, N, P, T, M> getNetSystem() {
		return this.sys;
	}

	@Override
	public void construct(INetSystem<F,N,P,T,M> sys, int maxSize) {
		this.sys = sys;
		
		this.m2s = new HashMap<M,S>();
		
		if (maxSize<=0) {
			if (this.sys!=null) this.isComplete = false;
			return;
		}
		
		S ini = this.createState(this.sys.getMarking());
		this.addVertex(ini);
		
		Queue<S> queue = new ConcurrentLinkedQueue<S>();
		queue.add(ini);
		
		m2s.put(this.sys.getMarking(),ini);
		
		while (!queue.isEmpty()) {
			S v = queue.poll();
			
			Set<T> enabled = this.sys.getEnabledTransitionsAtMarking(v.getMarking());
			
			for (T t : enabled) {
				@SuppressWarnings("unchecked")
				M freshMarking = (M) v.getMarking().clone();
				freshMarking.fire(t);
				
				if (m2s.containsKey(freshMarking)) {
					ST edge = this.addEdge(v,m2s.get(freshMarking));
					edge.setSymbol(t);
					continue;
				}
				
				if (m2s.size()>=maxSize) {
					if (!queue.isEmpty()) this.isComplete = false;
					return;
				}
					
				S state = this.createState(freshMarking);
				m2s.put(freshMarking,state);
				queue.add(state);
				ST edge = (ST) this.addEdge(v,state);
				edge.setSymbol(t);
			}
		}
		
		this.isComplete = true;
		
	}
	
	@SuppressWarnings("unchecked")
	private S createState(M marking) {
		S state;
		try {
			state = (S) State.class.newInstance();
		} catch (Exception e) {
			return null;
		} 
		
		state.setMarking(marking);
		
		return state;
	}
	
	@Override
	public String toDOT() {
		String result = "digraph G {\n";
		result += "graph [fontname=\"Helvetica\" fontsize=10 \"];\n";
		result += "node [fontname=\"Helvetica\" fontsize=\"10\"];\n";
		
		for (S s : this.getStates()) { 
			result += String.format("\tstate%s[label=\"\" shape=\"point\" width=\".075\" height=\".075\"];\n", s.getId().replace("-", ""));
			result += String.format("\tmarking%s[label=\"%s\" shape=\"plaintext\"];\n", s.getId().replace("-", ""), s.getMarking().toMultiSet().toString());
			//result += String.format("{rank=\"same\"; state%s; marking%s}\n", s.getId().replace("-", ""), s.getId().replace("-", ""));			
		}
		
		result += "\n";
		
		for (S s : this.getStates()) { 
			result += String.format("\tstate%s->marking%s [style=\"dotted\", arrowhead=\"odot\", arrowsize=\"0\"];\n", s.getId().replace("-", ""), s.getId().replace("-", ""));
		}
		
		for (ST st: this.getStateTransitions()) {
			result += String.format("\tstate%s->state%s [label=\"%s\" fontname=\"Helvetica\" fontsize=\"10\" arrowhead=\"normal\" arrowsize=\".75\" color=\"black\"];\n", st.getSource().getId().replace("-", ""), st.getTarget().getId().replace("-", ""), st.getSymbol().getLabel());
		}
		result += "}\n";
		
		return result;
	}

	@Override
	public Set<S> getStates() {
		return new HashSet<S>(this.getVertices());
	}

	@Override
	public Set<ST> getStateTransitions() {
		return new HashSet<ST>(this.getEdges());
	}
	
	
}
