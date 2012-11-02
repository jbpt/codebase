package org.jbpt.test.tree;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import org.jbpt.algo.tree.rpst.IRPSTNode;
import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.MultiDirectedGraph;
import org.jbpt.hypergraph.abs.Vertex;

public class RPSTTest extends TestCase {
	
	/**
	 * This test is taken from: 
	 * Artem Polyvyanyy, Jussi Vanhatalo, Hagen Völzer: Simplified Computation and Generalization of the Refined Process Structure Tree. WS-FM 2010: 25-41. 
	 * See Figure 6.
	 */
	public void testWSFM10_Figure6() {
		System.out.println("WSFM'10: Figure 6");
		MultiDirectedGraph g = new MultiDirectedGraph();
		
		Vertex s = new Vertex("s");
		Vertex u = new Vertex("u");
		Vertex v = new Vertex("v");
		Vertex w = new Vertex("w");
		Vertex x = new Vertex("x");
		Vertex t = new Vertex("t");
		
		g.addEdge(s,u);
		g.addEdge(u,v);
		g.addEdge(v,x);
		g.addEdge(u,w);
		g.addEdge(w,x);
		g.addEdge(v,w);
		g.addEdge(x,t);
		
		RPST<DirectedEdge,Vertex> rpst = new RPST<DirectedEdge,Vertex>(g);
		//IOUtils.toFile("rpst.dot", rpst.toDOT());
		
		for (IRPSTNode<DirectedEdge,Vertex> node : rpst.getRPSTNodes()) {
			System.out.print(node.getName() + ": ");
			for (IRPSTNode<DirectedEdge,Vertex> child : rpst.getPolygonChildren(node)) {
				System.out.print(child.getName() + " ");
			}
			System.out.println();
		}
		
		performBasicChecks(g,rpst);
		assertEquals(9,rpst.getRPSTNodes().size());
		assertEquals(7,rpst.getRPSTNodes(TCType.TRIVIAL).size());
		assertEquals(0,rpst.getRPSTNodes(TCType.BOND).size());
		assertEquals(1,rpst.getRPSTNodes(TCType.POLYGON).size());
		assertEquals(1,rpst.getRPSTNodes(TCType.RIGID).size());
		
		assertTrue(rpst.isRoot(rpst.getRPSTNodes(TCType.POLYGON).iterator().next()));
		assertEquals("s",rpst.getRPSTNodes(TCType.POLYGON).iterator().next().getEntry().getName());
		assertEquals("t",rpst.getRPSTNodes(TCType.POLYGON).iterator().next().getExit().getName());
		assertEquals(7,rpst.getRPSTNodes(TCType.POLYGON).iterator().next().getFragment().size());
		
		assertEquals("u",rpst.getRPSTNodes(TCType.RIGID).iterator().next().getEntry().getName());
		assertEquals("x",rpst.getRPSTNodes(TCType.RIGID).iterator().next().getExit().getName());
		assertEquals(5,rpst.getRPSTNodes(TCType.RIGID).iterator().next().getFragment().size());
		System.out.println("-----------------------------------------------------------------------");
	}
	
