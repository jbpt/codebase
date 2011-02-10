package de.hpi.bpt.process.petri.bp.test;

import junit.framework.TestCase;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;
import de.hpi.bpt.process.petri.bp.BehaviouralProfile;
import de.hpi.bpt.process.petri.bp.BehaviouralProfile.CharacteristicRelationType;
import de.hpi.bpt.process.petri.bp.construct.BPCreatorNet;
import de.hpi.bpt.process.petri.bp.construct.BPCreatorTree;

public class CBPComputationTest extends TestCase {
	
	public void testCBPComputation(){
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
		
		assertTrue(net.isExtendedFreeChoice());
		assertTrue(net.isWFNet());
		assertFalse(net.isSNet());
		assertFalse(net.isTNet());
		assertTrue(net.hasCycle());
		
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
		
		assertTrue(bp2.equals(bp));
		
	}


}
