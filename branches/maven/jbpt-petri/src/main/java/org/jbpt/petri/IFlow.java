package org.jbpt.petri;

import org.jbpt.graph.abs.IDirectedEdge;

/**
 * Interface to a flow relation of a Petri net.
 * 
 * @author Artem Polyvyanyy
 *
 * @param <N> Node template.
 */
public interface IFlow<N extends INode> extends IDirectedEdge<N>{
}