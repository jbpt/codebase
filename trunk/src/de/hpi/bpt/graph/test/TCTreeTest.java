package de.hpi.bpt.graph.test;

import junit.framework.TestCase;
import de.hpi.bpt.graph.algo.tctree.TCTree;
import de.hpi.bpt.graph.algo.tctree.TCTreeNode;
import de.hpi.bpt.graph.algo.tctree.TCType;
import de.hpi.bpt.process.Activity;
import de.hpi.bpt.process.ControlFlow;
import de.hpi.bpt.process.FlowNode;
import de.hpi.bpt.process.Gateway;
import de.hpi.bpt.process.ProcessModel;
import de.hpi.bpt.process.XorGateway;

public class TCTreeTest extends TestCase {
		
	public void testSimpleGraph() {
		
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
		
		Gateway s2 = new XorGateway("2");
		Gateway s6 = new XorGateway("6");
		Gateway j7 = new XorGateway("7");
		Gateway j5 = new XorGateway("5");
		
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
		
		TCTree<ControlFlow<FlowNode>,FlowNode> tc = new TCTree<ControlFlow<FlowNode>,FlowNode>(p,backEdge);
		
		/*for (TCTreeNode<ControlFlow, Node> n:tc.getVertices()) {
			System.out.println(String.valueOf(n) + ": " + n.getSkeleton().getEdges());
			System.out.println(String.valueOf(n) + ": " + n.getSkeleton().getVirtualEdges());
		}*/
		for (TCTreeNode<ControlFlow<FlowNode>, FlowNode> n:tc.getVertices()) {
			System.out.println(n + ": " + n.getSkeleton().getEdges());
			System.out.println(n + ": " + n.getSkeleton().getVirtualEdges());
			System.out.println(n + ": " + n.getSkeleton().getESMap());
		}
		//System.out.println(tc);
		
		assertEquals(tc.getVertices().size(), 18);
		assertEquals(tc.getEdges().size(), 17);
		assertEquals(tc.getVertices(TCType.B).size(), 2);
		assertEquals(tc.getVertices(TCType.R).size(), 0);
		assertEquals(tc.getVertices(TCType.P).size(), 4);
		assertEquals(tc.getVertices(TCType.T).size(), 12);
	}
	
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
		Process p = new Process();
		
		Task t1 = new Task("1");
		Task t3 = new Task("3");
		Task t4 = new Task("4");
		Task t10 = new Task("10");

		Gateway s2 = new Gateway(GatewayType.XOR, "2");
		Gateway s6 = new Gateway(GatewayType.XOR, "6");
		Gateway s7 = new Gateway(GatewayType.XOR, "7");
		Gateway j5 = new Gateway(GatewayType.XOR, "5");
		Gateway j8 = new Gateway(GatewayType.XOR, "8");
		Gateway j9 = new Gateway(GatewayType.XOR, "9");
		
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
		
		TCTree<ControlFlow, Node> tc = new TCTree<ControlFlow, Node>(p);
		
		//assertEquals(tc.getVertices().size(), 5);
		//assertEquals(tc.getEdges().size(), 4);
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
		
		Process p = new Process();
		
		Task t1 = new Task("n1");
		Task t5 = new Task("n5");
		Task t7 = new Task("n7");

		Gateway s2 = new Gateway(GatewayType.XOR, "n2");
		Gateway j6 = new Gateway(GatewayType.XOR, "n6");
		Gateway s3 = new Gateway(GatewayType.XOR, "n3");
		Gateway j4 = new Gateway(GatewayType.XOR, "n4");
		
		p.addControlFlow(t1, s2);
		p.addControlFlow(s2, s3);
		p.addControlFlow(s2, j4);
		p.addControlFlow(s3, j4);
		p.addControlFlow(s3, j6);
		p.addControlFlow(j4, t5);
		p.addControlFlow(t5, j6);
		p.addControlFlow(j6, t7);
		ControlFlow backEdge = p.addControlFlow(t7, t1);
		
		TCTree<ControlFlow, Node> tc = new TCTree<ControlFlow, Node>(p, backEdge);
		
		for (TCTreeNode<ControlFlow, Node> n:tc.getVertices()) {
			System.out.println(String.valueOf(n) + ": " + n.getSkeleton().getEdges());
			System.out.println(String.valueOf(n) + ": " + n.getSkeleton().getVirtualEdges());
		}
		