	/**
	 * This test is taken from: 
	 * Artem Polyvyanyy, Jussi Vanhatalo, Hagen Völzer: Simplified Computation and Generalization of the Refined Process Structure Tree. WS-FM 2010: 25-41. 
	 * See Figures 8 & 9.
	 */
	public void testWSFM10_Figures8and9() {
		System.out.println("WSFM'10: Figures 8 & 9");
		MultiDirectedGraph g = new MultiDirectedGraph();
		
		Vertex s = new Vertex("s");
		Vertex y = new Vertex("y");
		Vertex z = new Vertex("z");
		Vertex t = new Vertex("t");
		
		g.addEdge(s,y);
		g.addEdge(y,z);
		g.addEdge(y,z);
		g.addEdge(z,y);
		g.addEdge(z,t);
		
		RPST<DirectedEdge,Vertex> rpst = new RPST<DirectedEdge,Vertex>(g);
		//IOUtils.toFile("rpst.dot", rpst.toDOT());
		
		for (IRPSTNode<DirectedEdge,Vertex> node : rpst.getRPSTNodes()) {
			System.out.print(node.getName() + ": ");
			for (IRPSTNode<DirectedEdge,Vertex> child : rpst.getPolygonChildren(node)) {
				System.out.print(child.getName() + " ");	
			}
			System.out.println();
		}
		
		performBasicChecks(g,rpst);
		assertEquals(8,rpst.getRPSTNodes().size());
		assertEquals(5,rpst.getRPSTNodes(TCType.TRIVIAL).size());
		assertEquals(2,rpst.getRPSTNodes(TCType.BOND).size());
		assertEquals(1,rpst.getRPSTNodes(TCType.POLYGON).size());
		assertEquals(0,rpst.getRPSTNodes(TCType.RIGID).size());
		
		assertTrue(rpst.isRoot(rpst.getRPSTNodes(TCType.POLYGON).iterator().next()));
		assertEquals("s",rpst.getRPSTNodes(TCType.POLYGON).iterator().next().getEntry().getName());
		assertEquals("t",rpst.getRPSTNodes(TCType.POLYGON).iterator().next().getExit().getName());
		assertEquals(5,rpst.getRPSTNodes(TCType.POLYGON).iterator().next().getFragment().size());
		
		for (IRPSTNode<DirectedEdge,Vertex> bond : rpst.getRPSTNodes(TCType.BOND)) {
			assertEquals("y",bond.getEntry().getName());
			assertEquals("z",bond.getExit().getName());
		}
		System.out.println("-----------------------------------------------------------------------");
	}
	
	/**
	 * This test is taken from: 
	 * Jussi Vanhatalo, Hagen Völzer, Jana Koehler: The Refined Process Structure Tree. BPM 2008: 100-115.  
	 * See Figure 11.
	 */
	public void testBPM08_Figure6() {
		System.out.println("BPM'08: Figure 6");
		MultiDirectedGraph g = new MultiDirectedGraph();
		
		Vertex s = new Vertex("s");
		Vertex t = new Vertex("t");
		Vertex v1 = new Vertex("v1");
		Vertex v2 = new Vertex("v2");
		Vertex v3 = new Vertex("v3");
		Vertex v4 = new Vertex("v4");
		Vertex v5 = new Vertex("v5");
		Vertex v6 = new Vertex("v6");
		Vertex v7 = new Vertex("v7");
		
		g.addVertex(s);
		g.addVertex(t);
		g.addVertex(v1);
		g.addVertex(v2);
		g.addVertex(v3);
		g.addVertex(v4);
		g.addVertex(v5);
		g.addVertex(v6);
		g.addVertex(v7);
		
		g.addEdge(s,v1);
		g.addEdge(s,v2);
		g.addEdge(v1,v3);
		g.addEdge(v1,v5);
		g.addEdge(v2,v5);
		g.addEdge(v3,v2);
		g.addEdge(v3,v4);
		g.addEdge(v4,v1);
		g.addEdge(v4,v2);
		g.addEdge(v5,v6);
		g.addEdge(v5,v7);
		g.addEdge(v6,v5);
		g.addEdge(v6,v7);
		g.addEdge(v7,v5);
		g.addEdge(v7,t);
		
		RPST<DirectedEdge,Vertex> rpst = new RPST<DirectedEdge,Vertex>(g);
		//IOUtils.toFile("rpst.dot", rpst.toDOT());
		
		for (IRPSTNode<DirectedEdge,Vertex> node : rpst.getRPSTNodes()) {
			System.out.print(node.getName() + ": ");
			for (IRPSTNode<DirectedEdge,Vertex> child : rpst.getPolygonChildren(node)) {
				System.out.print(child.getName() + " ");	
			}
			System.out.println();
		}
		
		performBasicChecks(g,rpst);
		assertEquals(19,rpst.getRPSTNodes().size());
		assertEquals(15,rpst.getRPSTNodes(TCType.TRIVIAL).size());
		assertEquals(1,rpst.getRPSTNodes(TCType.BOND).size());
		assertEquals(1,rpst.getRPSTNodes(TCType.POLYGON).size());
		assertEquals(2,rpst.getRPSTNodes(TCType.RIGID).size());
		
		assertEquals("s",rpst.getRPSTNodes(TCType.POLYGON).iterator().next().getEntry().getName());
		assertEquals("t",rpst.getRPSTNodes(TCType.POLYGON).iterator().next().getExit().getName());
		assertEquals(15,rpst.getRPSTNodes(TCType.POLYGON).iterator().next().getFragment().size());
		
		assertEquals("v5",rpst.getRPSTNodes(TCType.BOND).iterator().next().getEntry().getName());
		assertEquals("v7",rpst.getRPSTNodes(TCType.BOND).iterator().next().getExit().getName());
		assertEquals(5,rpst.getRPSTNodes(TCType.BOND).iterator().next().getFragment().size());
		
		System.out.println("-----------------------------------------------------------------------");
	}
	
