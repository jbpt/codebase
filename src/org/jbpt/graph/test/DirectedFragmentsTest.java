package org.jbpt.graph.test;

import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.DirectedGraph;
import org.jbpt.graph.DirectedGraphFragment;
import org.jbpt.hypergraph.abs.Vertex;

import junit.framework.TestCase;

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
