package org.jbpt.petri;

import org.jbpt.hypergraph.abs.IVertex;

/**
 * Interface to a step in a net system.
 * 
 * @author Artem Polyvyanyy
 */
public interface IStep<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>>
	extends ICommand<F,N,P,T,M>, IVertex {
	
	public T getTransition();
}
