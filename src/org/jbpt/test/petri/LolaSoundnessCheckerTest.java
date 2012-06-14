package org.jbpt.test.petri;

import java.io.IOException;

import junit.framework.TestCase;

import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.petri.bevahior.LolaSoundnessChecker;
import org.jbpt.utils.SerializationException;

public class LolaSoundnessCheckerTest extends TestCase {

	public void testSoundness() {
		NetSystem net = new NetSystem();
		Place p1 = new Place();
		Place p2 = new Place();
		Place p3 = new Place();
		Place p4 = new Place();
		Place p5 = new Place();
		Place p6 = new Place();
		Transition t1 = new Transition();
		Transition t2 = new Transition();
		Transition t3 = new Transition();
		Transition t4 = new Transition();
		net.addFlow(p1, t1);
		net.addFlow(t1, p2);
		net.addFlow(t1, p3);
		net.addFlow(p2, t2);
		net.addFlow(p3, t3);
		net.addFlow(t2, p4);
		net.addFlow(t3, p5);
		net.addFlow(p4, t4);
		net.addFlow(p5, t4);
		net.addFlow(t4, p6);
		net.putTokens(p1,1);
		try {
			assertTrue(LolaSoundnessChecker.isSound(net));
		} catch (IOException e) {
			e.printStackTrace();
			fail("LoLA seems to be unavailable.");
		} catch (SerializationException e) {
			e.printStackTrace();
			fail("The PetriNet cannot be serialized.");
		}
	}
}
