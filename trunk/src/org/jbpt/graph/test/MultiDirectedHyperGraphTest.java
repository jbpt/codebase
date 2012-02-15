package org.jbpt.graph.test;

import org.jbpt.hypergraph.DirectedHyperEdge;
import org.jbpt.hypergraph.MultiDirectedHyperGraph;
import org.jbpt.hypergraph.abs.Vertex;

import junit.framework.TestCase;

/**
 * 
 * @author Artem Polyvyanyy
 *
 */
public class MultiDirectedHyperGraphTest extends TestCase {
	
	MultiDirectedHyperGraph g = new MultiDirectedHyperGraph();
	
	@SuppressWarnings("all")
	public void testSomeBehavior() {
		Vertex v1 = new Vertex("V1");
		Vertex v2 = new Vertex("V2");
		Vertex v3 = new Vertex("V3");
		Vertex v4 = new Vertex("V4");
		
		DirectedHyperEdge e1 = g.addEdge(v1, v2);
		
		assertEquals(1,g.countEdges());
		assertEquals(2,g.countVertices());
		
		DirectedHyperEdge e2 = g.addEdge(v1, v2);
		
		assertNotNull(e2);
		assertEquals(2,g.countEdges());
		assertEquals(2,g.countVertices());
		
		assertEquals(2,g.getEdges().size());
		assertEquals(2, g.getEdges(v1).size());
		
		e2.addTargetVertex(v3);
		
		assertEquals(2,g.getEdgesWithSource(v1).size());
		assertEquals(0,g.getEdgesWithTarget(v1).size());
		
		assertEquals(0,g.getEdgesWithSource(v2).size());
		assertEquals(2,g.getEdgesWithTarget(v2).size());
		
		assertEquals(0,g.getEdgesWithSource(v3).size());
		assertEquals(1,g.getEdgesWithTarget(v3).size());
		
		assertTrue(g.getEdgesWithSourceAndTarget(v1, v3).iterator().next().equals(e2));
		
		assertEquals(2,g.getIncomingEdges(v2).size());
		assertEquals(1,g.getIncomingEdges(v3).size());
		assertEquals(0,g.getIncomingEdges(v1).size());
		
		assertEquals(2,g.getOutgoingEdges(v1).size());
		assertEquals(0,g.getOutgoingEdges(v2).size());
		assertEquals(0,g.getOutgoingEdges(v3).size());
		
		assertEquals(0,g.getDirectPredecessors(v1).size());
		assertEquals(1,g.getDirectPredecessors(v2).size());
		assertEquals(1,g.getDirectPredecessors(v3).size());
		
		assertEquals(2,g.getDirectSuccessors(v1).size());
		assertEquals(0,g.getDirectSuccessors(v2).size());
		assertEquals(0,g.getDirectSuccessors(v3).size());
		
		assertEquals(3,g.getVertices().size());
		g.removeVertex(v2);
		assertEquals(2,g.countEdges());
		assertEquals(2,g.countVertices());
		
		g.removeEdge(e2);
		assertEquals(1,g.countEdges());
		assertEquals(3,g.countVertices());
		
		g.removeVertices(g.getDisconnectedVertices());
		assertEquals(1,g.countVertices());
		
		g.removeEdge(e1);
		assertEquals(0,g.countEdges());
		assertEquals(0,g.countVertices());
	}
	
}
