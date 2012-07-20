package org.jbpt.graph.abs;

import java.util.Set;

import org.jbpt.hypergraph.abs.IVertex;


/**
 * Tree interface.
 * A tree is an undirected graph in which any two vertices are connected by exactly one simple path. 
 * A simple path is a path with no repeated vertices. 
 * 
 * @author Artem Polyvyanyy
 * 
 * @param <V> template for vertex (extends IVertex)
 */
public interface ITree <V extends IVertex> {
	/**
	 * Get root vertex of this tree.
	 * @return Root vertex of this tree.
	 */
	public V getRoot();
	
	/**
	 * Set the new root of this tree.
	 * @param v Vertex to use as a new root.
	 * @return New root of this tree. Note that the root will stay the same if <tt>v</tt> does not belong to this tree.
	 */
	public V reRoot(V v);
	
	/**
	 * Check if the given vertex is the root of this tree.
	 * @param v Vertex to check.
	 * @return <tt>true<tt> if the given vertex is the root of this tree; <tt>false</tt> otherwise.
	 */
	public boolean isRoot(V v);

	/**
	 * Get children of the vertex. 
	 * @param v Vertex of this tree.
	 * @return Children of the vertex.
	 */
	public Set<V> getChildren(V v);
	
	/**
	 * Get parent of the vertex
	 * @param v Vertex of this tree.
	 * @return Parent vertex of <tt>v</tt> or <tt>null</tt> if <tt>v</tt> is the root vertex.
	 */
	public V getParent(V v);
	
	/**
	 * Add child vertex to a given vertex.
	 * @param p Parent vertex.
	 * @param c Child vertex.
	 * @return Fresh child vertex added to the tree; <tt>null</tt> if child was not added.
	 */
	public V addChild(V p, V c);
}
