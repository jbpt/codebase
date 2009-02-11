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
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Notification mechanism of edge updates to graph
 * 
 * @author Artem Polyvyanyy
 *
 * @param <E> Edge type employed in the graph
 * @param <V> Vertex type employed in the graph
 */
public abstract class AbstractGraphNotifier<E extends IHyperEdge<V>, V extends IVertex> extends GObject {
	
	protected Map<V,Set<E>> vertices = new Hashtable<V, Set<E>>();
	protected Map<E,Set<V>> edges = new Hashtable<E, Set<V>>();
	
	/**
	 * Index vertex in the edge
	 * @param e Edge
	 * @param v Vertex
	 */
	protected void addIndex(E e, V v) {
		if (e == null || v == null) return;
		if (!this.edges.containsKey(e))
			this.edges.put((E) e,new HashSet<V>());
			
		this.edges.get(e).add(v);
		
		if (!this.vertices.containsKey(v))
			this.vertices.put(v,new HashSet<E>());
			
		this.vertices.get(v).add((E) e);
	}
	
	/**
	 * Index collection of vertices in the edge
	 * @param e Edge
	 * @param vs Collection of vertices
	 */
	protected void addIndex(E e, Collection<V> vs) {
		if (e == null || vs == null) return;
		Iterator<V> i = vs.iterator();
		while (i.hasNext()) {
			this.addIndex(e, i.next());
		}
	}
	
	/**
	 * Remove vertex index from the edge
	 * @param e Edge
	 * @param v Vertex
	 */
	protected void removeIndex(E e, V v) {
		if (e == null || v == null) return;
		if (this.edges.containsKey(e))
		{
			this.edges.get(e).remove(v);
			
			if (this.edges.get(e).size() == 0)
				this.edges.remove(e);
		}
		
		if (this.vertices.containsKey(v))
		{
			this.vertices.get(v).remove(e);
			
			if (this.vertices.get(v).size() == 0)
				this.vertices.remove(v);
		}
	}
	
	/**
	 * Remove vertex index for collection of vertices from the edge
	 * @param e Edge
	 * @param vs Collection of vertices
	 */
	protected void removeIndex(E e, Collection<V> vs) {
		if (e == null || vs == null) return;
		Iterator<V> i = vs.iterator();
		while (i.hasNext()) {
			this.removeIndex(e, i.next());
		}
	}
}
