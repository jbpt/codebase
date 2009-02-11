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

import junit.framework.TestCase;
import de.hpi.bpt.graph.DirectedEdge;
import de.hpi.bpt.graph.DirectedGraph;
import de.hpi.bpt.graph.DirectedGraphFragment;
import de.hpi.bpt.hypergraph.abs.Vertex;

/**
 * 
 * @author Artem Polyvyanyy
 *
 */
public class DirectedFragmentsTest extends TestCase{
	
	public void testSomeBehavior() {
		DirectedGraph g = new DirectedGraph();
		
		Vertex v1 = new Vertex("V1");
		Vertex v2 = new Vertex("V2");
		Vertex v3 = new Vertex("V3");
		Vertex v4 = new Vertex("V4");
		
		g.addEdge(v1,v2);
		g.addEdge(v2,v3);
		
		DirectedGraphFragment gf = new DirectedGraphFragment(g);
		
		DirectedEdge e1 = gf.addEdge(v1, v2); 
		assertNotNull(e1);
		assertNull(gf.addEdge(v1,v3));
		assertEquals(1,gf.countEdges());
		assertEquals(2,gf.countVertices());
		assertEquals(2,g.countEdges());
		assertEquals(3,g.countVertices());
		
		g.addEdge(v1,v3);
		
		assertNotNull(gf.addEdge(v1,v3));
		assertNull(gf.addEdge(v4,v3));
		assertEquals(2,gf.countEdges());
		assertEquals(3,gf.countVertices());
		assertEquals(3,g.countEdges());
		assertEquals(3,g.countVertices());
		
		gf.copyOriginalGraph();
		
		assertEquals(3,gf.countEdges());
		assertEquals(3,gf.countVertices());
		assertEquals(3,g.countEdges());
		assertEquals(3,g.countVertices());
	}
}
