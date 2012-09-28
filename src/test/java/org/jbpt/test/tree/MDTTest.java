package org.jbpt.test.tree;


import junit.framework.TestCase;

import org.jbpt.algo.tree.mdt.MDT;
import org.jbpt.algo.tree.mdt.MDTNode;
import org.jbpt.algo.tree.mdt.MDTNode.NodeType;
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
		
		MDT mdt = new MDT(graph);
		
		// RESULT: LINEAR[a, COMPLETE_0[LINEAR[d, b], LINEAR[e, c]]]
		assertTrue(mdt.getRoot().getType().equals(NodeType.LINEAR));
		assertTrue(mdt.getRoot().getChildren().size() == 2);
		
		MDTNode trivial = null, complete0 = null;
		
		for (MDTNode child: mdt.getRoot().getChildren()) {
			if (child.getType().equals(NodeType.TRIVIAL))
				trivial = child;
			else if (child.getType().equals(NodeType.COMPLETE))
				complete0 = child;
		}
		
		assertTrue(trivial != null && complete0 != null);
		assertTrue(complete0.getColor() == 0);
		assertTrue(complete0.getChildren().size() == 2);
	}

}
