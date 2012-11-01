package org.jbpt.test.petri;

import junit.framework.TestCase;

import org.jbpt.petri.NetSystem;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;

public class PetriNetCloneTest extends TestCase {
	
	public void testPetriNetClone() throws CloneNotSupportedException {
		NetSystem net = new NetSystem();
		
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
		Place p6 = new Place("6");
		Place p7 = new Place("7");
		
		net.addNode(p1);
		net.addNode(p2);
		net.addNode(p3);
		net.addNode(p4);
		net.addNode(p5);
		net.addNode(p6);
		net.addNode(p7);
		net.putTokens(p5,3);
		
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
		
		assertTrue(PetriNet.STRUCTURAL_CHECKS.isExtendedFreeChoice(net));
		assertTrue(PetriNet.STRUCTURAL_CHECKS.isWorkflowNet(net));
		assertFalse(PetriNet.STRUCTURAL_CHECKS.isSNet(net));
		assertFalse(PetriNet.STRUCTURAL_CHECKS.isTNet(net));
		assertFalse(PetriNet.DIRECTED_GRAPH_ALGORITHMS.isCyclic(net));
		
		NetSystem clone = (NetSystem) net.clone();
		
		assertEquals(6, clone.getTransitions().size());
		assertEquals(7, clone.getPlaces().size());
		assertEquals(14, clone.getFlow().size());
		
		int count = 0;
		for (Place p : clone.getPlaces()) {
			if (clone.getTokens(p) > 0) {
				assertEquals(3, clone.getTokens(p).intValue());
				assertEquals(1, clone.getDirectPredecessors(p).size()); 
				assertEquals(2, clone.getDirectSuccessors(p).size()); 
				count++;
			}
		}
		assertEquals(1, count);
						
		assertTrue(PetriNet.STRUCTURAL_CHECKS.isExtendedFreeChoice(clone));
		assertTrue(PetriNet.STRUCTURAL_CHECKS.isWorkflowNet(clone));
		assertFalse(PetriNet.STRUCTURAL_CHECKS.isSNet(clone));
		assertFalse(PetriNet.STRUCTURAL_CHECKS.isTNet(clone));
		assertFalse(PetriNet.DIRECTED_GRAPH_ALGORITHMS.isCyclic(net));
		
	}


}
