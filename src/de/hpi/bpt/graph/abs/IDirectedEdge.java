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
package de.hpi.bpt.graph.abs;

import de.hpi.bpt.hypergraph.abs.IDirectedHyperEdge;
import de.hpi.bpt.hypergraph.abs.IVertex;

/**
 * Interface describing directed binary graph edge behavior (constrained by implementation)
 * Directed binary edge is an edge that connects exactly two vertices and makes a difference between source and target 
 * 
 * @author Artem Polyvyanyy
 *
 * @param <V> Vertex type employed in the edge
 */
public interface IDirectedEdge <V extends IVertex> extends IDirectedHyperEdge<V>, IEdge<V> {	
	/**
	 * Get source vertex 
	 * @return Source vertex
	 */
	public V getSource();
	
	/**
	 * Set source vertex
	 * @param v Source vertex
	 * @return Vertex set as source, <code>null</code> upon failure
	 */
	public V setSource(V v);
	
	/**
	 * Get target vertex
	 * @return Target vertex
	 */
	public V getTarget();
	
	/**
	 * Set target vertex
	 * @param v Target vertex
	 * @return Vertex set as target, <code>null</code> upon failure
	 */
	public V setTarget(V v);
	
	/**
	 * Set directed graph edge vertices 
	 * @param v1 Source vertex
	 * @param v2 Target vertex
	 */
	public void setVertices(V s, V t);
}
