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

import de.hpi.bpt.hypergraph.abs.IDirectedHyperGraph;
import de.hpi.bpt.hypergraph.abs.IVertex;

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
