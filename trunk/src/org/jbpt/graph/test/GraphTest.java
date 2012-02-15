package org.jbpt.graph.test;

import java.util.ArrayList;
import java.util.Collection;

import org.jbpt.graph.Edge;
import org.jbpt.graph.MultiGraph;
import org.jbpt.hypergraph.abs.Vertex;

import junit.framework.TestCase;

/**
 * 
 * @author Artem Polyvyanyy
 *
 */
public class GraphTest extends TestCase {
	public void testSomeBehavior() { 
		MultiGraph g = new MultiGraph();
		
		Vertex v1 = new Vertex("A");
		Vertex v2 = new Vertex("B");
		
		Edge e1 = g.addEdge(v1, v2);
		Edge e2 = g.addEdge(v1, v2);
		Edge e3 = g.addEdge(v1, v2);
		Edge e4 = g.addEdge(v1, v2);
		
		Collection<Edge> es = new ArrayList<Edge>();
		
		es.add(e1);
		es.add(e2);
		es.add(e3);
		es.add(e4);
		
		assertEquals(4, es.size());
		es.remove(e1);
		assertEquals(3, es.size());
		es.remove(e1);
		assertEquals(3, es.size());
		es.remove(e2);
		assertEquals(2, es.size());
		es.remove(e3);
		assertEquals(1, es.size());
		es.remove(e3);
		assertEquals(1, es.size());
		es.remove(e4);
		assertEquals(0, es.size());
	}
}
