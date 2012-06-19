package org.jbpt.test.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.jbpt.algo.graph.StronglyConnectedComponents;
import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.DirectedGraph;
import org.jbpt.hypergraph.abs.Vertex;
import org.junit.Test;

/**
 * Test of the algorithm for discovery of strongly connected components.
 *
 * @author Artem Polyvyanyy
 */
public class StronglyConnectedComponentsTest {

	@Test
	public void testCompute() {
		DirectedGraph dg = new DirectedGraph();
		
		Vertex a = new Vertex("a");
		Vertex b = new Vertex("b");
		Vertex c = new Vertex("c");
		Vertex d = new Vertex("d");
		Vertex e = new Vertex("e");
		Vertex f = new Vertex("f");
		Vertex g = new Vertex("g");
		Vertex h = new Vertex("h");
		
		dg.addEdge(a,b);
		dg.addEdge(b,c);
		dg.addEdge(c,d);
		dg.addEdge(d,c);
		dg.addEdge(e,a);
		dg.addEdge(b,e);
		dg.addEdge(b,f);
		dg.addEdge(c,g);
		dg.addEdge(d,h);
		dg.addEdge(e,f);
		dg.addEdge(f,g);
		dg.addEdge(g,f);
		dg.addEdge(g,h);
		dg.addEdge(h,h);
		
		StronglyConnectedComponents<DirectedEdge,Vertex> scc = new StronglyConnectedComponents<DirectedEdge,Vertex>();
		Set<Set<Vertex>> SCCs = scc.compute(dg);
		
		assertEquals(4,SCCs.size());
		
		for (Set<Vertex> SCC : SCCs) {
			if (SCC.size()==3) {
				assertTrue(SCC.contains(a));
				assertTrue(SCC.contains(b));
				assertTrue(SCC.contains(e));
			}
			
			if (SCC.size()==1) {
				assertTrue(SCC.contains(h));
			}
		}
	}

	@Test
	public void testIsStronglyConnected() {
		DirectedGraph dg = new DirectedGraph();
		
		Vertex a = new Vertex("a");
		Vertex b = new Vertex("b");
		Vertex c = new Vertex("c");
		Vertex d = new Vertex("d");
		Vertex e = new Vertex("e");
		Vertex f = new Vertex("f");
		Vertex g = new Vertex("g");
		Vertex h = new Vertex("h");
		
		dg.addEdge(a,b);
		dg.addEdge(b,c);
		dg.addEdge(c,d);
		dg.addEdge(d,c);
		dg.addEdge(e,a);
		dg.addEdge(b,e);
		dg.addEdge(b,f);
		dg.addEdge(c,g);
		dg.addEdge(d,h);
		dg.addEdge(e,f);
		dg.addEdge(f,g);
		dg.addEdge(g,f);
		dg.addEdge(g,h);
		dg.addEdge(h,h);
		
		StronglyConnectedComponents<DirectedEdge,Vertex> scc = new StronglyConnectedComponents<DirectedEdge,Vertex>();
		
		assertFalse(scc.isStronglyConnected(dg));
		dg.addEdge(f,b);
		dg.addEdge(h,d);
		assertTrue(scc.isStronglyConnected(dg));
	}

}
