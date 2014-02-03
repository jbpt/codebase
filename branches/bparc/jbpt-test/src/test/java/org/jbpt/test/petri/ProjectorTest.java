package org.jbpt.test.petri;

import junit.framework.TestCase;

import org.jbpt.petri.Flow;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.petri.io.PNMLSerializer;
import org.jbpt.petri.structure.PetriNetProjector;
import org.junit.Test;

public class ProjectorTest extends TestCase {

	@Test
	public void testReducePetriNetBasedOnProjectionSet() {
		PNMLSerializer ser = new PNMLSerializer();
		NetSystem pn = ser.parse("src/test/resources/models/petri_net_pnml/simp.pnml");

		assertEquals(43, pn.getTransitions().size());
		assertEquals(38, pn.getPlaces().size());
		assertEquals(88, pn.getFlow().size());

		PetriNetProjector<Flow, Node, Place, Transition> projector = new PetriNetProjector<Flow, Node, Place, Transition>();
		
		projector.reducePetriNetBasedOnProjectionSet(pn, pn.getTransitions());
		
		assertEquals(43, pn.getTransitions().size());
		assertEquals(38, pn.getPlaces().size());
		assertEquals(88, pn.getFlow().size());
				
		projector.reducePetriNetBasedOnProjectionSet(pn, pn.getObservableTransitions());
	
		assertEquals(19, pn.getTransitions().size());
		assertEquals(14, pn.getPlaces().size());
		assertEquals(40, pn.getFlow().size());
	}

}
