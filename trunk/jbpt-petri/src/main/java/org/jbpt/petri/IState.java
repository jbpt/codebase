package org.jbpt.petri;

import org.jbpt.hypergraph.abs.IVertex;

/**
 * Interface to a state of a net system
 * @author Artem Polyvyanyy
 */
public interface IState<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>>
		extends IVertex {
	
	/**
	 * Get marking associated with this state.
	 * 
	 * @return Marking associated with this state.
	 */
	public M getMarking();
	
	/**
	 * Set marking associated with this state.
	 * 
	 * @param marking A marking to associate with this state.
	 */
	public void setMarking(M marking);
	
}
