package org.jbpt.test.petri;

import junit.framework.TestCase;

import org.jbpt.petri.Flow;
import org.jbpt.petri.Marking;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.petri.behavior.SimpleStateSpace;
import org.jbpt.petri.io.PNMLSerializer;
import org.jbpt.petri.structure.PetriNetProjector;
import org.junit.Test;

public class SimpleStateSpaceTest extends TestCase {

	@Test
	public void testSimpleStateSpace() {
		PNMLSerializer ser = new PNMLSerializer();
		NetSystem netSystem = ser.parse("src/test/resources/models/petri_net_pnml/simp.pnml");

		assertEquals(43, netSystem.getTransitions().size());
		assertEquals(38, netSystem.getPlaces().size());
		assertEquals(88, netSystem.getFlow().size());
		
		SimpleStateSpace<Flow, Node, Place, Transition, Marking> space = new SimpleStateSpace<>(netSystem);
		
		space.createUpToNumberOfMarkings(0);
		assertEquals(1, space.getNumberOfMarkings());

		space.createUpToNumberOfMarkings(10);
		assertEquals(10, space.getNumberOfMarkings());

		space.create();
		assertEquals(121, space.getNumberOfMarkings());
		
	}

	@Test
	public void testSimpleStateSpaceReducedNet() {
		PNMLSerializer ser = new PNMLSerializer();
		NetSystem netSystem = ser.parse("src/test/resources/models/petri_net_pnml/simp.pnml");

		assertEquals(43, netSystem.getTransitions().size());
		assertEquals(38, netSystem.getPlaces().size());
		assertEquals(88, netSystem.getFlow().size());
		
		PetriNetProjector<Flow, Node, Place, Transition> projector = new PetriNetProjector<>();
		projector.reducePetriNetBasedOnProjectionSet(netSystem, netSystem.getObservableTransitions());
		
		SimpleStateSpace<Flow, Node, Place, Transition, Marking> space = new SimpleStateSpace<>(netSystem);
		space.create();
		assertEquals(18, space.getNumberOfMarkings());

	}

}
