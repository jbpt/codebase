package org.jbpt.test.bp;

import java.util.HashSet;

import junit.framework.TestCase;

import org.jbpt.bp.RelSet;
import org.jbpt.bp.RelSetType;
import org.jbpt.bp.construct.ProjTARCreatorStateSpace;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;
import org.jbpt.petri.Transition;
import org.jbpt.petri.io.PNMLSerializer;

public class ProjTARCreatorTest extends TestCase {

	public void testProjTARCreator() {
		PNMLSerializer ser = new PNMLSerializer();
		NetSystem netSystem = ser.parse("src/test/resources/models/petri_net_pnml/simp.pnml");

		assertEquals(43, netSystem.getTransitions().size());
		assertEquals(38, netSystem.getPlaces().size());
		assertEquals(88, netSystem.getFlow().size());
		
		RelSet<NetSystem, Node> tar = ProjTARCreatorStateSpace.getInstance().deriveRelationSet(netSystem, new HashSet<Node>(netSystem.getObservableTransitions()));

		assertEquals(RelSetType.Order, tar.getRelationForEntities(getTransitionByLabel(netSystem,"create issue"), getTransitionByLabel(netSystem,"customer extension")));
		assertEquals(RelSetType.Order, tar.getRelationForEntities(getTransitionByLabel(netSystem,"create issue"), getTransitionByLabel(netSystem,"issue details")));
		assertEquals(RelSetType.Order, tar.getRelationForEntities(getTransitionByLabel(netSystem,"create issue"), getTransitionByLabel(netSystem,"resolution plan")));
		assertEquals(RelSetType.Exclusive, tar.getRelationForEntities(getTransitionByLabel(netSystem,"create issue"), getTransitionByLabel(netSystem,"change management")));

		assertEquals(RelSetType.Order, tar.getRelationForEntities(getTransitionByLabel(netSystem,"proposal to close"), getTransitionByLabel(netSystem,"close issue")));
		assertEquals(RelSetType.Interleaving, tar.getRelationForEntities(getTransitionByLabel(netSystem,"proposal to close"), getTransitionByLabel(netSystem,"reject proposal to close")));

		assertEquals(RelSetType.ReverseOrder, tar.getRelationForEntities(getTransitionByLabel(netSystem,"customer extension"), getTransitionByLabel(netSystem,"reject proposal to close")));

		assertEquals(RelSetType.Exclusive, tar.getRelationForEntities(getTransitionByLabel(netSystem,"risk management"), getTransitionByLabel(netSystem,"reject proposal to close")));
		assertEquals(RelSetType.Exclusive, tar.getRelationForEntities(getTransitionByLabel(netSystem,"risk management"), getTransitionByLabel(netSystem,"risk management")));
		
	}
	
	private Transition getTransitionByLabel(NetSystem netSystem, String s) {
		for (Transition t : netSystem.getTransitions())
			if (t.getLabel().equals(s))
				return t;
		
		return null;
	}
}
