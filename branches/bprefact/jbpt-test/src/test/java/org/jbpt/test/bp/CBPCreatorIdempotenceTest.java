package org.jbpt.test.bp;

import junit.framework.TestCase;

import org.jbpt.bp.construct.BPCreatorNet;
import org.jbpt.bp.construct.BPCreatorTree;
import org.jbpt.bp.construct.BPCreatorUnfolding;
import org.jbpt.bp.construct.CBPCreatorTree;
import org.jbpt.bp.construct.CBPCreatorUnfolding;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;

public class CBPCreatorIdempotenceTest extends TestCase {
	public void testIdempotenceOfCreatorClasses(){
		NetSystem net = new NetSystem();
		
		Transition a = new Transition("a");
		Transition b = new Transition("b");
		Transition c = new Transition("c");
		Transition d = new Transition("d");
		Transition e = new Transition("e");
		Transition f = new Transition("f");
		Transition g = new Transition("g");
		Transition h = new Transition("h");
		Transition i = new Transition("i");
		Transition j = new Transition("j");

		net.addNode(a);
		net.addNode(b);
		net.addNode(c);
		net.addNode(d);
		net.addNode(e);
		net.addNode(f);
		net.addNode(g);
		net.addNode(h);
		net.addNode(i);
		net.addNode(j);

		Place p1 = new Place("1");
		Place p2 = new Place("2");
		Place p3 = new Place("3");
		Place p4 = new Place("4");
		Place p5 = new Place("5");
		Place p6 = new Place("6");
		Place p7 = new Place("7");
		Place p8 = new Place("8");
		Place p9 = new Place("9");
		Place p10 = new Place("10");
		
		net.addNode(p1);
		net.addNode(p2);
		net.addNode(p3);
		net.addNode(p4);
		net.addNode(p5);
		net.addNode(p6);
		net.addNode(p7);
		net.addNode(p8);
		net.addNode(p9);
		net.addNode(p10);
		
		net.putTokens(p1,1);
		
		net.addFlow(p1, a);
		net.addFlow(a, p2);
		net.addFlow(a, p3);
		net.addFlow(p2, b);
		net.addFlow(p3, c);
		net.addFlow(b, p4);
		net.addFlow(c, p5);
		net.addFlow(p5, d);
		net.addFlow(p5, e);
		net.addFlow(d, p6);
		net.addFlow(e, p6);
		net.addFlow(p6, f);
		net.addFlow(p4, f);
		net.addFlow(f, p7);
		net.addFlow(p7, g);
		net.addFlow(g, p8);
		net.addFlow(p8, h);
		net.addFlow(h, p9);
		net.addFlow(p9, i);
		net.addFlow(i, p7);
		net.addFlow(p9, j);
		net.addFlow(j, p10);

		/*
		 * Checks for net
		 */
		assertTrue(net.getTransitions().size() == 10);
		assertTrue(net.getTransitions().contains(a));
		assertTrue(net.getTransitions().contains(b));
		assertTrue(net.getTransitions().contains(c));
		assertTrue(net.getTransitions().contains(d));
		assertTrue(net.getTransitions().contains(e));
		assertTrue(net.getTransitions().contains(f));
		assertTrue(net.getTransitions().contains(g));
		assertTrue(net.getTransitions().contains(h));
		assertTrue(net.getTransitions().contains(i));
		assertTrue(net.getTransitions().contains(j));

		assertTrue(net.getPlaces().size() == 10);
		assertTrue(net.getPlaces().contains(p1));
		assertTrue(net.getPlaces().contains(p2));
		assertTrue(net.getPlaces().contains(p3));
		assertTrue(net.getPlaces().contains(p4));
		assertTrue(net.getPlaces().contains(p5));
		assertTrue(net.getPlaces().contains(p6));
		assertTrue(net.getPlaces().contains(p7));
		assertTrue(net.getPlaces().contains(p8));
		assertTrue(net.getPlaces().contains(p9));
		assertTrue(net.getPlaces().contains(p10));

		assertTrue(net.getEdges().size() == 22);
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, p1, a));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, a, p2));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, a, p3));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, p2, b));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, p3, c));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, b, p4));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, c, p5));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, p5, d));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, p5, e));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, d, p6));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, e, p6));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, p6, f));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, p4, f));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, f, p7));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, p7, g));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, g, p8));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, p8, h));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, h, p9));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, p9, i));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, i, p7));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, p9, j));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, j, p10));
		
		/*
		 * Invoke all creator classes on net, except for CBPCreatorNet since
		 * the net does not meet the respective requirements 
		 */
		BPCreatorNet.getInstance().deriveRelationSet(net);
		BPCreatorTree.getInstance().deriveRelationSet(net);
		BPCreatorUnfolding.getInstance().deriveRelationSet(net);
		CBPCreatorTree.getInstance().deriveCausalBehaviouralProfile(net);
		CBPCreatorUnfolding.getInstance().deriveCausalBehaviouralProfile(net);

		/*
		 * Repeat all checks for net
		 */
		assertTrue(net.getTransitions().size() == 10);
		assertTrue(net.getTransitions().contains(a));
		assertTrue(net.getTransitions().contains(b));
		assertTrue(net.getTransitions().contains(c));
		assertTrue(net.getTransitions().contains(d));
		assertTrue(net.getTransitions().contains(e));
		assertTrue(net.getTransitions().contains(f));
		assertTrue(net.getTransitions().contains(g));
		assertTrue(net.getTransitions().contains(h));
		assertTrue(net.getTransitions().contains(i));
		assertTrue(net.getTransitions().contains(j));

		assertTrue(net.getPlaces().size() == 10);
		assertTrue(net.getPlaces().contains(p1));
		assertTrue(net.getPlaces().contains(p2));
		assertTrue(net.getPlaces().contains(p3));
		assertTrue(net.getPlaces().contains(p4));
		assertTrue(net.getPlaces().contains(p5));
		assertTrue(net.getPlaces().contains(p6));
		assertTrue(net.getPlaces().contains(p7));
		assertTrue(net.getPlaces().contains(p8));
		assertTrue(net.getPlaces().contains(p9));
		assertTrue(net.getPlaces().contains(p10));

		assertTrue(net.getEdges().size() == 22);
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, p1, a));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, a, p2));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, a, p3));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, p2, b));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, p3, c));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, b, p4));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, c, p5));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, p5, d));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, p5, e));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, d, p6));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, e, p6));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, p6, f));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, p4, f));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, f, p7));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, p7, g));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, g, p8));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, p8, h));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, h, p9));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, p9, i));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, i, p7));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, p9, j));
		assertTrue(PetriNet.DIRECTED_GRAPH_ALGORITHMS.hasPath(net, j, p10));
		

	}
	
}
