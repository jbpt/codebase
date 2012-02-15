package org.jbpt.alignment.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.jbpt.alignment.Alignment;
import org.jbpt.graph.DirectedGraph;
import org.jbpt.hypergraph.abs.Vertex;
import org.junit.Test;


public class AlignmentTest {
	
	DirectedGraph g1 = new DirectedGraph();
	DirectedGraph g2 = new DirectedGraph();

	@Test
	public void testAlignment() {
		g1 = new DirectedGraph();
		Vertex v1 = new Vertex("V1");
		Vertex v2 = new Vertex("V2");
		Vertex v3 = new Vertex("V3");
		Vertex v4 = new Vertex("V4");
		Vertex v5 = new Vertex("V5");
		Vertex v6 = new Vertex("V6");
		g1.addVertex(v1);
		g1.addVertex(v2);
		g1.addVertex(v3);
		g1.addVertex(v4);
		g1.addVertex(v5);
		g1.addVertex(v6);
		
		g1.addEdge(v1, v2);
		g1.addEdge(v2, v3);
		g1.addEdge(v2, v4);
		g1.addEdge(v4, v5);
		g1.addEdge(v3, v5);
		g1.addEdge(v5, v6);
		
		g2 = new DirectedGraph();
		Vertex v21 = new Vertex("V1");
		Vertex v22 = new Vertex("V2");
		Vertex v23 = new Vertex("V3");
		
		g2.addVertex(v21);
		g2.addVertex(v22);
		g2.addVertex(v23);
		
		g1.addEdge(v21, v22);
		g1.addEdge(v21, v23);
		
		Alignment<DirectedGraph, Vertex> a = new Alignment<DirectedGraph, Vertex>(g1,g2);
		
		assertTrue(a.getFirstModel().equals(g1));
		assertTrue(a.getSecondModel().equals(g2));

		a.addElementaryCorrespondence(v2, v22);
		
		assertTrue(a.getAlignedEntitiesOfFirstModel().size() == 1);
		assertTrue(a.getAlignedEntitiesOfFirstModel().contains(v2));

		assertTrue(a.getAlignedEntitiesOfSecondModel().size() == 1);
		assertTrue(a.getAlignedEntitiesOfSecondModel().contains(v22));

		a.addElementaryCorrespondence(v2, v22);

		assertTrue(a.getAlignedEntitiesOfFirstModel().size() == 1);
		assertTrue(a.getAlignedEntitiesOfFirstModel().contains(v2));

		assertTrue(a.getAlignedEntitiesOfSecondModel().size() == 1);
		assertTrue(a.getAlignedEntitiesOfSecondModel().contains(v22));
		
		assertTrue(a.getCorrespondingEntitiesForEntityOfFirstModel(v2).size() == 1);
		assertTrue(a.getCorrespondingEntitiesForEntityOfFirstModel(v2).contains(v22));

		assertTrue(a.getCorrespondingEntitiesForEntityOfSecondModel(v22).size() == 1);
		assertTrue(a.getCorrespondingEntitiesForEntityOfSecondModel(v22).contains(v2));

		assertTrue(a.isFunctional());
		assertTrue(a.isInjective());

		Set<Vertex> s1 = new HashSet<Vertex>();
		s1.add(v1);
		s1.add(v3);
		Set<Vertex> s2 = new HashSet<Vertex>();
		s2.add(v21);
		s2.add(v23);
		
		a.addComplexCorrespondence(s1,s2);
		
		assertTrue(a.getAlignedEntitiesOfFirstModel().size() == 3);
		assertTrue(a.getAlignedEntitiesOfFirstModel().contains(v1));
		assertTrue(a.getAlignedEntitiesOfFirstModel().contains(v2));
		assertTrue(a.getAlignedEntitiesOfFirstModel().contains(v3));

		assertTrue(a.getAlignedEntitiesOfSecondModel().size() == 3);
		assertTrue(a.getAlignedEntitiesOfSecondModel().contains(v21));
		assertTrue(a.getAlignedEntitiesOfSecondModel().contains(v22));
		assertTrue(a.getAlignedEntitiesOfSecondModel().contains(v23));

		assertTrue(a.getCorrespondingEntitiesForEntityOfFirstModel(v1).size() == 2);
		assertTrue(a.getCorrespondingEntitiesForEntityOfFirstModel(v1).contains(v21));
		assertTrue(a.getCorrespondingEntitiesForEntityOfFirstModel(v1).contains(v23));

		assertTrue(a.getCorrespondingEntitiesForEntityOfSecondModel(v21).size() == 2);
		assertTrue(a.getCorrespondingEntitiesForEntityOfSecondModel(v21).contains(v1));
		assertTrue(a.getCorrespondingEntitiesForEntityOfSecondModel(v21).contains(v3));
		
		assertTrue(a.isRightTotal());
		assertFalse(a.isLeftTotal());
		
		assertFalse(a.isFunctional());
		assertFalse(a.isInjective());
		
		assertFalse(a.isOverlapping());
		
		a.addElementaryCorrespondence(v5, v21);
		
		assertTrue(a.isOverlapping());
		
	}
}
