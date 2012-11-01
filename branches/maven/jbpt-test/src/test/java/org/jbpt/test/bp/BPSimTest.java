package org.jbpt.test.bp;

import junit.framework.TestCase;

import org.jbpt.alignment.Alignment;
import org.jbpt.bp.BehaviouralProfile;
import org.jbpt.bp.construct.BPCreatorUnfolding;
import org.jbpt.bp.sim.AggregatedSimilarity;
import org.jbpt.bp.sim.ExclusivenessSimilarity;
import org.jbpt.bp.sim.ExtendedInterleavingSimilarity;
import org.jbpt.bp.sim.ExtendedOrderSimilarity;
import org.jbpt.bp.sim.InterleavingSimilarity;
import org.jbpt.bp.sim.OrderSimilarity;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.junit.Test;


public class BPSimTest extends TestCase {
	
	@Test
	public void testSims() {
		NetSystem net1 = new NetSystem();
		
		Transition a = new Transition("a");
		Transition b = new Transition("b");
		Transition c = new Transition("c");
		Transition d = new Transition("d");
		Transition e = new Transition("e");

		net1.addNode(a);
		net1.addNode(b);
		net1.addNode(c);
		net1.addNode(d);
		net1.addNode(e);

		Place p1 = new Place("1");
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
		
		net1.getMarking().put(p1,1);
		
		net1.addFlow(p1, a);
		net1.addFlow(a, p2);
		net1.addFlow(a, p3);
		net1.addFlow(p2, b);
		net1.addFlow(p3, c);
		net1.addFlow(p3, d);
		net1.addFlow(b, p4);
		net1.addFlow(c, p5);
		net1.addFlow(d, p5);
		net1.addFlow(p4, e);
		net1.addFlow(p5, e);
		net1.addFlow(e, p6);

		NetSystem net2 = new NetSystem();
		
		Transition x = new Transition("x");
		Transition y = new Transition("y");
		Transition z = new Transition("z");

		net2.addNode(x);
		net2.addNode(y);
		net2.addNode(z);

		Place p21 = new Place("1");
		Place p22 = new Place("2");
		Place p23 = new Place("3");
		Place p24 = new Place("4");
		
		net2.addNode(p21);
		net2.addNode(p22);
		net2.addNode(p23);
		net2.addNode(p24);
		
		net2.getMarking().put(p21,1);
		
		net2.addFlow(p21, x);
		net2.addFlow(x, p22);
		net2.addFlow(p22, y);
		net2.addFlow(p22, z);
		net2.addFlow(y, p23);
		net2.addFlow(z, p24);
		
		BehaviouralProfile<NetSystem, Node> bp1 = BPCreatorUnfolding.getInstance().deriveRelationSet(net1);
		BehaviouralProfile<NetSystem, Node> bp2 = BPCreatorUnfolding.getInstance().deriveRelationSet(net2);
		
		Alignment<BehaviouralProfile<NetSystem,Node>, Node> al = new Alignment<BehaviouralProfile<NetSystem,Node>, Node>(bp1, bp2);
		
		ExclusivenessSimilarity<BehaviouralProfile<NetSystem,Node>, NetSystem, Node>         ex = new ExclusivenessSimilarity<BehaviouralProfile<NetSystem,Node>, NetSystem, Node> ();
		OrderSimilarity<BehaviouralProfile<NetSystem,Node>, NetSystem, Node>                 so = new OrderSimilarity<BehaviouralProfile<NetSystem,Node>, NetSystem, Node>();
		InterleavingSimilarity<BehaviouralProfile<NetSystem,Node>, NetSystem, Node>          io = new InterleavingSimilarity<BehaviouralProfile<NetSystem,Node>, NetSystem, Node>();
		ExtendedOrderSimilarity<BehaviouralProfile<NetSystem,Node>, NetSystem, Node>        eso = new ExtendedOrderSimilarity<BehaviouralProfile<NetSystem,Node>, NetSystem, Node>();
		ExtendedInterleavingSimilarity<BehaviouralProfile<NetSystem,Node>, NetSystem, Node> eio = new ExtendedInterleavingSimilarity<BehaviouralProfile<NetSystem,Node>, NetSystem, Node>();
		AggregatedSimilarity<BehaviouralProfile<NetSystem,Node>, NetSystem, Node>           agg = new AggregatedSimilarity<BehaviouralProfile<NetSystem,Node>, NetSystem, Node>();
		
		agg.weightExSim = 1;
		agg.weightSoSim = 1;
		agg.weightInSim = 1;
		agg.weightESSim = 1;
		agg.weightEISim = 1;
		
		assertTrue(ex.score(al) == 0);
		assertTrue(so.score(al) == 0);
		assertTrue(io.score(al) == 0);
		assertTrue(eso.score(al) == 0);
		assertTrue(eio.score(al) == 0);
		
		al.addElementaryCorrespondence(a, x);
		
		
		assertTrue(Math.round(ex.score(al)*1000) == 91);
		assertTrue(so.score(al) == 0);
		assertTrue(io.score(al) == 0);
		assertTrue(eso.score(al) == 0);
		assertTrue(eio.score(al) == 0);

		al.addElementaryCorrespondence(b, y);

		assertTrue(Math.round(ex.score(al)*1000) == 200);
		assertTrue(Math.round(so.score(al)*1000) == 125);
		assertTrue(io.score(al) == 0);
		assertTrue(Math.round(eso.score(al)*1000) == 125);
		assertTrue(Math.round(eio.score(al)*1000) == 100);

		al.addElementaryCorrespondence(c, z);

		assertTrue(Math.round(ex.score(al)*1000) == 333);
		assertTrue(Math.round(so.score(al)*1000) == 286);
		assertTrue(io.score(al) == 0);
		assertTrue(Math.round(eso.score(al)*1000) == 286);
		assertTrue(Math.round(eio.score(al)*1000) == 222);

		al.removeElementaryCorrespondence(b, y);
		al.addElementaryCorrespondence(d, y);
		
		assertTrue(Math.round(ex.score(al)*1000) == 714);
		assertTrue(Math.round(so.score(al)*1000) == 286);
		assertTrue(io.score(al) == 0);
		assertTrue(Math.round(eso.score(al)*1000) == 286);
		assertTrue(Math.round(eio.score(al)*1000) == 222);

	}

}
