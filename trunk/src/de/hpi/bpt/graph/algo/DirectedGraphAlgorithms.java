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
package de.hpi.bpt.graph.algo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import de.hpi.bpt.graph.abs.IDirectedEdge;
import de.hpi.bpt.graph.abs.IDirectedGraph;
import de.hpi.bpt.hypergraph.abs.IVertex;

/**
 * Collection of directed graph algorithms
 * @author Artem Polyvyanyy
 *
 * @param <E> template for edge (extends IDirectedEdge)
 * @param <V> template for vertex (extends IVertex)
 */
public class DirectedGraphAlgorithms<E extends IDirectedEdge<V>,V extends IVertex> {
	
	/**
	 * Get boundary vertices in the directed graph - vertices without predecessors or successors 
	 * @param g Directed graph
	 * @return Collection of boundary vertices
	 */
	public Collection<V> getBoundaryVertices(IDirectedGraph<E, V> g)
	{
		Collection<V> result = new ArrayList<V>();
		
		Iterator<V> i = g.getVertices().iterator();
		while (i.hasNext()) {
			V v = i.next();
			int in = g.getIncomingEdges(v).size();
			int out = g.getOutgoingEdges(v).size();
			if (in==0 || out==0) result.add(v);
		}
		
		return result;
	}
	
	/**
	 * Get directed graph input vertices
	 * @param g Directed graph
	 * @return Collection of graph input vertices
	 */
	public Collection<V> getInputVertices(IDirectedGraph<E, V> g)
	{
		Collection<V> result = new ArrayList<V>();
		Iterator<V> i = this.getBoundaryVertices(g).iterator();
		while (i.hasNext()) {
			V v = i.next();
			if (g.getPredecessors(v).size()==0)
				result.add(v);
		}
		
		return result;
	}
	
	/**
	 * Get directed graph output vertices
	 * @param g Directed graph
	 * @return Collection of graph output vertices
	 */
	public Collection<V> getOutputVertices(IDirectedGraph<E, V> g)
	{
		Collection<V> result = new ArrayList<V>();
		Iterator<V> i = this.getBoundaryVertices(g).iterator();
		while (i.hasNext()) {
			V v = i.next();
			if (g.getSuccessors(v).size()==0)
				result.add(v);
		}
		
		return result;
	}
	
	/**
	 * Check if directed graph has cycles
	 * @param g Directed graph
	 * @return <code>true</code> if graph has a cycle, <code>false</code> otherwise
	 */
	public boolean hasCycles(IDirectedGraph<E, V> g) {
		TransitiveClosure<E, V> tc = new TransitiveClosure<E,V>(g);
		for (V v : g.getVertices())
			if (tc.isInLoop(v))
				return true;
		
		return false;
	}
}
