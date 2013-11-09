package org.jbpt.test.petri;

import java.io.IOException;

import junit.framework.TestCase;

import org.jbpt.automaton.Automaton;
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

public class StateSpaceTest extends TestCase {

	@Test
	public void testSimpleStateSpace() throws IOException {
		PNMLSerializer ser = new PNMLSerializer();
		NetSystem netSystem = ser.parse("src/test/resources/models/petri_net_pnml/simp.pnml");

		assertEquals(43, netSystem.getTransitions().size());
		assertEquals(38, netSystem.getPlaces().size());
		assertEquals(88, netSystem.getFlow().size());
		
		SimpleStateSpace<Flow, Node, Place, Transition, Marking> space = new SimpleStateSpace<Flow, Node, Place, Transition, Marking>(netSystem);
		
		space.createUpToNumberOfMarkings(0);
		assertEquals(1, space.getNumberOfMarkings());

		space.createUpToNumberOfMarkings(10);
		assertEquals(10, space.getNumberOfMarkings());

		space.create();
		assertEquals(121, space.getNumberOfMarkings());
		
		Automaton stateSpace = new Automaton(netSystem);
		assertEquals(121, stateSpace.getVertices().size());
		
		//IOUtils.invokeDOT("./", "ns1.png", netSystem.toDOT());
		//IOUtils.invokeDOT("./", "ss1.png", stateSpace.toDOT());
	}

	@Test
	public void testSimpleStateSpaceReducedNet() throws IOException {
		PNMLSerializer ser = new PNMLSerializer();
		NetSystem netSystem = ser.parse("src/test/resources/models/petri_net_pnml/simp.pnml");

		assertEquals(43, netSystem.getTransitions().size());
		assertEquals(38, netSystem.getPlaces().size());
		assertEquals(88, netSystem.getFlow().size());
		
		PetriNetProjector<Flow, Node, Place, Transition> projector = new PetriNetProjector<Flow, Node, Place, Transition>();
		projector.reducePetriNetBasedOnProjectionSet(netSystem, netSystem.getObservableTransitions());
		
		SimpleStateSpace<Flow, Node, Place, Transition, Marking> space = new SimpleStateSpace<Flow, Node, Place, Transition, Marking>(netSystem);
		space.create();
		assertEquals(18, space.getNumberOfMarkings());
		
		Automaton stateSpace = new Automaton(netSystem);
		assertEquals(18, stateSpace.getVertices().size());
		
		//IOUtils.invokeDOT("./", "ns2.png", netSystem.toDOT());
		//IOUtils.invokeDOT("./", "ss2.png", stateSpace.toDOT());
	}

}
