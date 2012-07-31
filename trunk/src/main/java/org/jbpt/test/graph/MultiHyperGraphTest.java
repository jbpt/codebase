package org.jbpt.test.graph;

import org.jbpt.hypergraph.HyperEdge;
import org.jbpt.hypergraph.MultiHyperGraph;
import org.jbpt.hypergraph.abs.Vertex;

import junit.framework.TestCase;

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
