package de.hpi.bpt.hypergraph.abs;

import java.util.Collection;

/**
 * Interface describing directed hyper graph behavior
 * Directed hyper graph is collection of directed hyper edges and disconnected vertices
 * 
 * @author Artem Polyvyanyy
 *
 * @param <E> Edge type employed in the graph
 * @param <V> Vertex type employed in the graph
 */
public interface IDirectedHyperGraph <E extends IDirectedHyperEdge<V>,V extends IVertex> extends IHyperGraph<E,V>
{
	/**
	 * Add edge to the graph
	 * @param s Source vertex to create edge from
	 * @param t Target vertex to create edge from
	 * @return Edge that was added to the graph, <code>null</code> otherwise
	 */
	public E addEdge(V s, V t);
	
	/**
	 * Add edge to the graph
	 * @param ss Collection of source vertices to create edge from
	 * @param ts Collection of target vertices to create edge from
	 * @return Edge that was added to the graph, <code>null</code> otherwise
	 */
	public E addEdge(Collection<V> ss, Collection<V> ts);
	
	/**
	 * Get collection of edges that contain target vertex 'v'
	 * @param v Vertex
	 * @return Collection of edges that contain target vertex 'v'
	 */
	public Collection<E> getEdgesWithTarget(V v);
	
	/**
	 * Get collection of edges that contain target vertices 'vs'
	 * @param vs Vertices
	 * @return Collection of edges that contain target vertices 'vs'
	 */
	public Collection<E> getEdgesWithTargets(Collection<V> vs);
	
	/**
	 * Get collection of edges that contain source vertex 'v'
	 * @param v Vertex
	 * @return Collection edges that contain source vertex 'v'
	 */
	public Collection<E> getEdgesWithSource(V v);
	
	/**
	 * Get collection of edges that contain source vertices 'vs'
	 * @param vs Vertices
	 * @return Collection of edges that contain source vertices 'vs'
	 */
	public Collection<E> getEdgesWithSources(Collection<V> vs);
	
	/**
	 * Get collection of edges that contain source vertex 's' and target vertex 't'
	 * @param s Source vertex
	 * @param t Target vertex
	 * @return Collection of edges that contain source vertex 's' and target vertex 't'
	 */
	public Collection<E> getEdgesWithSourceAndTarget(V s, V t);
	
	/**
	 * Get collection of edges that contain source vertices 'ss' and target vertices 'ts'
	 * @param ss Source vertices
	 * @param ts Target vertices
	 * @return Collection of edges that contain source vertices 'ss' and target vertices 'ts'
	 */
	public Collection<E> getEdgesWithSourcesAndTargets(Collection<V> ss, Collection<V> ts);
	
	/**
	 * Get collection of vertices that are direct predecessors of a given vertex
	 * @param v Vertex
	 * @return Collection of predecessor vertices of the given vertex
	 */
	public Collection<V> getDirectPredecessors(V v);
	
	/**
	 * Get collection of vertices that are direct predecessors of vertices from a given collection
	 * @param vs Collection of vertices
	 * @return Collection of direct predecessor vertices of vertices from the given collection
	 */
	public Collection<V> getDirectPredecessors(Collection<V> vs);
	
	/**
	 * Get first arbitrary direct predecessor of a given vertex
	 * @param v Vertex
	 * @return Arbitrary predecessor of the given vertex, <code>null</code> if it does not exist
	 */
	public V getFirstDirectPredecessor(V v);
	
	/**
	 * Get collection of vertices that are direct successors to a given vertex
	 * @param v Vertex
	 * @return Collection of successor vertices of the given vertex
	 */
	public Collection<V> getDirectSuccessors(V v);
	
	/**
	 * Get collection of vertices that are direct successors of vertices from a given collection
	 * @param vs Collection of vertices
	 * @return Collection of direct successor vertices of vertices from the given collection
	 */
	public Collection<V> getDirectSuccessors(Collection<V> vs);
	
	/**
	 * Get first arbitrary direct successor of a given vertex
	 * @param v Vertex
	 * @return Arbitrary successor of the given vertex, <code>null</code> if it does not exist
	 */
	public V getFirstDirectSuccessor(V v);
	
	/**
	 * Get incoming edges of a given vertex
	 * @param v Vertex
	 * @return Incoming edges of the given vertex
	 */
	public Collection<E> getIncomingEdges(V v);
	
	/**
	 * Get first arbitrary incoming edge of a given vertex
	 * @param v Vertex
	 * @return Arbitrary incoming edge of the given vertex, <code>null</code> if it does not exist
	 */
	public E getFirstIncomingEdge(V v);
	
	/**
	 * Get outgoing edges of a given vertex
	 * @param v Vertex
	 * @return Outgoing edges of the given vertex
	 */
	public Collection<E> getOutgoingEdges(V v);
	
	/**
	 * Get first arbitrary outgoing edge of a given vertex
	 * @param v Vertex
	 * @return Arbitrary outgoing edge of the given vertex, <code>null</code> if it does not exist
	 */
	public E getFirstOutgoingEdge(V v);
}