	/**
	 * This test is taken from: 
	 * Artem Polyvyanyy, Jussi Vanhatalo, Hagen Völzer: Simplified Computation and Generalization of the Refined Process Structure Tree. WS-FM 2010: 25-41. 
	 * See Figure 11.
	 */
	public void testWSFM10_Figure11() {
		System.out.println("WSFM'10: Figure 11");
		MultiDirectedGraph g = new MultiDirectedGraph();
		
		Vertex s = new Vertex("s");
		Vertex t = new Vertex("t");
		Vertex u = new Vertex("u");
		Vertex v = new Vertex("v");
		Vertex a1 = new Vertex("a1");
		Vertex a2 = new Vertex("a2");
		
		g.addEdge(s,u);
		g.addEdge(u,a1);
		g.addEdge(a1,u);
		g.addEdge(u,a2);
		g.addEdge(a2,v);
		g.addEdge(v,u);
		g.addEdge(v,t);
		
		RPST<DirectedEdge,Vertex> rpst = new RPST<DirectedEdge,Vertex>(g);
		//IOUtils.toFile("rpst.dot", rpst.toDOT());
		
		for (IRPSTNode<DirectedEdge,Vertex> node : rpst.getRPSTNodes()) {
			System.out.print(node.getName() + ": ");
			for (IRPSTNode<DirectedEdge,Vertex> child : rpst.getPolygonChildren(node)) {
				System.out.print(child.getName() + " ");	
			}
			System.out.println();
		}
		
		performBasicChecks(g,rpst);
		assertEquals(11,rpst.getRPSTNodes().size());
		assertEquals(7,rpst.getRPSTNodes(TCType.TRIVIAL).size());
		assertEquals(1,rpst.getRPSTNodes(TCType.BOND).size());
		assertEquals(3,rpst.getRPSTNodes(TCType.POLYGON).size());
		assertEquals(0,rpst.getRPSTNodes(TCType.RIGID).size());
		
		for (IRPSTNode<DirectedEdge,Vertex> polygon : rpst.getRPSTNodes(TCType.POLYGON)) {
			if (rpst.isRoot(polygon)) {
				assertEquals("s",polygon.getEntry().getName());
				assertEquals("t",polygon.getExit().getName());
				assertEquals(7,polygon.getFragment().size());
			}
			else {
				assertEquals("u",polygon.getEntry().getName());
			}
		}
		
		for (IRPSTNode<DirectedEdge,Vertex> bond : rpst.getRPSTNodes(TCType.BOND)) {
			assertEquals("u",bond.getEntry().getName());
			assertEquals("v",bond.getExit().getName());
			assertEquals(5,bond.getFragment().size());
		}
		
		System.out.println("-----------------------------------------------------------------------");
	}
	
