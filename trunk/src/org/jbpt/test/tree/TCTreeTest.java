package org.jbpt.test.tree;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.jbpt.algo.tree.tctree.TCTree;
import org.jbpt.algo.tree.tctree.TCTreeNode;
import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.Edge;
import org.jbpt.graph.MultiDirectedGraph;
import org.jbpt.graph.MultiGraph;
import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.utils.IOUtils;

public class TCTreeTest extends TestCase {
	
	/**
	 * Test of a graph from the WS-FM'10 paper:
	 * Artem Polyvyanyy, Jussi Vanhatalo, and Hagen Völzer: 
	 * Simplified Computation and Generalization of the Refined Process Structure Tree. WS-FM 2010: 25-41
	 */
	public void testWSFM() {
		MultiDirectedGraph g = new MultiDirectedGraph();
		
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
		DirectedEdge backEdge = g.addEdge(t,s);
		
		IOUtils.toFile("graph.dot", g.toDOT());
		
		long start = System.nanoTime();
		TCTree<DirectedEdge,Vertex> tctree = new TCTree<DirectedEdge,Vertex>(g,backEdge);
		long end = System.nanoTime();
		System.out.println("WSFM\t"+((double) end-start) / 1000000000);
		
		Set<DirectedEdge> edges = new HashSet<DirectedEdge>();
		for (TCTreeNode<DirectedEdge,Vertex> node : tctree.getVertices()) {
			if (node.getType()==TCType.POLYGON) {
				assertEquals(6, node.getSkeleton().getVertices().size());
				assertEquals(4, node.getSkeleton().getOriginalEdges().size());
				assertEquals(2, node.getSkeleton().getVirtualEdges().size());
			}
				
			if (node.getType()==TCType.BOND) {
				assertEquals(2, node.getSkeleton().getVertices().size());
				assertEquals(3, node.getSkeleton().getOriginalEdges().size());
				assertEquals(1, node.getSkeleton().getVirtualEdges().size());
			}
			
			if (node.getType()==TCType.RIGID) {
				assertEquals(4, node.getSkeleton().getVertices().size());
				assertEquals(5, node.getSkeleton().getOriginalEdges().size());
				assertEquals(1, node.getSkeleton().getVirtualEdges().size());
			}
			
			assertEquals(true,g.getEdges().containsAll(node.getSkeleton().getOriginalEdges()));
			edges.addAll((node.getSkeleton().getOriginalEdges()));
			
			IOUtils.toFile(node.getName() + ".dot",node.getSkeleton().toDOT());
		}
		
		assertEquals(true,edges.containsAll(g.getEdges()));
		assertEquals(true,g.getEdges().containsAll(edges));
		assertEquals(3,tctree.getTCTreeNodes().size());
		assertEquals(1,tctree.getTCTreeNodes(TCType.BOND).size());
		assertEquals(1,tctree.getTCTreeNodes(TCType.RIGID).size());
		assertEquals(1,tctree.getTCTreeNodes(TCType.POLYGON).size());
		
		IOUtils.toFile("tree.dot", tctree.toDOT());
	}
	
	public void testNULL() {
		MultiDirectedGraph g = null;
		long start = System.nanoTime();
		TCTree<DirectedEdge,Vertex> tctree = new TCTree<DirectedEdge,Vertex>(g);
		long end = System.nanoTime();
		System.out.println("NULL\t"+((double) end-start) / 1000000000);
		
		assertEquals(0,tctree.getTCTreeNodes().size());
	}
	
	public void testSingleVertex() {
		MultiDirectedGraph g = new MultiDirectedGraph();
		g.addVertex(new Vertex("A"));
		long start = System.nanoTime();
		TCTree<DirectedEdge,Vertex> tctree = new TCTree<DirectedEdge,Vertex>(g);
		long end = System.nanoTime();
		System.out.println("1V\t"+((double) end-start) / 1000000000);
		
		assertEquals(0,tctree.getTCTreeNodes().size());
	}
	
	public void testSingleEdge() {
		MultiDirectedGraph g = new MultiDirectedGraph();
		g.addEdge(new Vertex("A"),new Vertex("B"));
		long start = System.nanoTime();
		TCTree<DirectedEdge,Vertex> tctree = new TCTree<DirectedEdge,Vertex>(g);
		long end = System.nanoTime();
		System.out.println("1E\t"+((double) end-start) / 1000000000);
		
		assertEquals(0,tctree.getTCTreeNodes().size());
	}
	
