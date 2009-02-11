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
package de.hpi.bpt.graph.test;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;
import de.hpi.bpt.graph.Edge;
import de.hpi.bpt.graph.Graph;
import de.hpi.bpt.graph.MultiGraph;
import de.hpi.bpt.hypergraph.abs.Vertex;

/**
 * 
 * @author Artem Polyvyanyy
 *
 */
public class GraphTest extends TestCase {
	public void testSomeBehavior() { 
		MultiGraph g = new MultiGraph();
		
		Vertex v1 = new Vertex("A");
		Vertex v2 = new Vertex("B");
		
		Edge e1 = g.addEdge(v1, v2);
		Edge e2 = g.addEdge(v1, v2);
		Edge e3 = g.addEdge(v1, v2);
		Edge e4 = g.addEdge(v1, v2);
		
		Collection<Edge> es = new ArrayList<Edge>();
		
		es.add(e1);
		es.add(e2);
		es.add(e3);
		es.add(e4);
		
		assertEquals(4, es.size());
		es.remove(e1);
		assertEquals(3, es.size());
		es.remove(e1);
		assertEquals(3, es.size());
		es.remove(e2);
		assertEquals(2, es.size());
		es.remove(e3);
		assertEquals(1, es.size());
		es.remove(e3);
		assertEquals(1, es.size());
		es.remove(e4);
		assertEquals(0, es.size());
	}
}
