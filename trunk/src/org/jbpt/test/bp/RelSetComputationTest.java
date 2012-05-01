package org.jbpt.test.bp;

import junit.framework.TestCase;

import org.jbpt.bp.RelSet;
import org.jbpt.bp.RelSetType;
import org.jbpt.bp.construct.RelSetCreatorUnfolding;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;


public class RelSetComputationTest extends TestCase {

	public void testRelSet1() {
		NetSystem net = new NetSystem();
		
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
		Place p2 = new Place("2");
		Place p3 = new Place("3");
		Place p4 = new Place("4");
		Place p5 = new Place("5");
		Place p6 = new Place("6");
		Place p7 = new Place("7");
		
		net.addNode(p1);
		net.getMarking().put(p1,1);
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
		RelSet<NetSystem, Node> relSet = RelSetCreatorUnfolding.getInstance().deriveRelationSet(net,1);
		
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

	public void testRelSet2() {
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
		
		net.addNode(p1);
		net.getMarking().put(p1,1);
		net.addNode(p2);
		net.addNode(p3);
		net.addNode(p4);
		net.addNode(p5);
		net.addNode(p6);
		net.addNode(p7);
		net.addNode(p8);
		
		net.addFlow(p1, a);

		net.addFlow(a, p6);
		net.addFlow(p6, h);
		net.addFlow(h, p7);
		
		net.addFlow(p7, i);
		net.addFlow(i, p6);

		net.addFlow(p7, j);
		net.addFlow(j, p8);
		
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

		System.out.println(net.toDOT());
		
		/*
		 * Get alpha relations
		 */
		RelSet<NetSystem, Node> relSet = RelSetCreatorUnfolding.getInstance().deriveRelationSet(net,1);
		
		assertTrue(relSet.getEntities().size() == 10);

		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(a,a));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(h,h));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(c,c));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(e,e));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(d,d));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(i,i));

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
		
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(a,i));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(h,i));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(i,h));

		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(a,j));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(i,j));

		assertEquals(RelSetType.Order, relSet.getRelationForEntities(h,j));
		
		/*
		 * Get behavioural profile relations
		 */
		relSet = RelSetCreatorUnfolding.getInstance().deriveRelationSet(net,RelSet.RELATION_FAR_LOOKAHEAD);

		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(a,a));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(h,h));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(i,i));
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
		
		assertEquals(RelSetType.Order, relSet.getRelationForEntities(a,i));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(h,i));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(i,h));
		
		assertEquals(RelSetType.Order, relSet.getRelationForEntities(a,j));
		assertEquals(RelSetType.Order, relSet.getRelationForEntities(i,j));

		assertEquals(RelSetType.Order, relSet.getRelationForEntities(h,j));

		
	}

}
