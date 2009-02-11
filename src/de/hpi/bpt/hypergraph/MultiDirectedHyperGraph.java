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
package de.hpi.bpt.hypergraph;

import java.util.ArrayList;
import java.util.Collection;

import de.hpi.bpt.hypergraph.abs.AbstractMultiDirectedHyperGraph;
import de.hpi.bpt.hypergraph.abs.Vertex;

/**
 * Multi directed graph implementation
 * 
 * @author Artem Polyvyanyy
 */
public class MultiDirectedHyperGraph extends AbstractMultiDirectedHyperGraph<DirectedHyperEdge, Vertex>
{
	/*
	 * (non-Javadoc)
	 * @see de.hpi.bpt.hypergraph.abs.AbstractMultiHyperGraph#addEdge(java.util.Collection)
	 */
	@Override
	public DirectedHyperEdge addEdge(Collection<Vertex> ss, Collection<Vertex> ts) {
		DirectedHyperEdge e = new DirectedHyperEdge(this);
		e.addSourceAndTagetVertices(ss, ts);
		
		return e;
	}

	/*
	 * (non-Javadoc)
	 * @see de.hpi.bpt.hypergraph.abs.AbstractMultiHyperGraph#addEdge(de.hpi.bpt.hypergraph.abs.IVertex)
	 */
	@Override
	public DirectedHyperEdge addEdge(Vertex s, Vertex t) {
		DirectedHyperEdge e = new DirectedHyperEdge(this);
		Collection<Vertex> ss = new ArrayList<Vertex>(); ss.add(s);
		Collection<Vertex> ts = new ArrayList<Vertex>(); ts.add(t);
		e.addSourceAndTagetVertices(ss, ts);
		return e;
	}
}
