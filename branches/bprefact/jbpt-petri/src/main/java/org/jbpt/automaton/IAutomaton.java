package org.jbpt.automaton;

import java.util.Set;

import org.jbpt.graph.abs.IDirectedGraph;
import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;

/**
 * @author Artem Polyvyanyy
 */
public interface IAutomaton<ST extends IStateTransition<S,F,N,P,T,M>, S extends IState<F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> 
		extends IDirectedGraph<ST,S> {
	
	public boolean isComplete();

	public boolean isReachable(M marking);
	
	public boolean isReachable(M fromMarking, M toMarking);
	
	public INetSystem<F,N,P,T,M> getNetSystem();
	
	public Set<S> getStates();
	
	public Set<ST> getStateTransitions();
	
	public void construct(INetSystem<F,N,P,T,M> sys, int maxSize);
	
	public S getStartState();
}
