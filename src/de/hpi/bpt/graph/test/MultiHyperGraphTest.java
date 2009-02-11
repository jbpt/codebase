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
import de.hpi.bpt.hypergraph.MultiHyperGraph;
import de.hpi.bpt.hypergraph.abs.Vertex;

/**
 * @author Artem Polyvyanyy
 *
 */
public class MultiHyperGraphTest extends TestCase {
	
	MultiHyperGraph mhg = new MultiHyperGraph();
	
	public MultiHyperGraphTest(String name) {
		super(name);
	}
	
	public void testSomeBehavior() {
		Vertex v = new Vertex("A");
		Vertex v2 = new Vertex("B");
		HyperEdge e = mhg.addEdge(v);
		
        assertEquals(1, mhg.countEdges());
        assertEquals(1, mhg.countVertices());
        assertNotNull(e);
        assertTrue(e.connectsVertex(v));
        
        HyperEdge e2 = mhg.addEdge(v);
        assertEquals(2, mhg.countEdges());
        assertEquals(1, mhg.countVertices());
        assertNotNull(e2);
        
        assertFalse(e.connectsVertex(v2));
        assertFalse(e2.connectsVertex(v2));
        
        assertNull(mhg.removeVertex(v2));
        assertEquals(2, mhg.countEdges());
        assertEquals(1, mhg.countVertices());
        
        assertNotNull(mhg.removeVertex(v));
        assertEquals(0, mhg.countEdges());
        assertEquals(0, mhg.countVertices());
        
        e.addVertex(v);
        e.addVertex(v2);
        assertEquals(2, mhg.countVertices());
        assertEquals(1, mhg.countEdges());
        assertTrue(e.connectsVertex(v2));
        assertTrue(e.connectsVertex(v2));
        assertFalse(e2.connectsVertex(v));
        
        mhg.removeEdge(e);
        assertEquals(0, mhg.countEdges());
        assertEquals(2, mhg.countVertices());
        
        mhg.removeVertices(mhg.getDisconnectedVertices());
        assertEquals(0, mhg.countVertices());
        
        /*e.addVertex(v2);
        assertEquals(0, mhg.countEdges());
        assertEquals(0, mhg.countVertices());*/
        
    }
}