	/**
	 * This test is taken from: 
	 * Artem Polyvyanyy, Jussi Vanhatalo, Hagen Völzer: Simplified Computation and Generalization of the Refined Process Structure Tree. WS-FM 2010: 25-41. 
	 * See Figure 12(a).
	 */
	public void testWSFM10_Figure12a() {
		System.out.println("WSFM'10: Figure 12(a)");
		MultiDirectedGraph g = new MultiDirectedGraph();
		
		Vertex s = new Vertex("s");
		Vertex t = new Vertex("t");
		Vertex u = new Vertex("u");
		Vertex v = new Vertex("v");
		
		g.addEdge(s,u);
		g.addEdge(u,v);
		g.addEdge(v,u);
		g.addEdge(u,t);
		
		RPST<DirectedEdge,Vertex> rpst = new RPST<DirectedEdge,Vertex>(g);		
		//IOUtils.toFile("rpst.dot", rpst.toDOT());
		
		for (IRPSTNode<DirectedEdge,Vertex> node : rpst.getRPSTNodes()) {
			System.out.print(node.getName() + ": ");
			for (IRPSTNode<DirectedEdge,Vertex> child : rpst.getPolygonChildren(node)) {
				System.out.print(child.getName() + " ");	
			}
			System.out.println();
		}
		
		performBasicChecks(g,rpst);
		assertEquals(6,rpst.getRPSTNodes().size());
		assertEquals(4,rpst.getRPSTNodes(TCType.TRIVIAL).size());
		assertEquals(0,rpst.getRPSTNodes(TCType.BOND).size());
		assertEquals(2,rpst.getRPSTNodes(TCType.POLYGON).size());
		assertEquals(0,rpst.getRPSTNodes(TCType.RIGID).size());
		
		for (IRPSTNode<DirectedEdge,Vertex> polygon : rpst.getRPSTNodes(TCType.POLYGON)) {
			if (rpst.isRoot(polygon)) {
				assertEquals("s",polygon.getEntry().getName());
				assertEquals("t",polygon.getExit().getName());
				assertEquals(4,polygon.getFragment().size());
			}
			else {
				assertEquals(polygon.getEntry().getName(),polygon.getExit().getName());
				assertEquals("u",polygon.getEntry().getName());
				assertEquals(2,polygon.getFragment().size());
			}
		}
		
		System.out.println("-----------------------------------------------------------------------");
	}
	
	/**
	 * This test is taken from: 
	 * Artem Polyvyanyy, Jussi Vanhatalo, Hagen Völzer: Simplified Computation and Generalization of the Refined Process Structure Tree. WS-FM 2010: 25-41. 
	 * See Figure 12(b).
	 */
	public void testWSFM10_Figure12b() {
		System.out.println("WSFM'10: Figure 12(b)");
		MultiDirectedGraph g = new MultiDirectedGraph();
		
		Vertex s = new Vertex("s");
		Vertex t = new Vertex("t");
		Vertex u = new Vertex("u");
		Vertex v = new Vertex("v");
		Vertex w = new Vertex("w");
		
		g.addEdge(s,u);
		g.addEdge(u,v);
		g.addEdge(v,u);
		g.addEdge(u,t);
		g.addEdge(u,w);
		g.addEdge(w,u);
		
		RPST<DirectedEdge,Vertex> rpst = new RPST<DirectedEdge,Vertex>(g);
		//IOUtils.toFile("rpst.dot", rpst.toDOT());
		
		for (IRPSTNode<DirectedEdge,Vertex> node : rpst.getRPSTNodes()) {
			System.out.print(node.getName() + ": ");
			for (IRPSTNode<DirectedEdge,Vertex> child : rpst.getPolygonChildren(node)) {
				System.out.print(child.getName() + " ");	
			}
			System.out.println();
		}
		
		performBasicChecks(g,rpst);
		assertEquals(10,rpst.getRPSTNodes().size());
		assertEquals(6,rpst.getRPSTNodes(TCType.TRIVIAL).size());
		assertEquals(1,rpst.getRPSTNodes(TCType.BOND).size());
		assertEquals(3,rpst.getRPSTNodes(TCType.POLYGON).size());
		assertEquals(0,rpst.getRPSTNodes(TCType.RIGID).size());
		
		for (IRPSTNode<DirectedEdge,Vertex> polygon : rpst.getRPSTNodes(TCType.POLYGON)) {
			if (rpst.isRoot(polygon)) {
				assertEquals("s",polygon.getEntry().getName());
				assertEquals("t",polygon.getExit().getName());
				assertEquals(6,polygon.getFragment().size());
			}
			else {
				assertEquals(polygon.getEntry().getName(),polygon.getExit().getName());
				assertEquals("u",polygon.getEntry().getName());
				assertEquals(2,polygon.getFragment().size());
			}
		}
		
		for (IRPSTNode<DirectedEdge,Vertex> bond : rpst.getRPSTNodes(TCType.BOND)) {
			assertEquals(bond.getEntry().getName(),bond.getExit().getName());
			assertEquals("u",bond.getEntry().getName());
		}
		
		System.out.println("-----------------------------------------------------------------------");
	}
	
