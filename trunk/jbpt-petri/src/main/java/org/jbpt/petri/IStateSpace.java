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
	
}
