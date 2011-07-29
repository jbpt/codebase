package de.hpi.bpt.process.petri.bp.test;

import junit.framework.TestCase;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;
import de.hpi.bpt.process.petri.bp.BehaviouralProfile;
import de.hpi.bpt.process.petri.bp.CausalBehaviouralProfile;
import de.hpi.bpt.process.petri.bp.BehaviouralProfile.CharacteristicRelationType;
import de.hpi.bpt.process.petri.bp.construct.BPCreatorNet;
import de.hpi.bpt.process.petri.bp.construct.BPCreatorTree;
import de.hpi.bpt.process.petri.bp.construct.BPCreatorUnfolding;
import de.hpi.bpt.process.petri.bp.construct.CBPCreatorTree;
import de.hpi.bpt.process.petri.bp.construct.CBPCreatorUnfolding;

public class CBPComputationTest extends TestCase {

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
	

	public void testCBPComputation1(){
		PetriNet net = new PetriNet();
		
		Transition a = new Transition("a");
		Transition b = new Transition("b");
		Transition c = new Transition("c");
		Transition d = new Transition("d");
		Transition e = new Transition("e");

		net.addNode(a);
		net.addNode(b);
		net.addNode(c);
		net.addNode(d);
		net.addNode(e);

		Place p1 = new Place("1");
		p1.setTokens(1);
		Place p2 = new Place("2");
		Place p3 = new Place("3");
		Place p4 = new Place("4");
		
		net.addNode(p1);
		net.addNode(p2);
		net.addNode(p3);
		net.addNode(p4);
		
		net.addFlow(p1, a);
		net.addFlow(p1, b);
		net.addFlow(a, p2);
		net.addFlow(b, p2);
		net.addFlow(p2, c);
		net.addFlow(c, p3);
		net.addFlow(p3, d);
		net.addFlow(d, p2);
		net.addFlow(p3, e);
		net.addFlow(e, p4);
		
		CausalBehaviouralProfile cbp = CBPCreatorUnfolding.getInstance().deriveCausalBehaviouralProfile(net);

		assertTrue(cbp.areCooccurring(a, a));
		assertFalse(cbp.areCooccurring(a, b));
		assertTrue(cbp.areCooccurring(a, c));
		assertFalse(cbp.areCooccurring(a, d));
		assertTrue(cbp.areCooccurring(a, e));
		
		assertFalse(cbp.areCooccurring(b, a));
		assertTrue(cbp.areCooccurring(b, b));
		assertTrue(cbp.areCooccurring(b, c));
		assertFalse(cbp.areCooccurring(b, d));
		assertTrue(cbp.areCooccurring(b, e));

		assertFalse(cbp.areCooccurring(c, a));
		assertFalse(cbp.areCooccurring(c, b));
		assertTrue(cbp.areCooccurring(c, c));
		assertFalse(cbp.areCooccurring(c, d));
		assertTrue(cbp.areCooccurring(c, e));

		assertFalse(cbp.areCooccurring(d, a));
		assertFalse(cbp.areCooccurring(d, b));
		assertTrue(cbp.areCooccurring(d, c));
		assertTrue(cbp.areCooccurring(d, d));
		assertTrue(cbp.areCooccurring(d, e));

		assertFalse(cbp.areCooccurring(e, a));
		assertFalse(cbp.areCooccurring(e, b));
		assertTrue(cbp.areCooccurring(e, c));
		assertFalse(cbp.areCooccurring(e, d));
		assertTrue(cbp.areCooccurring(e, e));
	}

