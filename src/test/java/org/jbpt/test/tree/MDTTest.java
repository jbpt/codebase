package org.jbpt.test.tree;


import junit.framework.TestCase;

import org.jbpt.algo.tree.mdt.IMDTNode;
import org.jbpt.algo.tree.mdt.MDT;
import org.jbpt.algo.tree.mdt.MDTType;
import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.DirectedGraph;
import org.jbpt.hypergraph.abs.Vertex;

public class MDTTest extends TestCase {

	public void test() {
		DirectedGraph graph = new DirectedGraph();
		
		Vertex a = new Vertex("a");
		Vertex b = new Vertex("b");
		Vertex c = new Vertex("c");
		Vertex d = new Vertex("d");
		Vertex e = new Vertex("e");
		
		graph.addEdge(a, b);
		graph.addEdge(a, c);
		graph.addEdge(a, d);
		graph.addEdge(a, e);
		
		graph.addEdge(b, d);
		graph.addEdge(c, e);
		
		MDT<DirectedEdge,Vertex> mdt = new MDT<DirectedEdge,Vertex>(graph);
		
		// RESULT: LINEAR[a, COMPLETE_0[LINEAR[d, b], LINEAR[e, c]]]
		assertTrue(mdt.getRoot().getType().equals(MDTType.LINEAR));
		System.out.println(mdt.getRoot());
		assertTrue(mdt.getChildren(mdt.getRoot()).size() == 2);
		
		IMDTNode<DirectedEdge, Vertex> trivial = null, complete0 = null;
		
		for (IMDTNode<DirectedEdge, Vertex> child: mdt.getChildren(mdt.getRoot())) {
			if (child.getType().equals(MDTType.TRIVIAL))
				trivial = child;
			else if (child.getType().equals(MDTType.COMPLETE))
				complete0 = child;
		}
		
		assertTrue(trivial != null && complete0 != null);
		assertTrue(complete0.getColor() == 0);
		assertTrue(mdt.getChildren(complete0).size() == 2);
	}

}
