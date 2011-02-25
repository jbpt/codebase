package de.hpi.bpt.graph.test;

import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import de.hpi.bpt.graph.DirectedEdge;
import de.hpi.bpt.graph.DirectedGraph;
import de.hpi.bpt.graph.algo.DirectedGraphAlgorithms;
import de.hpi.bpt.graph.algo.GraphAlgorithms;
import de.hpi.bpt.hypergraph.abs.Vertex;

public class DirectedGraphTest extends TestCase {
	DirectedGraph g = new DirectedGraph();
	
	GraphAlgorithms<DirectedEdge, Vertex> ga = new GraphAlgorithms<DirectedEdge, Vertex>();
	DirectedGraphAlgorithms<DirectedEdge, Vertex> dga = new DirectedGraphAlgorithms<DirectedEdge, Vertex>();
	
	@SuppressWarnings("all")
	public void testSomeBehavior() {
		Vertex v1 = new Vertex("V1");
		Vertex v2 = new Vertex("V2");
		Vertex v3 = new Vertex("V3");
		Vertex v4 = new Vertex("V4");
		Vertex v5 = new Vertex("V5");
		Vertex v6 = new Vertex("V6");
		
		DirectedEdge e1 = g.addEdge(v1, v2);
		DirectedEdge e2 = g.addEdge(v2, v3);
		DirectedEdge e3 = g.addEdge(v2, v4);
		
		assertTrue(ga.isConnected(g));
		
		assertEquals(3,ga.getBoundaryVertices(g).size());
		assertEquals(0,g.getDisconnectedVertices().size());
		assertEquals(4,g.getConnectedVertices().size());
		
		Map<Vertex,Set<Vertex>> dom = dga.getDominators(g, false);
		Map<Vertex,Set<Vertex>> pdom = dga.getDominators(g, true);
		
		assertEquals(4,dom.keySet().size());
		assertEquals(4,pdom.keySet().size());
		
		assertEquals(1,dom.get(v1).size());
		assertTrue(dom.get(v1).contains(v1));

		assertEquals(2,pdom.get(v1).size());
		assertTrue(pdom.get(v1).contains(v1));
		assertTrue(pdom.get(v1).contains(v2));

		assertEquals(2,dom.get(v2).size());
		assertTrue(dom.get(v2).contains(v1));
		assertTrue(dom.get(v2).contains(v2));
		
		assertNotNull(g.addVertex(v5));
		assertEquals(1,g.getDisconnectedVertices().size());
		assertEquals(4,g.getConnectedVertices().size());
		assertEquals(5,g.countVertices());
		assertEquals(3,g.countEdges());
		
		assertTrue(g.getAdjacent(v1).iterator().next().equals(v2));
		
		assertEquals(0, g.getPredecessors(v1).size());
		assertEquals(1, g.getSuccessors(v1).size());
		
		DirectedEdge e4 = g.addEdge(v3, v5);
		DirectedEdge e5 = g.addEdge(v4, v5);
		
		dom = dga.getDominators(g, false);
		pdom = dga.getDominators(g, true);
		
		assertEquals(5,dom.keySet().size());
		assertEquals(5,pdom.keySet().size());
		
		assertEquals(3,dom.get(v5).size());
		assertTrue(dom.get(v5).contains(v1));
		assertTrue(dom.get(v5).contains(v2));
		assertTrue(dom.get(v5).contains(v5));

		assertEquals(2,pdom.get(v4).size());
		assertTrue(pdom.get(v4).contains(v4));
		assertTrue(pdom.get(v4).contains(v5));

		assertNotNull(g.addVertex(v6));
		
		DirectedEdge e6 = g.addEdge(v6, v4);
		
		dom = dga.getDominators(g, false);
		pdom = dga.getDominators(g, true);
		
		assertEquals(6,dom.keySet().size());
		assertEquals(6,pdom.keySet().size());
		
		assertEquals(1,dom.get(v5).size());
		assertTrue(dom.get(v5).contains(v5));

		assertEquals(3,pdom.get(v6).size());
		assertTrue(pdom.get(v6).contains(v4));
		assertTrue(pdom.get(v6).contains(v4));
		assertTrue(pdom.get(v6).contains(v6));
		
		
		
		
		
		////
		//assertEquals(1,dga.getInputVertices(g).size());
		
	}
}