	public void testCBPComputation2(){
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
		
		CausalBehaviouralProfile cbp = CBPCreatorUnfolding.getInstance().deriveCausalBehaviouralProfile(net);

		assertTrue(cbp.areCooccurring(a, a));
		assertTrue(cbp.areCooccurring(b, b));
		assertTrue(cbp.areCooccurring(c, c));
		assertTrue(cbp.areCooccurring(a, b));
		assertTrue(cbp.areCooccurring(b, a));
		assertTrue(cbp.areCooccurring(a, c));
		assertTrue(cbp.areCooccurring(c, a));

		assertTrue(cbp.areCooccurring(d, a));
		assertTrue(cbp.areCooccurring(d, b));
		assertTrue(cbp.areCooccurring(d, c));
		assertTrue(cbp.areCooccurring(e, a));
		assertTrue(cbp.areCooccurring(e, b));
		assertTrue(cbp.areCooccurring(e, c));

		assertFalse(cbp.areCooccurring(d, e));
		assertFalse(cbp.areCooccurring(e, d));

		assertFalse(cbp.areCooccurring(a, d));
		assertFalse(cbp.areCooccurring(b, d));
		assertFalse(cbp.areCooccurring(c, d));
		assertFalse(cbp.areCooccurring(a, e));
		assertFalse(cbp.areCooccurring(b, e));
		assertFalse(cbp.areCooccurring(c, e));

		assertTrue(cbp.areCooccurring(b, f));
		assertTrue(cbp.areCooccurring(b, g));
		assertTrue(cbp.areCooccurring(b, h));
		assertTrue(cbp.areCooccurring(b, j));
		assertTrue(cbp.areCooccurring(j, b));

		assertTrue(cbp.areCooccurring(g, h));
		assertTrue(cbp.areCooccurring(h, g));
		
		assertTrue(cbp.areCooccurring(d, h));
		assertTrue(cbp.areCooccurring(e, h));
		assertTrue(cbp.areCooccurring(d, j));
		assertTrue(cbp.areCooccurring(e, j));

		assertFalse(cbp.areCooccurring(g, e));
		assertFalse(cbp.areCooccurring(h, e));
		assertFalse(cbp.areCooccurring(g, d));
		assertFalse(cbp.areCooccurring(h, d));
		
		assertTrue(cbp.areCooccurring(i, a));
		assertTrue(cbp.areCooccurring(i, b));
		assertTrue(cbp.areCooccurring(i, g));
		assertTrue(cbp.areCooccurring(i, j));
	
		assertFalse(cbp.areCooccurring(i, e));
		assertFalse(cbp.areCooccurring(i, d));
		assertFalse(cbp.areCooccurring(a, i));
		assertFalse(cbp.areCooccurring(b, i));
		assertFalse(cbp.areCooccurring(e, i));
		assertFalse(cbp.areCooccurring(g, i));
		assertFalse(cbp.areCooccurring(j, i));

	}
	
	public void testCBPComputation3(){
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
		 * Generic tests for net structure
		 */
		assertTrue(net.isExtendedFreeChoice());
		assertTrue(net.isWFNet());
		assertFalse(net.isSNet());
		assertFalse(net.isTNet());
		assertTrue(net.hasCycle());
		
		/*
		 * Tests for behavioural profile
		 */
		BehaviouralProfile bp = BPCreatorNet.getInstance().deriveBehaviouralProfile(net);

		assertEquals(CharacteristicRelationType.StrictOrder, bp.getRelationForNodes(a, f));
		assertEquals(CharacteristicRelationType.StrictOrder, bp.getRelationForNodes(c, f));
		assertEquals(CharacteristicRelationType.StrictOrder, bp.getRelationForNodes(a, f));
		assertEquals(CharacteristicRelationType.ReverseStrictOrder, bp.getRelationForNodes(i, a));
		assertEquals(CharacteristicRelationType.ReverseStrictOrder, bp.getRelationForNodes(j, a));
		
		assertEquals(CharacteristicRelationType.Exclusive, bp.getRelationForNodes(d, e));
		assertEquals(CharacteristicRelationType.Exclusive, bp.getRelationForNodes(e, d));
		assertEquals(CharacteristicRelationType.Exclusive, bp.getRelationForNodes(a, a));
		assertEquals(CharacteristicRelationType.Exclusive, bp.getRelationForNodes(e, e));
		
		assertEquals(CharacteristicRelationType.InterleavingOrder, bp.getRelationForNodes(h, h));
		assertEquals(CharacteristicRelationType.InterleavingOrder, bp.getRelationForNodes(i, i));
		assertEquals(CharacteristicRelationType.InterleavingOrder, bp.getRelationForNodes(i, h));
		assertEquals(CharacteristicRelationType.InterleavingOrder, bp.getRelationForNodes(b, e));
		assertEquals(CharacteristicRelationType.InterleavingOrder, bp.getRelationForNodes(d, b));
		
		BehaviouralProfile bp2 = BPCreatorTree.getInstance().deriveBehaviouralProfile(net);
		
		assertTrue(bp2.equalsForSharedNodes(bp));
		
		BehaviouralProfile bp3 = BPCreatorUnfolding.getInstance().deriveBehaviouralProfile(net);
		
		assertTrue(bp3.equalsForSharedNodes(bp));
		
		/*
		 * Tests for co-occurrence relation of the causal behavioural profile
		 */
		CausalBehaviouralProfile cbp = CBPCreatorTree.getInstance().deriveCausalBehaviouralProfile(net);
		assertTrue(cbp.areCooccurring(a, a));
		assertTrue(cbp.areCooccurring(d, d));
		assertTrue(cbp.areCooccurring(i, i));
		assertTrue(cbp.areCooccurring(a, b));
		assertTrue(cbp.areCooccurring(b, a));
		
		assertFalse(cbp.areCooccurring(a, i));
		assertTrue(cbp.areCooccurring(i, a));
		assertFalse(cbp.areCooccurring(b, d));
		assertTrue(cbp.areCooccurring(d, b));
		assertFalse(cbp.areCooccurring(d, e));
		assertFalse(cbp.areCooccurring(e, d));
		
		assertFalse(cbp.areCooccurring(g, i));
		assertTrue(cbp.areCooccurring(i, g));
		assertTrue(cbp.areCooccurring(g, h));
		assertTrue(cbp.areCooccurring(h, g));


	}


}
