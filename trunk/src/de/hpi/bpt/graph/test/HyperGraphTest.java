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
import de.hpi.bpt.hypergraph.HyperEdge;
import de.hpi.bpt.hypergraph.HyperGraph;
import de.hpi.bpt.hypergraph.abs.Vertex;

/**
 * @author Artem Polyvyanyy
 *
 */
public class HyperGraphTest extends TestCase {

	HyperGraph hg = new HyperGraph();
	
	public HyperGraphTest(String name) {
		super(name);
	}

    public void testSomeBehavior() { 
    	Vertex v = new Vertex("A");
		Vertex v2 = new Vertex("B");
		Vertex v3 = new Vertex("A");
		HyperEdge e = hg.addEdge(v);
		
        assertEquals(1, hg.countEdges());
        assertEquals(1, hg.countVertices());
        assertNotNull(e);
        assertTrue(e.connectsVertex(v));
        
        HyperEdge e2 = hg.addEdge(v);
        assertNull(e2);
        assertEquals(1, hg.countEdges());
        assertEquals(1, hg.countVertices());
        
        e.addVertex(v2);
        assertEquals(1, hg.countEdges());
        assertEquals(2, hg.countVertices());
        
        e2 = hg.addEdge(v);
        assertNotNull(e2);
        assertEquals(2, hg.countEdges());
        assertEquals(2, hg.countVertices());
        
        assertNull(e2.addVertex(v2));
        assertEquals(2, hg.countEdges());
        assertEquals(2, hg.countVertices());
        assertNull(e.removeVertex(v2));
        assertEquals(2, hg.countEdges());
        assertEquals(2, hg.countVertices());
        
        assertNotNull(e.addVertex(v));
        assertEquals(2, hg.countEdges());
        assertEquals(2, hg.countVertices());
        
        assertNotNull(e2.addVertex(v2));
        assertEquals(2, hg.countEdges());
        assertEquals(2, hg.countVertices());
        assertNotNull(e2.addVertex(v3));
        assertNotNull(e2.addVertex(v));
        assertEquals(2, hg.countEdges());
        assertEquals(3, hg.countVertices());
        
        assertNull(e.addVertex(v3));
        assertNull(e2.removeVertex(v3));
        
        hg.removeEdge(e2);
        assertEquals(1, hg.countEdges());
        assertEquals(2, hg.countVertices());
        assertEquals(3,e.getVertices().size());
        
        e.removeVertex(v);
        assertEquals(1, hg.countEdges());
        assertEquals(1, hg.countVertices());
        
        e.removeVertex(v2);
        assertEquals(0, hg.countEdges());
        assertEquals(0, hg.countVertices());
        
    } 
}
