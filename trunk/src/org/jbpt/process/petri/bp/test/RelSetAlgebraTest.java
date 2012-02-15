package de.hpi.bpt.process.petri.bp.test;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.junit.Test;

import de.hpi.bpt.alignment.Alignment;
import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;
import de.hpi.bpt.process.petri.bp.BehaviouralProfile;
import de.hpi.bpt.process.petri.bp.RelSetAlgebra;
import de.hpi.bpt.process.petri.bp.RelSetType;
import de.hpi.bpt.process.petri.bp.construct.BPCreatorUnfolding;


public class RelSetAlgebraTest extends TestCase {

	@Test
	public void testAlgebra() {
		PetriNet net1 = new PetriNet();
		
		Transition a = new Transition("a");
		Transition b = new Transition("b");
		Transition c = new Transition("c");
		Transition d = new Transition("d");

		net1.addNode(a);
		net1.addNode(b);
		net1.addNode(c);
		net1.addNode(d);

		Place p1 = new Place("1");
		p1.setTokens(1);
		Place p2 = new Place("2");
		Place p3 = new Place("3");
		Place p4 = new Place("4");
		Place p5 = new Place("5");
		Place p6 = new Place("6");
		
		net1.addNode(p1);
		net1.addNode(p2);
		net1.addNode(p3);
		net1.addNode(p4);
		net1.addNode(p5);
		net1.addNode(p6);
		
		net1.addFlow(p1, a);
		net1.addFlow(a, p2);
		net1.addFlow(a, p3);
		net1.addFlow(p2, b);
		net1.addFlow(p3, c);
		net1.addFlow(b, p4);
		net1.addFlow(c, p5);
		net1.addFlow(p4, d);
		net1.addFlow(p5, d);
		net1.addFlow(d, p6);

		PetriNet net2 = new PetriNet();
		
		Transition x = new Transition("x");
		Transition y = new Transition("y");
		Transition z = new Transition("z");

		net2.addNode(x);
		net2.addNode(y);
		net2.addNode(z);

		Place p21 = new Place("1");
		p21.setTokens(1);
		Place p22 = new Place("2");
		Place p23 = new Place("3");
		Place p24 = new Place("4");
		
		net2.addNode(p21);
		net2.addNode(p22);
		net2.addNode(p23);
		net2.addNode(p24);
		
		net2.addFlow(p21, x);
		net2.addFlow(x, p22);
		net2.addFlow(p22, y);
		net2.addFlow(p22, z);
		net2.addFlow(y, p23);
		net2.addFlow(z, p24);
		
		BehaviouralProfile<PetriNet, Node> bp1 = BPCreatorUnfolding.getInstance().deriveRelationSet(net1);
		BehaviouralProfile<PetriNet, Node> bp2 = BPCreatorUnfolding.getInstance().deriveRelationSet(net2);
		
		Alignment<BehaviouralProfile<PetriNet, Node>, Node> al = new Alignment<BehaviouralProfile<PetriNet, Node>, Node>(bp1, bp2);
		
		al.addElementaryCorrespondence(a, x);
		al.addElementaryCorrespondence(b, y);

		assertTrue(RelSetAlgebra.isEqual(al));
		
		al.addElementaryCorrespondence(c, z);
		
		assertFalse(RelSetAlgebra.isEqual(al));
		assertTrue(RelSetAlgebra.firstSubsumesSecond(al));
		
		BehaviouralProfile<PetriNet, Node>  intersection = new BehaviouralProfile<PetriNet,Node>(al.getFirstModel().getModel(),new ArrayList<Node>(al.getAlignedEntitiesOfFirstModel()));
		RelSetAlgebra.fillIntersection(al, intersection);
		assertTrue(intersection.getRelationForEntities(b, c).equals(RelSetType.Exclusive));
		assertTrue(intersection.getRelationForEntities(a, b).equals(RelSetType.Order));
		
		BehaviouralProfile<PetriNet, Node> union = new BehaviouralProfile<PetriNet,Node>(al.getFirstModel().getModel(),new ArrayList<Node>(al.getAlignedEntitiesOfFirstModel()));
		RelSetAlgebra.fillUnion(al, union);
		assertTrue(union.getRelationForEntities(b, c).equals(RelSetType.Interleaving));
		assertTrue(union.getRelationForEntities(a, b).equals(RelSetType.Order));
		
		
	}

}