	/**
	 * This test is taken from: 
	 * Artem Polyvyanyy, Jussi Vanhatalo, Hagen Völzer: Simplified Computation and Generalization of the Refined Process Structure Tree. WS-FM 2010: 25-41. 
	 * See Figure 12(c).
	 */
	public void testWSFM10_Figure12c() {
		System.out.println("WSFM'10: Figure 12(c)");
		MultiDirectedGraph g = new MultiDirectedGraph();
		
		Vertex s = new Vertex("s");
		Vertex t = new Vertex("t");
		Vertex u = new Vertex("u");
		Vertex v = new Vertex("v");
		Vertex w = new Vertex("w");
		
		g.addEdge(s,u);
		g.addEdge(u,t);
		g.addEdge(u,v);
		g.addEdge(v,u);
		g.addEdge(v,w);
		g.addEdge(w,v);
		
		RPST<DirectedEdge,Vertex> rpst = new RPST<DirectedEdge,Vertex>(g);
		//IOUtils.toFile("rpst.dot", rpst.toDOT());
		
		for (IRPSTNode<DirectedEdge,Vertex> node : rpst.getRPSTNodes()) {
			System.out.print(node.getName() + ": ");
			for (IRPSTNode<DirectedEdge,Vertex> child : rpst.getPolygonChildren(node)) {
				System.out.print(child.getName() + " ");	
			}
			System.out.println();
		}
		
		performBasicChecks(g,rpst);
		assertEquals(9,rpst.getRPSTNodes().size());
		assertEquals(6,rpst.getRPSTNodes(TCType.TRIVIAL).size());
		assertEquals(0,rpst.getRPSTNodes(TCType.BOND).size());
		assertEquals(3,rpst.getRPSTNodes(TCType.POLYGON).size());
		assertEquals(0,rpst.getRPSTNodes(TCType.RIGID).size());
		
		for (IRPSTNode<DirectedEdge,Vertex> polygon : rpst.getRPSTNodes(TCType.POLYGON)) {
			if (rpst.isRoot(polygon)) {
				assertEquals("s",polygon.getEntry().getName());
				assertEquals("t",polygon.getExit().getName());
				assertEquals(6,polygon.getFragment().size());
			}
			else {
				assertEquals(polygon.getEntry().getName(),polygon.getExit().getName());
			}
		}
		
		System.out.println("-----------------------------------------------------------------------");
	}
	
	/**
	 * This test is taken from: 
	 * Artem Polyvyanyy, Jussi Vanhatalo, Hagen Völzer: Simplified Computation and Generalization of the Refined Process Structure Tree. WS-FM 2010: 25-41. 
	 * See Figure 13.
	 */
	public void testWSFM10_Figure13() {
		System.out.println("WSFM'10: Figure 13");
		MultiDirectedGraph g = new MultiDirectedGraph();
		
		Vertex u = new Vertex("u");
		Vertex v = new Vertex("v");
		Vertex w = new Vertex("w");
		Vertex x = new Vertex("x");
		Vertex y = new Vertex("y");
		Vertex z = new Vertex("z");
		Vertex q = new Vertex("q");
		
		g.addEdge(u,w);
		g.addEdge(v,w);
		g.addEdge(w,x);
		g.addEdge(x,y);
		g.addEdge(x,z);
		g.addEdge(w,q);
		
		RPST<DirectedEdge,Vertex> rpst = new RPST<DirectedEdge,Vertex>(g);
		//IOUtils.toFile("rpst.dot", rpst.toDOT());
		
		for (IRPSTNode<DirectedEdge,Vertex> node : rpst.getRPSTNodes()) {
			System.out.print(node.getName() + ": ");
			for (IRPSTNode<DirectedEdge,Vertex> child : rpst.getPolygonChildren(node)) {
				System.out.print(child.getName() + " ");	
			}
			System.out.println();
		}
		
		performBasicChecks(g,rpst);
		assertEquals(11,rpst.getRPSTNodes().size());
		assertEquals(6,rpst.getRPSTNodes(TCType.TRIVIAL).size());
		assertEquals(3,rpst.getRPSTNodes(TCType.BOND).size());
		assertEquals(2,rpst.getRPSTNodes(TCType.POLYGON).size());
		assertEquals(0,rpst.getRPSTNodes(TCType.RIGID).size());
		
		for (IRPSTNode<DirectedEdge,Vertex> polygon : rpst.getRPSTNodes(TCType.POLYGON)) {
			if (rpst.isRoot(polygon)) {
				assertEquals(null,polygon.getEntry());
				assertEquals(null,polygon.getExit());
				assertEquals(6,polygon.getFragment().size());
			}
			else {
				assertEquals("w",polygon.getEntry().getName());
				assertEquals(null,polygon.getExit());
				assertEquals(3,polygon.getFragment().size());
			}
		}
		
		System.out.println("-----------------------------------------------------------------------");
	}
	
