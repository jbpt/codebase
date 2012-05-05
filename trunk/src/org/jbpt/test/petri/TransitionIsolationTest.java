package org.jbpt.test.petri;


import junit.framework.TestCase;

import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.petri.wft.PetriNetUtils;
import org.junit.Test;

public class TransitionIsolationTest extends TestCase {

	@Test
	public void testIsolation() {

		NetSystem net = new NetSystem();
		
		Transition a = new Transition("a");
		Transition b = new Transition("b");
		Transition c = new Transition("c");

		net.addNode(a);
		net.addNode(b);
		net.addNode(c);

		Place p1 = new Place("1");
		Place p2 = new Place("2");
		Place p3 = new Place("3");
		
		net.addNode(p1);
		net.addNode(p2);
		net.addNode(p3);

		net.addFlow(p1, a);
		net.addFlow(a, p2);
		net.addFlow(a, p3);
		net.addFlow(p2, b);
		net.addFlow(p3, c);
				
		PetriNetUtils.isolateTransitions(net);

		assertEquals(4, net.getTransitions().size());
		assertEquals(4, net.getPlaces().size());
		assertEquals(7, net.getFlow().size());
		
		assertFalse(net.getPostset(a).contains(b));
		assertFalse(net.getPostset(a).contains(c));
	}

	@Test
	public void testIsolationForClone() {

		NetSystem net = new NetSystem();
		
		Transition a = new Transition("a");
		Transition b = new Transition("b");
		Transition c = new Transition("c");

		net.addNode(a);
		net.addNode(b);
		net.addNode(c);

		Place p1 = new Place("1");
		Place p2 = new Place("2");
		Place p3 = new Place("3");
		
		net.addNode(p1);
		net.addNode(p2);
		net.addNode(p3);

		net.addFlow(p1, a);
		net.addFlow(a, p2);
		net.addFlow(a, p3);
		net.addFlow(p2, b);
		net.addFlow(p3, c);

		NetSystem clone = net.clone();

		PetriNetUtils.isolateTransitions(clone);

		assertEquals(4, clone.getTransitions().size());
		assertEquals(4, clone.getPlaces().size());
		assertEquals(7, clone.getFlow().size());

		assertFalse(clone.getPostset(a).contains(b));
		assertFalse(clone.getPostset(a).contains(c));

	}

}
