package org.jbpt.test.petri;

import org.jbpt.petri.Node;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.petri.util.Process2PetriNet;
import org.jbpt.pm.Activity;
import org.jbpt.pm.AndGateway;
import org.jbpt.pm.Gateway;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.XorGateway;
import org.jbpt.utils.TransformationException;

import junit.framework.TestCase;

public class Process2PetriNetTest extends TestCase {

	public void testXORSplit() {
		ProcessModel p = new ProcessModel();
		Activity t1 = new Activity();
		Activity t2 = new Activity();
		Activity t3 = new Activity();
		Gateway g1 = new XorGateway();
		p.addControlFlow(t1, g1);
		p.addControlFlow(g1, t2);
		p.addControlFlow(g1, t3);
		PetriNet net = null;
		try {
			net = Process2PetriNet.convert(p);
		} catch (TransformationException e) {
			e.printStackTrace();
		}
		assertNotNull(net);
		assertEquals(11, net.getNodes().size());
		assertEquals(5, net.getTransitions().size());
		assertEquals(6, net.getPlaces().size());
		Node gPlace = null;
		for (Node node:net.getNodes()) {
			if (node.getId().equals(g1.getId())) {
				gPlace = node;
				break;
			}
		}
		assertEquals(Place.class, gPlace.getClass());
		assertEquals(1, net.getDirectPredecessors(gPlace).size());
		assertEquals(2, net.getDirectSuccessors(gPlace).size());
		assertEquals(t1.getId(), net.getFirstDirectPredecessor(gPlace).getId());
	}
	
	public void testANDSplit() {
		ProcessModel p = new ProcessModel();
		Activity t1 = new Activity();
		Activity t2 = new Activity();
		Activity t3 = new Activity();
		Gateway g1 = new AndGateway();
		p.addControlFlow(t1, g1);
		p.addControlFlow(g1, t2);
		p.addControlFlow(g1, t3);
		PetriNet net = null;
		try {
			net = Process2PetriNet.convert(p);
		} catch (TransformationException e) {
			e.printStackTrace();
		}
		assertNotNull(net);
		assertEquals(10, net.getNodes().size());
		assertEquals(4, net.getTransitions().size());
		assertEquals(6, net.getPlaces().size());
		Node gTrans = null;
		for (Node node:net.getNodes()) {
			if (node.getId().equals(g1.getId())) {
				gTrans = node;
				break;
			}
		}
		assertEquals(Transition.class, gTrans.getClass());
		assertEquals(1, net.getDirectPredecessors(gTrans).size());
		assertEquals(2, net.getDirectSuccessors(gTrans).size());
		assertEquals(t1.getId(), net.getFirstDirectPredecessor(net.getFirstDirectPredecessor(gTrans)).getId());
	}
}
