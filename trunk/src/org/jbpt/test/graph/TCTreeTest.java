package org.jbpt.test.graph;

import junit.framework.TestCase;

import org.jbpt.algo.tree.tctree.TCTree;
import org.jbpt.graph.Edge;
import org.jbpt.graph.MultiGraph;
import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.utils.IOUtils;

public class TCTreeTest extends TestCase {
	
	public void testSimpleGraph() {
		MultiGraph g = new MultiGraph();
		
		Vertex s = new Vertex("s");
		Vertex t = new Vertex("t");
		Vertex u = new Vertex("u");
		Vertex v = new Vertex("v");
		Vertex w = new Vertex("w");
		Vertex x = new Vertex("x");
		Vertex y = new Vertex("y");
		Vertex z = new Vertex("z");
		
		g.addEdge(s,u);
		g.addEdge(u,v);
		g.addEdge(u,w);
		g.addEdge(v,w);
		g.addEdge(v,x);
		g.addEdge(w,x);
		g.addEdge(x,y);
		g.addEdge(y,z);
		g.addEdge(y,z);
		g.addEdge(y,z);
		g.addEdge(z,t);
		Edge backEdge = g.addEdge(t,s);
		
		IOUtils.toFile("graph.dot", g.toDOT());
		
		TCTree<Edge,Vertex> tctree = new TCTree<Edge,Vertex>();
		tctree.getTriconnectedComponents(g, backEdge);
		
	}
		
	/*public void testSimpleGraph() {
		
		System.out.println("========================================================");
		System.out.println("Simple graph");
		System.out.println("========================================================");
		
		//		  --- t3 --- t4 ---
		//		  |				  |
		// t1 -- s2 ------------ j5 -- t9
		//	.	  |				  |		.
		//	.	  |_ s6 ---- j7 __|		.
		// 	.		  |_ t8 _|			.
		//	............................. 
		
		ProcessModel p = new ProcessModel();
		
		Activity t1 = new Activity("1");
		Activity t3 = new Activity("3");
		Activity t4 = new Activity("4");
		Activity t8 = new Activity("8");
		Activity t9 = new Activity("9");
		
		XorGateway s2 = new XorGateway("2");
		XorGateway s6 = new XorGateway("6");
		XorGateway j7 = new XorGateway("7");
		XorGateway j5 = new XorGateway("5");
		
		p.addControlFlow(t1, s2);
		p.addControlFlow(s2, t3);
		p.addControlFlow(s2, s6);
		p.addControlFlow(s2, j5);
		p.addControlFlow(t3, t4);
		p.addControlFlow(t4, j5);
		p.addControlFlow(s6, j7);
		p.addControlFlow(s6, t8);
		p.addControlFlow(t8, j7);
		p.addControlFlow(j7, j5);
		p.addControlFlow(j5, t9);
		ControlFlow<FlowNode> backEdge = p.addControlFlow(t9, t1);
		
		ModelDecomposer<ControlFlow<FlowNode>,FlowNode> tc = new ModelDecomposer<ControlFlow<FlowNode>,FlowNode>();
		System.out.println(tc.getTriconnectedComponents(p, backEdge));
		
		System.out.println("-----------------------------");
		
		assertEquals(tc.getVertices().size(), 18);
		assertEquals(tc.getEdges().size(), 17);
		assertEquals(tc.getVertices(TCType.B).size(), 2);
		assertEquals(tc.getVertices(TCType.R).size(), 0);
		assertEquals(tc.getVertices(TCType.P).size(), 4);
		assertEquals(tc.getVertices(TCType.T).size(), 12);
	}*/
	
