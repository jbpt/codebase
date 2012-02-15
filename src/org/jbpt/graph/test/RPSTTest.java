package org.jbpt.graph.test;

import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.DirectedGraph;
import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.graph.algo.rpst.RPST;
import org.jbpt.graph.algo.rpst.RPSTNode;
import org.jbpt.graph.algo.tctree.TCType;
import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.pm.Activity;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.Gateway;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.XorGateway;

import junit.framework.TestCase;

public class RPSTTest extends TestCase {
	
	public void testTrivialGraph2() {
		System.out.println("========================================================");
		System.out.println("Trivial Graph");
		System.out.println("========================================================");
		
		DirectedGraph g = new DirectedGraph();
		
		Vertex v1 = new Vertex("1");
		Vertex v2 = new Vertex("2");
		
		g.addEdge(v1, v2);
		
		RPST<DirectedEdge,Vertex> rpst = new RPST<DirectedEdge,Vertex>(g);
		
		System.out.println(rpst);
		
		for (RPSTNode<DirectedEdge,Vertex> node : rpst.getVertices()) {
			System.out.println(node.getName() + " : " + node.getFragment());
		}
		
		for (IDirectedEdge<Vertex> edge: rpst.getRoot().getFragmentEdges())
			System.out.println(edge);
	}
		
	public void testTrivialGraph() {
		System.out.println("========================================================");
		System.out.println("Trivial Graph");
		System.out.println("========================================================");
		
		DirectedGraph g = new DirectedGraph();
		
		Vertex v1 = new Vertex("1");
		Vertex v2 = new Vertex("2");
		
		g.addEdge(v1, v2);
		
		RPST<DirectedEdge,Vertex> rpst = new RPST<DirectedEdge,Vertex>(g);
		
		System.out.println(rpst);
		
		for (RPSTNode<DirectedEdge,Vertex> node : rpst.getVertices()) {
			System.out.println(node.getName() + " : " + node.getFragment());
		}
	}
	
	public void testBPM08Fig11() {
		System.out.println("========================================================");
		System.out.println("BPM08 Fig.11");
		System.out.println("========================================================");
		
		DirectedGraph g = new DirectedGraph();
		
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
		
		System.out.println(rpst);
		
		for (RPSTNode<DirectedEdge,Vertex> node : rpst.getVertices()) {
			System.out.println(node.getName() + " : " + node.getFragment());
		}
	}
	
	public void testSimpleGraph() {
		System.out.println("========================================================");
		System.out.println("Simple Graph");
		System.out.println("========================================================");
		
		DirectedGraph g = new DirectedGraph();
		
		Vertex s = new Vertex("s");
		Vertex t = new Vertex("t");
		Vertex y = new Vertex("y");
		Vertex z = new Vertex("z");
		
		g.addVertex(s);
		g.addVertex(t);
		g.addVertex(y);
		g.addVertex(z);
		
		g.addEdge(s,y);
		g.addEdge(y,z);
		g.addEdge(z,y);
		g.addEdge(z,t);
		
		System.out.println(g);
		
		RPST<DirectedEdge,Vertex> rpst = new RPST<DirectedEdge,Vertex>(g);
		
		System.out.println(rpst);
		
		for (RPSTNode<DirectedEdge,Vertex> node : rpst.getVertices()) {
			System.out.println(node.getName() + " : " + node.getFragment());
		}
	}
	
	
	public void testBondsTest() {
		
		System.out.println("========================================================");
		System.out.println("Bonds test");
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
		
		RPST<ControlFlow<FlowNode>,FlowNode> rpst = new RPST<ControlFlow<FlowNode>,FlowNode>(p);
		
		System.out.println(rpst);
		
		assertEquals(rpst.getVertices().size(), 17);
		assertEquals(rpst.getEdges().size(), 16);
		assertEquals(rpst.getVertices(TCType.B).size(), 2);
		assertEquals(rpst.getVertices(TCType.R).size(), 0);
		assertEquals(rpst.getVertices(TCType.P).size(), 4);
		assertEquals(rpst.getVertices(TCType.T).size(), 11);
	}
}
