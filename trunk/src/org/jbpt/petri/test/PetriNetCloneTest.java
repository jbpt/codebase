package org.jbpt.petri.test;

import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;

import junit.framework.TestCase;

public class PetriNetCloneTest extends TestCase {
	
	public void testPetriNetClone() throws CloneNotSupportedException {
		PetriNet net = new PetriNet();
		
		Transition a = new Transition("a");
		Transition b = new Transition("b");
		Transition c = new Transition("c");
		Transition d = new Transition("d");
		Transition e = new Transition("e");
		Transition f = new Transition("f");

		net.addNode(a);
		net.addNode(b);
		net.addNode(c);
		net.addNode(d);
		net.addNode(e);
		net.addNode(f);

		Place p1 = new Place("1");
		Place p2 = new Place("2");
		Place p3 = new Place("3");
		Place p4 = new Place("4");
		Place p5 = new Place("5");
		p5.setTokens(3);
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
		
		assertTrue(net.isExtendedFreeChoice());
		assertTrue(net.isWFNet());
		assertFalse(net.isSNet());
		assertFalse(net.isTNet());
		assertFalse(net.hasCycle());
		
		PetriNet clone = (PetriNet) net.clone();
		
		assertEquals(6, clone.getTransitions().size());
		assertEquals(7, clone.getPlaces().size());
		assertEquals(14, clone.getFlowRelation().size());
		
		for (Place p : clone.getPlaces()) {
			if (p.getTokens() > 0) {
				assertEquals(3, p.getTokens());
				assertEquals(1, clone.getDirectPredecessors(p).size()); 
				assertEquals(2, clone.getDirectSuccessors(p).size()); 
			}
		}
				
		
		assertTrue(clone.isExtendedFreeChoice());
		assertTrue(clone.isWFNet());
		assertFalse(clone.isSNet());
		assertFalse(clone.isTNet());
		assertFalse(clone.hasCycle());
		
	}


}