	/*public void testTrivialCase() {
		System.out.println("============================");
		System.out.println("Trivial case");
		System.out.println("============================");
		
		Graph g = new Graph();
		
		Vertex v1 = new Vertex("1");
		Vertex v2 = new Vertex("2");
		
		g.addEdge(v1, v2);
		
		TCTree<Edge, Vertex> tc = new TCTree<Edge, Vertex>(g);
		
		for (TCTreeNode<Edge, Vertex> node:tc.getVertices()) {
			System.out.println(node.getName() + ": " + node.getSkeleton().getEdges());
		}
		
		assertEquals(0, tc.getVertices(TCType.R).size());
		assertEquals(0, tc.getVertices(TCType.B).size());
		assertEquals(0, tc.getVertices(TCType.P).size());
		assertEquals(1, tc.getVertices(TCType.T).size());
	}
	
	public void testGraphWithR() {
		// create process model graph
		ProcessModel p = new ProcessModel();
		
		Activity t1 = new Activity("1");
		Activity t3 = new Activity("3");
		Activity t4 = new Activity("4");
		Activity t10 = new Activity("10");

		XorGateway s2 = new XorGateway("2");
		XorGateway s6 = new XorGateway("6");
		XorGateway s7 = new XorGateway("7");
		XorGateway j5 = new XorGateway("5");
		XorGateway j8 = new XorGateway("8");
		XorGateway j9 = new XorGateway("9");
		
		p.addControlFlow(t1, s2);
		p.addControlFlow(s2, t3);
		p.addControlFlow(s2, s6);
		p.addControlFlow(t3, t4);
		p.addControlFlow(t4, j5);
		p.addControlFlow(s6, s7);
		p.addControlFlow(s6, j9);
		p.addControlFlow(s7, j9);
		p.addControlFlow(s7, j8);
		p.addControlFlow(j9, j8);
		p.addControlFlow(j8, j5);
		p.addControlFlow(j5, t10);
		p.addControlFlow(t10, t1);
		
		TCTree<ControlFlow<FlowNode>, FlowNode> tc = new TCTree<ControlFlow<FlowNode>, FlowNode>(p);
		
		assertEquals(tc.getVertices(TCType.B).size(), 1);
		assertEquals(tc.getVertices(TCType.R).size(), 1);
		assertEquals(tc.getVertices(TCType.P).size(), 3);
	}
	
	public void testSimpleR() {
		//		  ----- s3 -----------
		//		  |		 |			 |
		// t1 -- s2 --- j4 -- t5 -- j6 -- t7
		//	.	  						   .
		//  ................................ 
		
		ProcessModel p = new ProcessModel();
		
		Activity t1 = new Activity("n1");
		Activity t5 = new Activity("n5");
		Activity t7 = new Activity("n7");

		XorGateway s2 = new XorGateway("n2");
		XorGateway j6 = new XorGateway("n6");
		XorGateway s3 = new XorGateway("n3");
		XorGateway j4 = new XorGateway("n4");
		
		p.addControlFlow(t1, s2);
		p.addControlFlow(s2, s3);
		p.addControlFlow(s2, j4);
		p.addControlFlow(s3, j4);
		p.addControlFlow(s3, j6);
		p.addControlFlow(j4, t5);
		p.addControlFlow(t5, j6);
		p.addControlFlow(j6, t7);
		ControlFlow backEdge = p.addControlFlow(t7, t1);
		
		TCTree<ControlFlow<FlowNode>, FlowNode> tc = new TCTree<ControlFlow<FlowNode>,FlowNode>(p, backEdge);
		
		for (TCTreeNode<ControlFlow<FlowNode>,FlowNode> n:tc.getVertices()) {
			System.out.println(String.valueOf(n) + ": " + n.getSkeleton().getEdges());
			System.out.println(String.valueOf(n) + ": " + n.getSkeleton().getVirtualEdges());
		}
		
		assertEquals(0, tc.getVertices(TCType.B).size());
		assertEquals(1, tc.getVertices(TCType.R).size());
		assertEquals(2, tc.getVertices(TCType.P).size());
	}
	
	public void testGraphWithR2() {
		// create process model graph
		ProcessModel p = new ProcessModel();
		
		Activity t1 = new Activity("1");
		Activity t3 = new Activity("3");
		Activity t4 = new Activity("4");
		Activity t10 = new Activity("10");
		Activity t11 = new Activity("11");

		XorGateway s2 = new XorGateway("2");
		XorGateway s6 = new XorGateway("6");
		XorGateway s7 = new XorGateway("7");
		XorGateway j5 = new XorGateway("5");
		XorGateway j8 = new XorGateway("8");
		XorGateway j9 = new XorGateway("9");
		
		p.addControlFlow(t1, s2);
		p.addControlFlow(s2, t3);
		p.addControlFlow(s2, s6);
		p.addControlFlow(t3, t4);
		p.addControlFlow(t4, j5);
		p.addControlFlow(s6, s7);
		p.addControlFlow(s6, j9);
		p.addControlFlow(s7, j9);
		p.addControlFlow(s7, j8);
		p.addControlFlow(j9, t11);
		p.addControlFlow(t11, j8);
		p.addControlFlow(j8, j5);
		p.addControlFlow(j5, t10);
		ControlFlow backEdge = p.addControlFlow(t10, t1);
		
		TCTree<ControlFlow<FlowNode>, FlowNode> tc = new TCTree<ControlFlow<FlowNode>, FlowNode>(p, backEdge);
		
		for (TCTreeNode<ControlFlow<FlowNode>, FlowNode> n:tc.getVertices()) {
			System.out.println(String.valueOf(n) + ": " + n.getSkeleton().getEdges());
			System.out.println(String.valueOf(n) + ": " + n.getSkeleton().getVirtualEdges());
		}
		
		assertEquals(tc.getVertices(TCType.B).size(), 1);
		assertEquals(tc.getVertices(TCType.R).size(), 1);
		assertEquals(tc.getVertices(TCType.P).size(), 4);
	}
	
	public void testType1SepPair() {
		// 		   ---- S2 -----------------
		//		   |	 |				   |
		//	T1 -- S1 -- J1 -- T2 -- S3 -- J2 -- T3
		//	 .			 |___________|			 .
		//	 ..................................... 
		
		ProcessModel p = new ProcessModel();
		
		Activity t1 = new Activity("T1");
		Activity t2 = new Activity("T2");
		Activity t3 = new Activity("T3");
		
		XorGateway s1 = new XorGateway("S1");
		XorGateway s2 = new XorGateway("S2");
		XorGateway s3 = new XorGateway("S3");
		XorGateway j1 = new XorGateway("J1");
		XorGateway j2 = new XorGateway("J2");
		
		p.addControlFlow(t1, s1);
		p.addControlFlow(s1, s2);
		p.addControlFlow(s1, j1);
		p.addControlFlow(s2, j1);
		p.addControlFlow(s2, j2);
		p.addControlFlow(j1, t2);
		p.addControlFlow(t2, s3);
		p.addControlFlow(s3, j1);
		p.addControlFlow(s3, j2);
		p.addControlFlow(j2, t3);
		p.addControlFlow(t3, t1);
		
		TCTree<ControlFlow<FlowNode>, FlowNode> tc = new TCTree<ControlFlow<FlowNode>, FlowNode>(p);
		
		//assertEquals(tc.getVertices().size(), 5);
		//assertEquals(tc.getEdges().size(), 4);
		assertEquals(3, tc.getVertices(TCType.P).size());
		assertEquals(1, tc.getVertices(TCType.B).size());
		assertEquals(1, tc.getVertices(TCType.R).size());
		
	}
	
	public void testSomeBehavior() {
		// create process model graph
		ProcessModel p = new ProcessModel();
		
		Activity t1 = new Activity("T1");
		Activity t2 = new Activity("T2");
		Activity t3 = new Activity("T3");
		Activity t4 = new Activity("T4");
		Activity t5 = new Activity("T5");
		Activity t6 = new Activity("T6");
		Activity t7 = new Activity("T7");
		Activity t8 = new Activity("T8");
		Activity t9 = new Activity("T9");
		Activity t10 = new Activity("T10");
		Activity t11 = new Activity("T11");
		Activity t12 = new Activity("T12");
		Activity t13 = new Activity("T13");
		Activity t14 = new Activity("T14");
		
		XorGateway s1 = new XorGateway("S1");
		XorGateway s2 = new XorGateway("S2");
		XorGateway s3 = new XorGateway("S3");
		XorGateway j1 = new XorGateway("J1");
		XorGateway j2 = new XorGateway("J2");
		XorGateway j3 = new XorGateway("J3");
		
		p.addControlFlow(t1, s1);
		p.addControlFlow(s1, t2);
		p.addControlFlow(s1, t3);
		p.addControlFlow(s1, t10);
		p.addControlFlow(t2, s2);
		p.addControlFlow(t3, j1);
		p.addControlFlow(t10, t11);
		p.addControlFlow(s2, t4);
		p.addControlFlow(s2, t6);
		p.addControlFlow(s2, t7);
		p.addControlFlow(s2, j1);
		p.addControlFlow(j1, t9);
		p.addControlFlow(t4, t5);
		p.addControlFlow(t9, s3);
		p.addControlFlow(s3, j1);
		p.addControlFlow(t11, t12);
		p.addControlFlow(t12, j3);
		p.addControlFlow(s3, j2);
		p.addControlFlow(j2, t13);
		p.addControlFlow(t7, t8);
		p.addControlFlow(t8, j2);
		p.addControlFlow(t5, j3);
		p.addControlFlow(t6, j3);
		p.addControlFlow(t13, j3);
		p.addControlFlow(j3, t14);
		p.addControlFlow(t14, t1);
		
		TCTree<ControlFlow<FlowNode>, FlowNode> tc = new TCTree<ControlFlow<FlowNode>, FlowNode>(p);
		
		for (TCTreeNode<ControlFlow<FlowNode>, FlowNode> n:tc.getVertices()) {
			System.out.println(String.valueOf(n) + ": " + n.getSkeleton().getEdges());
			System.out.println(String.valueOf(n) + ": " + n.getBoundaryNodes());
		}
		
		System.out.println("Vertices: " + tc.countVertices());
		System.out.println("Edges: " + tc.countEdges());
		
		assertEquals(3, tc.getVertices(TCType.B).size());
		assertEquals(1, tc.getVertices(TCType.R).size());
		assertEquals(10, tc.getVertices(TCType.P).size());
	}*/
	
