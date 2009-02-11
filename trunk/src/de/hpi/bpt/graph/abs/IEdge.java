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

import de.hpi.bpt.hypergraph.abs.IHyperEdge;
import de.hpi.bpt.hypergraph.abs.IVertex;

/**
 * Interface describing binary graph edge behavior (constrained by implementation)
 * Binary edge is an edge that connects exactly two vertices
 * 
 * @author Artem Polyvyanyy
 *
 * @param <V> Vertex type employed in the edge
 */
public interface IEdge<V extends IVertex> extends IHyperEdge<V> {
	/**
	 * Set graph edge vertices 
	 * @param v1 Vertex
	 * @param v2 Vertex
	 */
	public void setVertices(V v1, V v2);
	
	/**
	 * Get other vertex than specified  
	 * @param v Vertex
	 * @return Other connected vertex by the edge
	 */
	public V getOtherVertex(V v);

	/**
	 * Determines whether this edge is a self-loop
	 * @return <code>true</code> if this edge is a self-loop, <code>false</code> otherwise
	 */
	public boolean isSelfLoop();
	
	/**
	 * Get first vertex of the edge
	 * @return First vertex of the edge, <code>null</code> if such does not exist
	 */
	public V getV1();
	
	/**
	 * Get second vertex of the edge
	 * @return Second vertex of the edge, <code>null</code> if such does not exist
	 */
	public V getV2();
	
	/**
	 * Check if the edge connects two vertices
	 * @param v1 Vertex
	 * @param v2 Vertex
	 * @return <code>true</code> if this edge connects v1 and v2, <code>false</code> otherwise
	 */
	public boolean connectsVertices(V v1, V v2);
}
