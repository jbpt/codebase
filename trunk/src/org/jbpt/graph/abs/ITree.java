package org.jbpt.graph.abs;

import java.util.Collection;

import org.jbpt.hypergraph.abs.IVertex;


/**
 * Tree interface.
 * A tree is an undirected graph in which any two vertices are connected by exactly one simple path. 
 * A simple path is a path with no repeated vertices. 
 * 
 * @author Artem Polyvyanyy
 * 
 * @param <E> template for edge (extends IEdge)
 * @param <V> template for vertex (extends IVertex)
 */
public interface ITree <E extends IEdge<V>,V extends IVertex> {
	/**
	 * Get root vertex of the tree
	 * @return Root vertex
	 */
	public V getRoot();
	
	/**
	 * Set the new root of the tree
	 * @param v Vertex to use as a new root
	 * @return New root of the tree. Note that the root will stay the same if <code>v</code> does not belong to the tree.
	 */
	public V reRoot(V v);

	/**
	 * Get children of the vertex
	 * @param v Vertex
	 * @return Children of <code>v</code>
	 */
	public Collection<V> getChildren(V v);
	
	/**
	 * Get parent of the vertex
	 * @param v Vertex
	 * @return Parent vertex of <code>v</code> or <code>null</code> if <code>v</code> is the root vertex
	 */
	public V getParent(V v);
	
	/**
	 * Add child vertex to a given one
	 * @param p Parent vertex
	 * @param c Child vertex
	 * @return Fresh edge added to the tree or <code>null</code> if child was not added 
	 */
	public E addChild(V p, V c);
}
