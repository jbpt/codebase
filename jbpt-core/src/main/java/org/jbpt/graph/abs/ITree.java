package org.jbpt.graph.abs;

import java.util.List;
import java.util.Set;

import org.jbpt.hypergraph.abs.IVertex;


/**
 * Interface to a tree.  
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
	 * 
	 * @return Root vertex of this tree.
	 */
	public V getRoot();
	
	/**
	 * Set the new root of this tree.
	 * 
	 * @param v Vertex to use as a new root.
	 * @return New root of this tree. Note that the root will stay the same if <tt>v</tt> does not belong to this tree.
	 */
	public V reRoot(V v);
	
	/**
	 * Check if the given vertex is the root of this tree.
	 * 
	 * @param v Vertex to check.
	 * @return <tt>true<tt> if the given vertex is the root of this tree; <tt>false</tt> otherwise.
	 */
	public boolean isRoot(V v);

	/**
	 * Get children of the vertex. 
	 * 
	 * @param v Vertex of this tree.
	 * @return Children of the vertex.
	 */
	public Set<V> getChildren(V v);
	
	/**
	 * Get parent of the vertex.
	 * 
	 * @param v Vertex of this tree.
	 * @return Parent vertex of <tt>v</tt> or <tt>null</tt> if <tt>v</tt> is the root vertex.
	 */
	public V getParent(V v);
	
	/**
	 * Add child vertex to a given vertex.
	 * 
	 * @param p Parent vertex.
	 * @param c Child vertex.
	 * @return Fresh child vertex added to the tree; <tt>null</tt> if child was not added.
	 */
	public V addChild(V p, V c);
	
	/**
	 * Get the lowest common ancestor (LCA) of two vertices of this tree. 
	 * The LCA is defined between two vertices 'v1' and 'v2' as the lowest node in the tree that has both 'v1' and 'v2' as descendants (where we allow a node to be descendant of itself).
	 *
	 * @param v1 Vertex in this tree.
	 * @param v2 Vertex in this tree.
	 * @return The LCA of 'v1' and 'v2'.
	 */
	public V getLCA(V v1, V v2);
	
	/**
	 * Check if one vertex is a child of the other vertex.
	 * 
	 * @param v1 Vertex in this tree.
	 * @param v2 Vertex in this tree.
	 * @return <tt>true</tt> if 'v1' is child of 'v2'; otherwise <tt>false</tt>;
	 */
	public boolean isChild(V v1, V v2);
	
	/**
	 * Check if one vertex is the parent of the other vertex.
	 * 
	 * @param v1 Vertex in this tree.
	 * @param v2 Vertex in this tree.
	 * @return <tt>true</tt> if 'v1' is parent of 'v2'; otherwise <tt>false</tt>;
	 */
	public boolean isParent(V v1, V v2);
	
	/**
	 * Check if one vertex is a descendant of the other vertex.
	 * 
	 * @param v1 Vertex in this tree.
	 * @param v2 Vertex in this tree.
	 * @return <tt>true</tt> if 'v1' is descendant of 'v2'; otherwise <tt>false</tt>;
	 */
	public boolean isDescendant(V v1, V v2);
	
	/**
	 * Check if one vertex is an ancestor of the other vertex.
	 * 
	 * @param v1 Vertex in this tree.
	 * @param v2 Vertex in this tree.
	 * @return <tt>true</tt> if 'v1' is ancestor of 'v2'; otherwise <tt>false</tt>;
	 */
	public boolean isAncestor(V v1, V v2);
	
	/**
	 * Get path from a vertex to its descendant (where we allow a node to be descendant of itself).
	 * 
	 * @param v1 Vertex in this tree.
	 * @param v2 Vertex in this tree.
	 * @return List of vertices on the path from 'v1' to 'v2'; empty list if 'v2' is not descendant of 'v1'.
	 */
	public List<V> getDownwardPath(V v1, V v2);
}
