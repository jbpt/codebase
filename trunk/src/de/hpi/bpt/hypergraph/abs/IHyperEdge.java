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
 * Interface describing hyper edge behavior
 * Hyper edge is a set of vertices
 * 
 * @author Artem Polyvyanyy
 * 
 * @param <V> Vertex type employed in the edge
 */
public interface IHyperEdge<V extends IVertex> extends IGObject
{
	/**
	 * Add vertex to the edge  
	 * @param v Vertex to add
	 * @return Vertex added to the edge, <code>null</code> upon failure
	 */
	public V addVertex(V v);
	
	/**
	 * Add collection of vertices to the edge
	 * @param vs Collection of vertices to add
	 * @return Collection of vertices added to the edge, <code>null</code> if no vertex was added
	 */
	public Collection<V> addVertices(Collection<V> vs);
	
	/**
	 * Remove vertex from the edge
	 * @param v Vertex to remove
	 * @return Vertex that was removed, <code>null</code> upon failure
	 */
	public V removeVertex(V v);
	
	/**
	 * Remove collection of vertices from the edge
	 * @param vs Collection of vertices to remove
	 * @return Collection of vertices removed from the edge, <code>null</code> if no vertex was removed
	 */
	public Collection<V> removeVertices(Collection<V> vs);
	
	/**
	 * Check if the edge connects vertex
	 * @param v Vertex to check
	 * @return <code>true</code> if the edge connects vertex, <code>false<code> otherwise
	 */
	public boolean connectsVertex(V v);
	
	/**
	 * Check if the edge connects vertices
	 * @param v Collection of vertices to check
	 * @return <code>true</code> if the edge connects all the vertices, <code>false<code> otherwise
	 */
	public boolean connectsVertices(Collection<V> vs);
	
	/**
	 * Get other vertices than the one proposed
	 * @param v Vertex proposed
	 * @return Collection of other vertices of the edge
	 */
	public Collection<V> getOtherVertices(V v);
	
	/**
	 * Get other vertices than the ones in the collection proposed
	 * @param vs Collection of vertices proposed
	 * @return Collection of other vertices of the edge
	 */
	public Collection<V> getOtherVertices(Collection<V> vs);
	
	
	/**
	 * Get vertices of the edge
	 * @return Collection of the edge vertices 
	 */
	public Collection<V> getVertices();
	
	/**
	 * Destroy the edge, unlink from the graph 
	 */
	public void destroy();
}
