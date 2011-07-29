package de.hpi.bpt.process.petri.bp.test;

import junit.framework.TestCase;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;
import de.hpi.bpt.process.petri.bp.construct.BPCreatorNet;
import de.hpi.bpt.process.petri.bp.construct.BPCreatorTree;
import de.hpi.bpt.process.petri.bp.construct.BPCreatorUnfolding;
import de.hpi.bpt.process.petri.bp.construct.CBPCreatorTree;
import de.hpi.bpt.process.petri.bp.construct.CBPCreatorUnfolding;

public class CBPCreatorIdempotenceTest extends TestCase {
	public void testIdempotenceOfCreatorClasses(){
		PetriNet net = new PetriNet();
		
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
		p1.setTokens(1);
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
		assertTrue(net.hasPath(p1, a));
		assertTrue(net.hasPath(a, p2));
		assertTrue(net.hasPath(a, p3));
		assertTrue(net.hasPath(p2, b));
		assertTrue(net.hasPath(p3, c));
		assertTrue(net.hasPath(b, p4));
		assertTrue(net.hasPath(c, p5));
		assertTrue(net.hasPath(p5, d));
		assertTrue(net.hasPath(p5, e));
		assertTrue(net.hasPath(d, p6));
		assertTrue(net.hasPath(e, p6));
		assertTrue(net.hasPath(p6, f));
		assertTrue(net.hasPath(p4, f));
		assertTrue(net.hasPath(f, p7));
		assertTrue(net.hasPath(p7, g));
		assertTrue(net.hasPath(g, p8));
		assertTrue(net.hasPath(p8, h));
		assertTrue(net.hasPath(h, p9));
		assertTrue(net.hasPath(p9, i));
		assertTrue(net.hasPath(i, p7));
		assertTrue(net.hasPath(p9, j));
		assertTrue(net.hasPath(j, p10));
		
		/*
		 * Invoke all creator classes on net, except for CBPCreatorNet since
		 * the net does not meet the respective requirements 
		 */
		BPCreatorNet.getInstance().deriveBehaviouralProfile(net);
		BPCreatorTree.getInstance().deriveBehaviouralProfile(net);
		BPCreatorUnfolding.getInstance().deriveBehaviouralProfile(net);
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
		assertTrue(net.hasPath(p1, a));
		assertTrue(net.hasPath(a, p2));
		assertTrue(net.hasPath(a, p3));
		assertTrue(net.hasPath(p2, b));
		assertTrue(net.hasPath(p3, c));
		assertTrue(net.hasPath(b, p4));
		assertTrue(net.hasPath(c, p5));
		assertTrue(net.hasPath(p5, d));
		assertTrue(net.hasPath(p5, e));
		assertTrue(net.hasPath(d, p6));
		assertTrue(net.hasPath(e, p6));
		assertTrue(net.hasPath(p6, f));
		assertTrue(net.hasPath(p4, f));
		assertTrue(net.hasPath(f, p7));
		assertTrue(net.hasPath(p7, g));
		assertTrue(net.hasPath(g, p8));
		assertTrue(net.hasPath(p8, h));
		assertTrue(net.hasPath(h, p9));
		assertTrue(net.hasPath(p9, i));
		assertTrue(net.hasPath(i, p7));
		assertTrue(net.hasPath(p9, j));
		assertTrue(net.hasPath(j, p10));
		

	}
	
}