	/**
	 * This test is taken from: 
	 * Artem Polyvyanyy, Jussi Vanhatalo, Hagen Völzer: Simplified Computation and Generalization of the Refined Process Structure Tree. WS-FM 2010: 25-41. 
	 * See Figure 14.
	 */
	public void testWSFM10_Figure14() {
		System.out.println("WSFM'10: Figure 14");
		MultiDirectedGraph g = new MultiDirectedGraph();
		
		Vertex u = new Vertex("u");
		Vertex v = new Vertex("v");
		Vertex w = new Vertex("w");
		Vertex x = new Vertex("x");
		Vertex y = new Vertex("y");
		Vertex z = new Vertex("z");
		Vertex q = new Vertex("q");
		
		g.addEdge(u,v);
		g.addEdge(v,w);
		g.addEdge(v,x);
		g.addEdge(y,z);
		g.addEdge(z,q);
		
		RPST<DirectedEdge,Vertex> rpst = new RPST<DirectedEdge,Vertex>(g);
		//IOUtils.toFile("rpst.dot", rpst.toDOT());
		
		for (IRPSTNode<DirectedEdge,Vertex> node : rpst.getRPSTNodes()) {
			System.out.print(node.getName() + ": ");
			for (IRPSTNode<DirectedEdge,Vertex> child : rpst.getPolygonChildren(node)) {
				System.out.print(child.getName() + " ");	
			}
			System.out.println();
		}
		
		performBasicChecks(g,rpst);
		assertEquals(9,rpst.getRPSTNodes().size());
		assertEquals(5,rpst.getRPSTNodes(TCType.TRIVIAL).size());
		assertEquals(2,rpst.getRPSTNodes(TCType.BOND).size());
		assertEquals(2,rpst.getRPSTNodes(TCType.POLYGON).size());
		assertEquals(0,rpst.getRPSTNodes(TCType.RIGID).size());
		
		System.out.println("-----------------------------------------------------------------------");
	}
	
	private void performBasicChecks(MultiDirectedGraph g, RPST<DirectedEdge, Vertex> rpst) {
		for (IRPSTNode<DirectedEdge,Vertex> node : rpst.getRPSTNodes()) {
			assertTrue(g.getEdges().containsAll(node.getFragment()));
			
			Collection<DirectedEdge> edges = new ArrayList<DirectedEdge>();
			for (IRPSTNode<DirectedEdge,Vertex> child : rpst.getChildren(node)) {
				edges.addAll(child.getFragment());
			}
			
			assertTrue(node.getFragment().containsAll(edges));
		}
	}
	
