package org.jbpt.graph.abs;

import org.jbpt.hypergraph.abs.IDirectedHyperGraph;
import org.jbpt.hypergraph.abs.IVertex;

/**
 * Directed graph interface
 * @author Artem Polyvyanyy
 *
 * @param <E> template for edge (extends IDirectedEdge)
 * @param <V> template for vertex (extends IVertex)
 */
public interface IDirectedGraph<E extends IDirectedEdge<V>,V extends IVertex> extends IDirectedHyperGraph<E,V>, IGraph<E,V>
{
	/**
	 * Get directed edge that connects two vertices
	 * @param v1 Source vertex
	 * @param v2 Target vertex
	 * @return Edge that connects two vertices, <code>null</code> if no such edge exists
	 */
	public E getDirectedEdge(V v1, V v2);
}
