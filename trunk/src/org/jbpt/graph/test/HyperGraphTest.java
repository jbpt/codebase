package org.jbpt.graph.test;

import org.jbpt.hypergraph.HyperEdge;
import org.jbpt.hypergraph.HyperGraph;
import org.jbpt.hypergraph.abs.Vertex;

import junit.framework.TestCase;

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
