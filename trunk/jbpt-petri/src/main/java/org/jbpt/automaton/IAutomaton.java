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
	
	/**
	 * Check if this state space complete.
	 * 
	 * @return <tt>true</tt> if this state space is complete (contains all states); otherwise <tt>false</tt>.
	 */
	public boolean isComplete();
	
	/**
	 * Check if a given marking is reachable.
	 * 
	 * @param marking A marking.
	 * @return <tt>true</tt> if the given marking is reachable in this state space; otherwise <tt>false</tt>.
	 */
	public boolean isReachable(M marking);
	
	/**
	 * Check if a given marking is reachable from another marking.
	 * 
	 * @param fromMarking A marking.
	 * @param toMarking A marking.
	 * @return <tt>true</tt> if the marking 'toMarking' is reachable from the marking 'fromMarking' in this state space; otherwise <tt>false</tt>.
	 */
	public boolean isReachable(M fromMarking, M toMarking);
	
	/**
	 * Get net system for which this state space is constructed.
	 * 
	 * @return Net system for which this state space is constructed.
	 */
	
	public INetSystem<F,N,P,T,M> getNetSystem();
	
	public Set<S> getStates();
	
	public Set<ST> getStateTransitions();
	
	public void construct(INetSystem<F,N,P,T,M> sys, int maxSize);
}
