package org.jbpt.test.petri;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.petri.structure.PetriNetStructuralClassChecks;
import org.junit.Test;

/**
 * @author Artem Polyvyanyy
 */
public class StructuralClassTest {

	@Test
	public void testIsWorkflowNet() {
		PetriNet pn = new PetriNet();
		
		Place i = new Place("i");
		Place o = new Place("o");
		Transition t = new Transition("t");
		
		pn.addFlow(i,t);
		pn.addFlow(t,o);
		
		assertEquals(3,pn.getVertices().size());
		assertEquals(2,pn.getFlow().size());
		
		assertTrue(PetriNetStructuralClassChecks.isWorkflowNet(pn));
		
		assertEquals(3,pn.getVertices().size());
		assertEquals(2,pn.getFlow().size());
	}

}
