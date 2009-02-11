/**
 * Copyright (c) 2008 Artem Polyvyanyy
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
	 * Get collection of vertices that are predecessors to a given vertex
	 * @param v Vertex
	 * @return Collection of predecessor vertices
	 */
	public Collection<V> getPredecessors(V v);
	
	/**
	 * Get first arbitrary predecessor of the vertex
	 * @param v Vertex
	 * @return Arbitrary predecessor of vertex, <code>null</code> if it does not exist
	 */
	public V getFirstPredecessor(V v);
	
	/**
	 * Get collection of vertices that are successors to a given vertex
	 * @param v Vertex
	 * @return Collection of successor vertices
	 */
	public Collection<V> getSuccessors(V v);
	
	/**
	 * Get first arbitrary successor of the vertex
	 * @param v Vertex
	 * @return Arbitrary successor of vertex, <code>null</code> if it does not exist
	 */
	public V getFirstSuccessor(V v);
	
	/**
	 * Get vertex incoming edges
	 * @param v Vertex
	 * @return Vertex incoming edges
	 */
	public Collection<E> getIncomingEdges(V v);
	
	/**
	 * Get first arbitrary incoming edge of the vertex
	 * @param v Vertex
	 * @return Arbitrary incoming edge of vertex, <code>null</code> if it does not exist
	 */
	public E getFirstIncomingEdge(V v);
	
	/**
	 * Get vertex outgoing edges
	 * @param v Vertex
	 * @return Vertex outgoing edges
	 */
	public Collection<E> getOutgoingEdges(V v);
	
	/**
	 * Get first arbitrary outgoing edge of the vertex
	 * @param v Vertex
	 * @return Arbitrary outgoing edge of vertex, <code>null</code> if it does not exist
	 */
	public E getFirstOutgoingEdge(V v);
}
