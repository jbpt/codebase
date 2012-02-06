package de.hpi.bpt.process.petri.bp.test;

import junit.framework.TestCase;
import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;
import de.hpi.bpt.process.petri.bp.RelSet;
import de.hpi.bpt.process.petri.bp.RelSetType;
import de.hpi.bpt.process.petri.bp.construct.RelSetCreatorUnfolding;


public class RelSetComputationTest extends TestCase {

	public void testRelSet1() {
		PetriNet net = new PetriNet();
		
		Transition a = new Transition("a");
		Transition b = new Transition("b");
		Transition c = new Transition("c");
		Transition d = new Transition("d");
		Transition e = new Transition("e");
		Transition f = new Transition("f");
		Transition g = new Transition("g");
		Transition h = new Transition("h");

		net.addNode(a);
		net.addNode(b);
		net.addNode(c);
		net.addNode(d);
		net.addNode(e);
		net.addNode(f);
		net.addNode(g);
		net.addNode(h);

		Place p1 = new Place("1");
		p1.setTokens(1);
		Place p2 = new Place("2");
		Place p3 = new Place("3");
		Place p4 = new Place("4");
		Place p5 = new Place("5");
		Place p6 = new Place("6");
		Place p7 = new Place("7");
		
		net.addNode(p1);
		net.addNode(p2);
		net.addNode(p3);
		net.addNode(p4);
		net.addNode(p5);
		net.addNode(p6);
		net.addNode(p7);
		
		net.addFlow(p1, a);

		net.addFlow(a, p6);
		net.addFlow(p6, h);
		net.addFlow(h, p7);
		
		net.addFlow(a, p2);
		net.addFlow(p2, b);
		net.addFlow(p2, c);
		net.addFlow(f, p2);

		net.addFlow(b, p3);
		net.addFlow(d, p3);
		net.addFlow(p3, e);

		net.addFlow(e, p4);
		net.addFlow(p4, d);
		net.addFlow(p4, f);
		net.addFlow(p4, g);
		
		net.addFlow(g, p5);
		net.addFlow(c, p5);

//		System.out.println(net.toDOT());
		
		/*
		 * Get alpha relations
		 */
		RelSet<PetriNet, Node> relSet = RelSetCreatorUnfolding.getInstance().deriveRelationSet(net,1);
		
		assertTrue(relSet.getEntities().size() == 8);

		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(a,a));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(h,h));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(c,c));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(e,e));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(d,d));

		assertEquals(RelSetType.Order, relSet.getRelationForEntities(a,h));
		assertEquals(RelSetType.ReverseOrder, relSet.getRelationForEntities(h,a));

		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(h,b));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(h,c));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(h,d));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(h,e));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(h,f));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(h,g));

		assertEquals(RelSetType.Order, relSet.getRelationForEntities(a,b));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(a,e));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(a,g));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(a,f));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(a,d));

		assertEquals(RelSetType.Order, relSet.getRelationForEntities(a,c));
		assertEquals(RelSetType.Order, relSet.getRelationForEntities(b,e));
		assertEquals(RelSetType.Order, relSet.getRelationForEntities(e,f));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(e,d));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(d,e));
		assertEquals(RelSetType.Order, relSet.getRelationForEntities(e,g));
			
		assertEquals(RelSetType.Order, relSet.getRelationForEntities(f,b));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(b,d));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(b,g));
		
		/*
		 * Get behavioural profile relations
		 */
		relSet = RelSetCreatorUnfolding.getInstance().deriveRelationSet(net,RelSet.RELATION_FAR_LOOKAHEAD);

		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(a,a));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(h,h));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(c,c));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(e,e));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(d,d));

		assertEquals(RelSetType.Order, relSet.getRelationForEntities(a,b));
		assertEquals(RelSetType.Order, relSet.getRelationForEntities(a,c));
		assertEquals(RelSetType.Order, relSet.getRelationForEntities(a,d));
		assertEquals(RelSetType.Order, relSet.getRelationForEntities(a,e));
		assertEquals(RelSetType.Order, relSet.getRelationForEntities(a,f));
		assertEquals(RelSetType.Order, relSet.getRelationForEntities(a,g));
		assertEquals(RelSetType.Order, relSet.getRelationForEntities(a,h));

		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(h,b));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(h,c));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(h,d));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(h,e));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(h,f));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(h,g));

		assertEquals(RelSetType.ReverseOrder, relSet.getRelationForEntities(c,b));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(c,g));
		assertEquals(RelSetType.ReverseOrder, relSet.getRelationForEntities(c,d));

		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(b,f));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(b,d));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(e,b));

		assertEquals(RelSetType.Order, relSet.getRelationForEntities(b,g));
		assertEquals(RelSetType.Order, relSet.getRelationForEntities(e,g));
		
	}
	
}