	public void testSingleBond() {
		MultiGraph g = new MultiGraph();
		Vertex a = new Vertex("A");
		Vertex b = new Vertex("B");
		g.addEdge(a,b);
		g.addEdge(a,b);
		g.addEdge(a,b);
		g.addEdge(a,b);
		g.addEdge(a,b);
		long start = System.nanoTime();
		TCTree<Edge,Vertex> tctree = new TCTree<Edge,Vertex>(g);
		long end = System.nanoTime();
		System.out.println("1BOND\t"+((double) end-start) / 1000000000);
		
		assertEquals(1,tctree.getTCTreeNodes().size());
		assertEquals(1,tctree.getTCTreeNodes(TCType.BOND).size());
		for (TCTreeNode<Edge,Vertex> node : tctree.getVertices()) {
			IOUtils.toFile(node.getName() + ".dot",node.getSkeleton().toDOT());
		}
	}
	
	public void testSingleBondAndSingleVertex() {
		MultiGraph g = new MultiGraph();
		Vertex a = new Vertex("A");
		Vertex b = new Vertex("B");
		g.addEdge(a,b);
		g.addEdge(a,b);
		g.addEdge(a,b);
		g.addEdge(a,b);
		g.addEdge(a,b);
		g.addVertex(new Vertex("C"));
		long start = System.nanoTime();
		TCTree<Edge,Vertex> tctree = new TCTree<Edge,Vertex>(g);
		long end = System.nanoTime();
		System.out.println("1B1V\t"+((double) end-start) / 1000000000);
		
		assertEquals(1,tctree.getTCTreeNodes().size());
		assertEquals(1,tctree.getTCTreeNodes(TCType.BOND).size());
		for (TCTreeNode<Edge,Vertex> node : tctree.getVertices()) {
			IOUtils.toFile(node.getName() + ".dot",node.getSkeleton().toDOT());
		}
	}
	
	public void testSimpleGraph() {
		//		  --- t3 --- t4 ---
		//		  |				  |
		// t1 -- s2 ------------ j5 -- t9
		//	.	  |				  |		.
		//	.	  |_ s6 ---- j7 __|		.
		// 	.		  |_ t8 _|			.
		//	............................. 
		
		MultiDirectedGraph g = new MultiDirectedGraph();
		
		Vertex t1 = new Vertex("1");
		Vertex t3 = new Vertex("3");
		Vertex t4 = new Vertex("4");
		Vertex t8 = new Vertex("8");
		Vertex t9 = new Vertex("9");
		
		Vertex s2 = new Vertex("2");
		Vertex s6 = new Vertex("6");
		Vertex j7 = new Vertex("7");
		Vertex j5 = new Vertex("5");
		
		g.addEdge(t1, s2);
		g.addEdge(s2, t3);
		g.addEdge(s2, s6);
		g.addEdge(s2, j5);
		g.addEdge(t3, t4);
		g.addEdge(t4, j5);
		g.addEdge(s6, j7);
		g.addEdge(s6, t8);
		g.addEdge(t8, j7);
		g.addEdge(j7, j5);
		g.addEdge(j5, t9);
		DirectedEdge backEdge = g.addEdge(t9, t1);
		
		IOUtils.toFile("graph.dot", g.toDOT());
		long start = System.nanoTime();
		TCTree<DirectedEdge,Vertex> tctree = new TCTree<DirectedEdge,Vertex>(g,backEdge);
		long end = System.nanoTime();
		System.out.println("2B4P\t"+((double) end-start) / 1000000000);
		IOUtils.toFile("graph2.dot", g.toDOT());
		IOUtils.toFile("tree.dot", tctree.toDOT());
		
		Set<DirectedEdge> edges = new HashSet<DirectedEdge>();
		for (TCTreeNode<DirectedEdge,Vertex> node : tctree.getTCTreeNodes()) {
			assertEquals(true,g.getEdges().containsAll(node.getSkeleton().getOriginalEdges()));
			edges.addAll((node.getSkeleton().getOriginalEdges()));
			
			if (node.getType()==TCType.BOND) {
				assertEquals(2, node.getSkeleton().getVertices().size());
			}
			
			IOUtils.toFile(node.getName() + ".dot",node.getSkeleton().toDOT());
		}
		
		assertEquals(true,edges.containsAll(g.getEdges()));
		assertEquals(true,g.getEdges().containsAll(edges));
		
		assertEquals(6,tctree.getTCTreeNodes().size());
		assertEquals(2,tctree.getTCTreeNodes(TCType.BOND).size());
		assertEquals(0,tctree.getTCTreeNodes(TCType.RIGID).size());
		assertEquals(4,tctree.getTCTreeNodes(TCType.POLYGON).size());
	}
}