		assertEquals(3, tc.getVertices().size());
		assertEquals(2, tc.getEdges().size());
		assertEquals(0, tc.getVertices(TCType.B).size());
		assertEquals(1, tc.getVertices(TCType.R).size());
		assertEquals(2, tc.getVertices(TCType.P).size());
	}
	
	public void testGraphWithR2() {
		// create process model graph
		Process p = new Process();
		
		Task t1 = new Task("1");
		Task t3 = new Task("3");
		Task t4 = new Task("4");
		Task t10 = new Task("10");
		Task t11 = new Task("11");

		Gateway s2 = new Gateway(GatewayType.XOR, "2");
		Gateway s6 = new Gateway(GatewayType.XOR, "6");
		Gateway s7 = new Gateway(GatewayType.XOR, "7");
		Gateway j5 = new Gateway(GatewayType.XOR, "5");
		Gateway j8 = new Gateway(GatewayType.XOR, "8");
		Gateway j9 = new Gateway(GatewayType.XOR, "9");
		
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
		
		TCTree<ControlFlow, Node> tc = new TCTree<ControlFlow, Node>(p, backEdge);
		
		for (TCTreeNode<ControlFlow, Node> n:tc.getVertices()) {
			System.out.println(String.valueOf(n) + ": " + n.getSkeleton().getEdges());
			System.out.println(String.valueOf(n) + ": " + n.getSkeleton().getVirtualEdges());
		}
		
		assertEquals(tc.getVertices().size(), 6);
		assertEquals(tc.getEdges().size(), 5);
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
		
		Process p = new Process();
		
		Task t1 = new Task("T1");
		Task t2 = new Task("T2");
		Task t3 = new Task("T3");
		
		Gateway s1 = new Gateway(GatewayType.XOR, "S1");
		Gateway s2 = new Gateway(GatewayType.XOR, "S2");
		Gateway s3 = new Gateway(GatewayType.XOR, "S3");
		Gateway j1 = new Gateway(GatewayType.XOR, "J1");
		Gateway j2 = new Gateway(GatewayType.XOR, "J2");
		
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
		
		TCTree<ControlFlow, Node> tc = new TCTree<ControlFlow, Node>(p);
		
		//assertEquals(tc.getVertices().size(), 5);
		//assertEquals(tc.getEdges().size(), 4);
		assertEquals(3, tc.getVertices(TCType.P).size());
		assertEquals(1, tc.getVertices(TCType.B).size());
		assertEquals(1, tc.getVertices(TCType.R).size());
		
	}
	
	public void testSomeBehavior() {
		// create process model graph
		Process p = new Process();
		
		Task t1 = new Task("T1");
		Task t2 = new Task("T2");
		Task t3 = new Task("T3");
		Task t4 = new Task("T4");
		Task t5 = new Task("T5");
		Task t6 = new Task("T6");
		Task t7 = new Task("T7");
		Task t8 = new Task("T8");
		Task t9 = new Task("T9");
		Task t10 = new Task("T10");
		Task t11 = new Task("T11");
		Task t12 = new Task("T12");
		Task t13 = new Task("T13");
		Task t14 = new Task("T14");
		
		Gateway s1 = new Gateway(GatewayType.XOR,"S1");
		Gateway s2 = new Gateway(GatewayType.XOR,"S2");
		Gateway s3 = new Gateway(GatewayType.XOR,"S3");
		Gateway j1 = new Gateway(GatewayType.XOR,"J1");
		Gateway j2 = new Gateway(GatewayType.XOR,"J2");
		Gateway j3 = new Gateway(GatewayType.XOR,"J3");
		
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
		
		TCTree<ControlFlow, Node> tc = new TCTree<ControlFlow, Node>(p);
		
		for (TCTreeNode<ControlFlow, Node> n:tc.getVertices()) {
			System.out.println(String.valueOf(n) + ": " + n.getSkeleton().getEdges());
			System.out.println(String.valueOf(n) + ": " + n.getBoundaryNodes());
		}
		
		System.out.println("Vertices: " + tc.countVertices());
		System.out.println("Edges: " + tc.countEdges());
		
		assertEquals(3, tc.getVertices(TCType.B).size());
		assertEquals(1, tc.getVertices(TCType.R).size());
		assertEquals(10, tc.getVertices(TCType.P).size());
		
		assertEquals(14, tc.getVertices().size());
	}
	
	public void testOneMoreComplexExample() {
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