	/*public void testOneMoreComplexExample() {
		Graph g = new Graph();
		
		Vertex i = new Vertex("I");
		Vertex a = new Vertex("A");
		Vertex v18 = new Vertex("18");
		Vertex v51 = new Vertex("51");
		Vertex v6 = new Vertex("6");
		Vertex v56 = new Vertex("56");
		Vertex v55 = new Vertex("55");
		Vertex v33 = new Vertex("33");
		Vertex v46 = new Vertex("46");
		Vertex v38 = new Vertex("38");
		Vertex v37 = new Vertex("37");
		Vertex v10 = new Vertex("10");
		Vertex v3 = new Vertex("3");
		Vertex v42 = new Vertex("42");
		Vertex e = new Vertex("E");
		Vertex o = new Vertex("O");
		
		g.addEdge(i, a);
		g.addEdge(a, v18);
		g.addEdge(a, v51);
		g.addEdge(a, v6);
		g.addEdge(a, v33);
		g.addEdge(a, v42);
		g.addEdge(v18, e);
		g.addEdge(v51, v18);
		g.addEdge(v51, v6);
		g.addEdge(v51, v56);
		g.addEdge(v56, v55);
		g.addEdge(v56, e);
		g.addEdge(v55, v33);
		g.addEdge(v55, e);
		g.addEdge(v33, e);
		g.addEdge(v6, v46);
		g.addEdge(v46, v38);
		g.addEdge(v46, v10);
		g.addEdge(v38, v37);
		g.addEdge(v38, v10);
		g.addEdge(v37, v46);
		g.addEdge(v10, v3);
		g.addEdge(v3, v42);
		g.addEdge(v3, e);
		g.addEdge(v42, e);
		g.addEdge(e, o);
		g.addEdge(o, i);
		
		TCTree<Edge, Vertex> tc = new TCTree<Edge, Vertex>(g);
		
		for (TCTreeNode<Edge, Vertex> n:tc.getVertices()) {
			System.out.println(String.valueOf(n) + ": " + n.getSkeleton().getEdges());
		}
		
		assertEquals(4, tc.getVertices(TCType.P).size());
		assertEquals(2, tc.getVertices(TCType.B).size());
		assertEquals(1, tc.getVertices(TCType.R).size());
	}
	
	public void testNestedB() {
		Graph g = new Graph();
		
		Vertex v1 = new Vertex("1");
		Vertex v2 = new Vertex("2");
		Vertex v3 = new Vertex("3");
		Vertex v4 = new Vertex("4");
		Vertex v5 = new Vertex("5");
		Vertex v6 = new Vertex("6");
		
		g.addEdge(v1, v2);
		g.addEdge(v2, v4);
		g.addEdge(v2, v5);
		g.addEdge(v4, v5);
		g.addEdge(v2, v3);
		g.addEdge(v3, v4);
		g.addEdge(v3, v6);
		g.addEdge(v6, v1);
		
		TCTree<Edge, Vertex> tc = new TCTree<Edge, Vertex>(g);
		
		for (TCTreeNode<Edge, Vertex> node:tc.getVertices()) {
			System.out.println(node.getName() + ": " + node.getSkeleton().getEdges());
		}
		
		assertEquals(0, tc.getVertices(TCType.R).size());
		assertEquals(2, tc.getVertices(TCType.B).size());
		assertEquals(3, tc.getVertices(TCType.P).size());
	}
	
	public void testTripleNestedB() {
		Graph g = new Graph();
		
		Vertex v1 = new Vertex("1");
		Vertex v2 = new Vertex("2");
		Vertex v3 = new Vertex("3");
		Vertex v4 = new Vertex("4");
		Vertex v5 = new Vertex("5");
		Vertex v6 = new Vertex("6");
		Vertex v7 = new Vertex("7");
		
		g.addEdge(v1, v2);
		g.addEdge(v2, v4);
		g.addEdge(v2, v5);
		g.addEdge(v4, v5);
		g.addEdge(v2, v3);
		g.addEdge(v3, v4);
		g.addEdge(v3, v6);
		g.addEdge(v2, v7);
		g.addEdge(v5, v7);
		g.addEdge(v6, v1);
		
		TCTree<Edge, Vertex> tc = new TCTree<Edge, Vertex>(g);
		
		for (TCTreeNode<Edge, Vertex> node:tc.getVertices()) {
			System.out.println(node.getName() + ": " + node.getSkeleton().getEdges());
		}
		
		assertEquals(0, tc.getVertices(TCType.R).size());
		assertEquals(3, tc.getVertices(TCType.B).size());
		assertEquals(4, tc.getVertices(TCType.P).size());
	}*/

}
