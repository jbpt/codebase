package org.jbpt.test.petri;

import junit.framework.TestCase;

import org.jbpt.petri.Flow;
import org.jbpt.petri.Marking;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.petri.behavior.ProjectedStateSpace;
import org.jbpt.petri.io.PNMLSerializer;
import org.jbpt.petri.structure.PetriNetProjector;
import org.junit.Test;

public class ProjectedStateSpaceTest extends TestCase {

	@Test
	public void testProjectedStateSpace() {
		PNMLSerializer ser = new PNMLSerializer();
		NetSystem netSystem = ser.parse("src/test/resources/models/petri_net_pnml/simp.pnml");

		assertEquals(43, netSystem.getTransitions().size());
		assertEquals(38, netSystem.getPlaces().size());
		assertEquals(88, netSystem.getFlow().size());
		
		ProjectedStateSpace<Flow, Node, Place, Transition, Marking> space = new ProjectedStateSpace<>(netSystem, netSystem.getTransitions());
		
		space.create();
		assertEquals(121, space.getNumberOfMarkings());

	}

	@Test
	public void testProjectedStateSpaceReducedNet() {
		PNMLSerializer ser = new PNMLSerializer();
		NetSystem netSystem = ser.parse("src/test/resources/models/petri_net_pnml/simp.pnml");

		assertEquals(43, netSystem.getTransitions().size());
		assertEquals(38, netSystem.getPlaces().size());
		assertEquals(88, netSystem.getFlow().size());
		
		PetriNetProjector<Flow, Node, Place, Transition> projector = new PetriNetProjector<>();
		projector.reducePetriNetBasedOnProjectionSet(netSystem, netSystem.getObservableTransitions());
		
		ProjectedStateSpace<Flow, Node, Place, Transition, Marking> space = new ProjectedStateSpace<>(netSystem, netSystem.getTransitions());
		space.create();
		assertEquals(18, space.getNumberOfMarkings());

	}

}
