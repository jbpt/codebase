package org.jbpt.petri;

import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.graph.abs.IDirectedGraph;

/**
 * Interface to a state space of a net system.
 * 
 * @author Artem Polyvyanyy
 */
public interface IStateSpace<E extends IDirectedEdge<V>, V extends IState<F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> 
		extends IDirectedGraph<E,V> {

	/**
	 * Get size of this state space.
	 * 
	 * @return Size of this state space.
	 */
	public int size();
	
	/**
	 * Check if this state space complete.
	 * 
	 * @return <tt>true</tt> if this state space is complete (contains all states); otherwise <tt>false</tt>.
	 */
	public boolean isComplete();
	
	/**
	 * Check if a given state is reachable.
	 * 
	 * @param state A state.
	 * @return <tt>true</tt> if the given state is reachable in this state space; otherwise <tt>false</tt>.
	 */
	public boolean isReachable(IState<F,N,P,T,M> state);
	
	/**
	 * Check if a given state is reachable from another state.
	 * 
	 * @param fromState A state.
	 * @param toState A state.
	 * @return <tt>true</tt> if the state 'toState' is reachable from the state 'fromState' in this state space; otherwise <tt>false</tt>.
	 */
	public boolean isReachable(IState<F,N,P,T,M> fromState, IState<F,N,P,T,M> toState);
	
	/**
	 * Get net system for which this state space is constructed.
	 * 
	 * @return Net system for which this state space is constructed.
	 */
	public INetSystem<F,N,P,T,M> getNetSystem();
	
}