	public void test_sequence() {
		System.out.println("SIMPLE SEQUENCE");
		MultiDirectedGraph g = new MultiDirectedGraph();
		
		Vertex u = new Vertex("u");
		Vertex v = new Vertex("v");
		Vertex w = new Vertex("w");
		Vertex x = new Vertex("x");
		
		g.addEdge(u,v);
		g.addEdge(v,w);
		g.addEdge(w,x);
		
		RPST<DirectedEdge,Vertex> rpst = new RPST<DirectedEdge,Vertex>(g);
		//IOUtils.toFile("rpst.dot", rpst.toDOT());
		
		for (IRPSTNode<DirectedEdge,Vertex> node : rpst.getRPSTNodes()) {
			System.out.print(node.getName() + ": ");
			for (IRPSTNode<DirectedEdge,Vertex> child : rpst.getPolygonChildren(node)) {
				System.out.print(child.getName() + " ");	
			}
			System.out.println();
		}
		
		System.out.println("ROOT:" + rpst.getRoot());
		
		assertNotNull(rpst.getRoot());
		assertEquals(1,rpst.getRPSTNodes(TCType.POLYGON).size());
		assertEquals(3,rpst.getRPSTNodes(TCType.TRIVIAL).size());
		assertEquals(0,rpst.getRPSTNodes(TCType.RIGID).size());
		assertEquals(0,rpst.getRPSTNodes(TCType.BOND).size());
		assertEquals(TCType.POLYGON, rpst.getRoot().getType());
		
		System.out.println("-----------------------------------------------------------------------");
	}
	
	public void test_disconnected() {
		System.out.println("DISCONNECTED AND STRUCTURED");
		MultiDirectedGraph g = new MultiDirectedGraph();
		
		Vertex v1 = new Vertex("v1");
		Vertex v2 = new Vertex("v2");
		Vertex v3 = new Vertex("v3");
		Vertex v4 = new Vertex("v4");
		Vertex v5 = new Vertex("v5");
		Vertex v6 = new Vertex("v6");
		Vertex v7 = new Vertex("v7");
		Vertex v8 = new Vertex("v8");
		Vertex v9 = new Vertex("v9");
		Vertex v10 = new Vertex("v10");
		Vertex v11 = new Vertex("v11");
		Vertex v12 = new Vertex("v12");
		Vertex v13 = new Vertex("v13");
		Vertex v14 = new Vertex("v14");
		Vertex v15 = new Vertex("v15");
		Vertex v16 = new Vertex("v16");
		Vertex v17 = new Vertex("v17");
		Vertex v18 = new Vertex("v18");
		Vertex v19 = new Vertex("v19");
		Vertex v20 = new Vertex("v20");
		Vertex v21 = new Vertex("v21");
		Vertex v22 = new Vertex("v22");
		
		g.addEdge(v1,v2);
		g.addEdge(v2,v3);
		g.addEdge(v3,v4);
		g.addEdge(v4,v5);
		g.addEdge(v5,v6);
		g.addEdge(v6,v7);
		g.addEdge(v7,v16);
		g.addEdge(v2,v8);
		g.addEdge(v8,v9);
		g.addEdge(v9,v10);
		g.addEdge(v10,v11);
		g.addEdge(v11,v12);
		g.addEdge(v12,v13);
		g.addEdge(v10,v13);
		g.addEdge(v13,v14);
		g.addEdge(v14,v15);
		g.addEdge(v15,v16);
		g.addEdge(v16,v17);
		g.addEdge(v18,v19);
		g.addEdge(v19,v20);
		g.addEdge(v20,v21);
		g.addEdge(v21,v22);
		
		RPST<DirectedEdge,Vertex> rpst = new RPST<DirectedEdge,Vertex>(g);
		//IOUtils.toFile("rpst.dot", rpst.toDOT());
		
		for (IRPSTNode<DirectedEdge,Vertex> node : rpst.getRPSTNodes()) {
			if (rpst.isRoot(node)) {
				assertNull(node.getEntry());
				assertNull(node.getExit());
			}
			else {
				assertNotNull(node.getEntry());
				assertNotNull(node.getExit());	
			}
			
			System.out.println(node.getName() + ": (" + node.getEntry() + "," + node.getExit() + ")");
			
		}
		assertNotNull(rpst.getRoot());
		
		assertEquals(5,rpst.getRPSTNodes(TCType.POLYGON).size());
		assertEquals(22,rpst.getRPSTNodes(TCType.TRIVIAL).size());
		assertEquals(0,rpst.getRPSTNodes(TCType.RIGID).size());
		assertEquals(3,rpst.getRPSTNodes(TCType.BOND).size());
		assertEquals(TCType.BOND, rpst.getRoot().getType());
		
		System.out.println("-----------------------------------------------------------------------");
	}
}
