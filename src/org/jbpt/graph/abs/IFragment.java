package org.jbpt.graph.abs;

import java.util.Set;

import org.jbpt.hypergraph.abs.IVertex;

/**
 * Interface for a fragment of a graph.
 * A fragment of a graph is defined by a set of graph's edges.
 * 
 * @author Artem Polyvyanyy
 *
 * @param <E> Edge template.
 * @param <V> Vertex template.
 */
public interface IFragment <E extends IEdge<V>,V extends IVertex> extends Set<E>{
	/**
	 * Get a reference to the graph object for which this fragment is constructed. 
	 * @return Reference to the graph object of this fragment. 
	 */
	IGraph<E,V> getGraph();
}
