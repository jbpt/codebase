package de.hpi.bpt.graph.test;

import junit.framework.TestCase;
import de.hpi.bpt.hypergraph.DirectedHyperEdge;
import de.hpi.bpt.hypergraph.MultiDirectedHyperGraph;
import de.hpi.bpt.hypergraph.abs.Vertex;

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
		
		assertEquals(0,g.getPredecessors(v1).size());
		assertEquals(1,g.getPredecessors(v2).size());
		assertEquals(1,g.getPredecessors(v3).size());
		
		assertEquals(2,g.getSuccessors(v1).size());
		assertEquals(0,g.getSuccessors(v2).size());
		assertEquals(0,g.getSuccessors(v3).size());
		
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
