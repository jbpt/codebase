package org.jbpt.graph.test;

import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.DirectedGraph;
import org.jbpt.graph.Edge;
import org.jbpt.graph.Graph;
import org.jbpt.graph.algo.GraphAlgorithms;
import org.jbpt.hypergraph.abs.Vertex;

import junit.framework.TestCase;

public class GraphAlgorithmsTest_isConnected extends TestCase {
	private Graph g = new Graph();
	private DirectedGraph dg = new DirectedGraph();
	private GraphAlgorithms<Edge, Vertex> ga = new GraphAlgorithms<Edge, Vertex>();
	private GraphAlgorithms<DirectedEdge, Vertex> ga2 = new GraphAlgorithms<DirectedEdge, Vertex>();
	
	@SuppressWarnings("all")
	public void testSomeBehavior() {
		Vertex v1 = new Vertex("A");
		Vertex v2 = new Vertex("B");
		Vertex v3 = new Vertex("C");
		Vertex v4 = new Vertex("D");
		Vertex v5 = new Vertex("E");
		
		g.addEdge(v1, v2);
		assertTrue(ga.isConnected(g));
		g.addVertex(v3);
		assertFalse(ga.isConnected(g));
		g.addEdge(v2,v3);
		assertTrue(ga.isConnected(g));
		g.addEdge(v4, v5);
		assertFalse(ga.isConnected(g));
		g.addEdge(v2, v5);
		assertTrue(ga.isConnected(g));
		
		dg.addEdge(v1, v2);
		assertTrue(ga2.isConnected(dg));
		dg.addVertex(v3);
		assertFalse(ga2.isConnected(dg));
		dg.addEdge(v2,v3);
		assertTrue(ga2.isConnected(dg));
		dg.addEdge(v4, v5);
		assertFalse(ga2.isConnected(dg));
		dg.addEdge(v2, v5);
		assertTrue(ga2.isConnected(dg));
	}
}
