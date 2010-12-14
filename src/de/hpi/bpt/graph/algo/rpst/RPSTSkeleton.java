/**
 * Copyright (c) 2010 Artem Polyvyanyy
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
package de.hpi.bpt.graph.algo.rpst;

import java.util.ArrayList;
import java.util.Collection;

import de.hpi.bpt.graph.abs.AbstractMultiDirectedGraph;
import de.hpi.bpt.graph.abs.IDirectedEdge;
import de.hpi.bpt.hypergraph.abs.IVertex;

public class RPSTSkeleton<E extends IDirectedEdge<V>, V extends IVertex>
			extends AbstractMultiDirectedGraph<E,V>
{
	private Collection<Collection<V>> vEdges = new ArrayList<Collection<V>>();
	
	public void addVirtualEdge(V v1, V v2) {
		Collection<V> edge = new ArrayList<V>();
		edge.add(v1);
		edge.add(v2);
		vEdges.add(edge);
	}
	
	@Override
	public E removeEdge(E e) {
		vEdges.remove(e);
		return super.removeEdge(e);
	}
	
	public Collection<Collection<V>> getVirtualEdges() {
		return this.vEdges;
	}
	
	public boolean isVirtual(E e) {
		return vEdges.contains(e);
	}
}
