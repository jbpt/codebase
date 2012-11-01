package org.jbpt.test.petri.unfolding;

import junit.framework.TestCase;

import org.jbpt.petri.NetSystem;
import org.jbpt.petri.unfolding.Event;
import org.jbpt.petri.unfolding.OrderingRelationsGraph;
import org.jbpt.petri.unfolding.ProperCompletePrefixUnfolding;
import org.jbpt.pm.Activity;
import org.jbpt.pm.AndGateway;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.XorGateway;
import org.jbpt.pm.structure.ProcessModel2NetSystem;
import org.jbpt.throwable.TransformationException;

/**
 * Proper complete prefix unfolding tests. 
 *
 * @author Artem Polyvyanyy
 *
 */
public class ProperCompletePrefixUnfoldingTest extends TestCase {
	
	/**
	 * Basic acyclic rigid test.
	 * 
	 * Test is based on Figure 8 and Figure 9(a) in:
	 * Artem Polyvyanyy, Luciano García-Bañuelos, Marlon Dumas: Structuring acyclic process models. Inf. Syst. 37(6): 518-538 (2012)
	 */
	public void testBasicAcyclicRigid() throws TransformationException {
		ProcessModel p = new ProcessModel();
		
		Activity ti = new Activity();
		Activity to = new Activity();
		Activity ta = new Activity("A");
		Activity tb = new Activity("B");
		Activity tc = new Activity("C");
		Activity td = new Activity("D");
		
		XorGateway gu = new XorGateway();
		XorGateway gx = new XorGateway();
		XorGateway gy = new XorGateway();
		
		AndGateway gv = new AndGateway();
		AndGateway gw = new AndGateway();
		AndGateway gz = new AndGateway();
		
		p.addControlFlow(ti,gu);
		p.addControlFlow(gu,ta);
		p.addControlFlow(gu,tb);
		p.addControlFlow(ta,gv);
		p.addControlFlow(tb,gw);
		p.addControlFlow(gv,gx);
		p.addControlFlow(gw,gy);
		p.addControlFlow(gv,gy);
		p.addControlFlow(gw,gx);
		p.addControlFlow(gx,tc);
		p.addControlFlow(gy,td);
		p.addControlFlow(tc,gz);
		p.addControlFlow(td,gz);
		p.addControlFlow(gz,to);
		
		NetSystem net = ProcessModel2NetSystem.transform(p);
		ProperCompletePrefixUnfolding pcpu = new ProperCompletePrefixUnfolding(net);
		OrderingRelationsGraph orgraph = new OrderingRelationsGraph(pcpu);
		
		assertEquals(4,orgraph.getEvents().size());
		
		for (Event e1 : orgraph.getEvents()) {
			for (Event e2 : orgraph.getEvents()) {
				if (e1.equals(e2))
					assertTrue(orgraph.areConcurrent(e1,e2));
				else {
					if (e1.getTransition().getLabel().equals("A")) {
						if (e2.getTransition().getLabel().equals("B")) {
							assertTrue(orgraph.areInConflict(e1,e2));
							assertTrue(orgraph.areInConflict(e2,e1));
						}
						if (e2.getTransition().getLabel().equals("C")) {
							assertTrue(orgraph.areCausal(e1,e2));
							assertTrue(orgraph.areInverseCausal(e2,e1));
						}
						if (e2.getTransition().getLabel().equals("D")) {
							assertTrue(orgraph.areCausal(e1,e2));
							assertTrue(orgraph.areInverseCausal(e2,e1));
						}
					}
					if (e1.getTransition().getLabel().equals("B")) {
						if (e2.getTransition().getLabel().equals("A")) {
							assertTrue(orgraph.areInConflict(e1,e2));
							assertTrue(orgraph.areInConflict(e2,e1));
						}
						if (e2.getTransition().getLabel().equals("C")) {
							assertTrue(orgraph.areCausal(e1,e2));
							assertTrue(orgraph.areInverseCausal(e2,e1));
						}
						if (e2.getTransition().getLabel().equals("D")) {
							assertTrue(orgraph.areCausal(e1,e2));
							assertTrue(orgraph.areInverseCausal(e2,e1));
						}
					}
					if (e1.getTransition().getLabel().equals("C")) {
						if (e2.getTransition().getLabel().equals("A")) {
							assertTrue(orgraph.areInverseCausal(e1,e2));
							assertTrue(orgraph.areCausal(e2,e1));
						}
						if (e2.getTransition().getLabel().equals("B")) {
							assertTrue(orgraph.areInverseCausal(e1,e2));
							assertTrue(orgraph.areCausal(e2,e1));
						}
						if (e2.getTransition().getLabel().equals("D")) {
							assertTrue(orgraph.areConcurrent(e1,e2));
							assertTrue(orgraph.areConcurrent(e2,e1));
						}
					}
					if (e1.getTransition().getLabel().equals("D")) {
						if (e2.getTransition().getLabel().equals("A")) {
							assertTrue(orgraph.areInverseCausal(e1,e2));
							assertTrue(orgraph.areCausal(e2,e1));
						}
						if (e2.getTransition().getLabel().equals("B")) {
							assertTrue(orgraph.areInverseCausal(e1,e2));
							assertTrue(orgraph.areCausal(e2,e1));
						}
						if (e2.getTransition().getLabel().equals("C")) {
							assertTrue(orgraph.areConcurrent(e1,e2));
							assertTrue(orgraph.areConcurrent(e2,e1));
						}
					}
				}
			}
		}
		
		//this.serialize(p,net,occnet,orgraph);
	}

	/*private void serialize(ProcessModel p, NetSystem net, OccurrenceNet bpnet, OrderingRelationsGraph orGraph) {
		IOUtils.toFile("model.dot", p.toDOT());
		IOUtils.toFile("net.dot", net.toDOT());
		IOUtils.toFile("unf.dot", bpnet.toDOT());
		IOUtils.toFile("orgraph.dot", orGraph.toDOT());
	}*/
	
	/*public void test1() throws TransformationException, FileNotFoundException {
		ProcessModel p = new ProcessModel();
		
		Activity ti = new Activity("I");
		Activity to = new Activity("O");
		Activity ta = new Activity("A");
		Activity tb = new Activity("B");
		Activity tc = new Activity("C");
		Activity td = new Activity("D");
		Activity te = new Activity("E");
		Activity tf = new Activity("F");
		Activity tg = new Activity("G");
		Activity th = new Activity("H");
		Activity tj = new Activity("J");
		Activity tk = new Activity("K");
	
		XorGateway gq = new XorGateway();
		XorGateway gr = new XorGateway();
		XorGateway gs = new XorGateway();
		XorGateway gt = new XorGateway();
		XorGateway gu = new XorGateway();
		AndGateway gv = new AndGateway();
		AndGateway gw = new AndGateway();
		XorGateway gx = new XorGateway();
		XorGateway gy = new XorGateway();
		AndGateway gz = new AndGateway();
		
		p.addControlFlow(ti,gq);
		p.addControlFlow(gq,gr);
		p.addControlFlow(gq,gs);
		p.addControlFlow(gr,ta);
		p.addControlFlow(ta,gt);
		p.addControlFlow(gt,tb);
		p.addControlFlow(tb,gu);
		p.addControlFlow(gu,tc);
		p.addControlFlow(tc,gs);
		p.addControlFlow(gs,tk);
		p.addControlFlow(tk,gr);
		p.addControlFlow(gt,gv);
		p.addControlFlow(gv,td);
		p.addControlFlow(gv,te);
		p.addControlFlow(td,gx);
		p.addControlFlow(te,gy);
		p.addControlFlow(gx,th);
		p.addControlFlow(gy,tj);
		p.addControlFlow(th,gz);
		p.addControlFlow(tj,gz);
		p.addControlFlow(gz,to);
		p.addControlFlow(gu,gw);
		p.addControlFlow(gw,tf);
		p.addControlFlow(gw,tg);
		p.addControlFlow(tf,gx);
		p.addControlFlow(tg,gy);
		
		IOUtils.toFile("model1.dot", p.toDOT());
		
		NetSystem net = ProcessModel2NetSystem.transform(p);
		int cp = 1; int ct = 1;
		for (Place place : net.getPlaces()) place.setName("p"+cp++);
		for (Transition trans : net.getTransitions()) trans.setName("t"+ct++);
		net.loadNaturalMarking();
		IOUtils.toFile("net1.dot", net.toDOT());
		
		ProperCompletePrefixUnfolding unf = new ProperCompletePrefixUnfolding(net);
		OccurrenceNet bpnet = (OccurrenceNet) unf.getOccurrenceNet();
		IOUtils.toFile("unf1.dot", bpnet.toDOT());
	}
	
	public void test2() throws TransformationException, FileNotFoundException {
		ProcessModel p = new ProcessModel();
		
		Activity ti = new Activity("I");
		Activity to = new Activity("O");
		Activity ta = new Activity("A");
		Activity tb = new Activity("B");
		Activity tc = new Activity("C");
		Activity td = new Activity("D");
		Activity te = new Activity("E");
		Activity tf = new Activity("F");
		Activity tg = new Activity("G");
		Activity th = new Activity("H");
		Activity tj = new Activity("J");
		Activity tk = new Activity("K");
	
		AndGateway gq = new AndGateway();
		XorGateway gr = new XorGateway();
		XorGateway gs = new XorGateway();
		XorGateway gt = new XorGateway();
		AndGateway gu = new AndGateway();
		AndGateway gv = new AndGateway();
		XorGateway gw = new XorGateway();
		XorGateway gx = new XorGateway();
		AndGateway gy = new AndGateway();
		AndGateway gz = new AndGateway();
		
		p.addControlFlow(ti,gq);
		p.addControlFlow(gq,gr);
		p.addControlFlow(gr,ta);
		p.addControlFlow(ta,gs);
		p.addControlFlow(gs,tb);
		p.addControlFlow(tb,gt);
		p.addControlFlow(gt,tc);
		p.addControlFlow(tc,gr);
		p.addControlFlow(gs,gu);
		p.addControlFlow(gt,gv);
		p.addControlFlow(gu,td);
		p.addControlFlow(gu,te);
		p.addControlFlow(td,gw);
		p.addControlFlow(te,gx);
		p.addControlFlow(gw,th);
		p.addControlFlow(gx,tj);
		p.addControlFlow(th,gz);
		p.addControlFlow(tj,gy);
		p.addControlFlow(gy,tk);
		p.addControlFlow(tk,gz);
		p.addControlFlow(gz,to);
		p.addControlFlow(gq,gy);
		p.addControlFlow(gv,tf);
		p.addControlFlow(gv,tg);
		p.addControlFlow(tf,gw);
		p.addControlFlow(tg,gx);
		
		IOUtils.toFile("model2.dot", p.toDOT());
		
		NetSystem net = ProcessModel2NetSystem.transform(p);
		int cp = 1; int ct = 1;
		for (Place place : net.getPlaces()) place.setName("p"+cp++);
		for (Transition trans : net.getTransitions()) trans.setName("t"+ct++);
		net.loadNaturalMarking();
		IOUtils.toFile("net2.dot", net.toDOT());
		
		ProperCompletePrefixUnfolding unf = new ProperCompletePrefixUnfolding(net);
		OccurrenceNet bpnet = (OccurrenceNet)unf.getOccurrenceNet();
		IOUtils.toFile("unf2.dot", bpnet.toDOT());
	}
	
	public void test3() throws TransformationException, FileNotFoundException {
		ProcessModel p = new ProcessModel();
		
		Activity ti = new Activity("I");
		Activity to = new Activity("O");
		Activity ta = new Activity("A");
		Activity tb = new Activity("B");
		Activity tc = new Activity("C");
		Activity td = new Activity("D");
		Activity te = new Activity("E");
		Activity tf = new Activity("F");
		Activity tg = new Activity("G");
		
		AndGateway gq = new AndGateway();
		XorGateway gr = new XorGateway();
		XorGateway gs = new XorGateway();
		XorGateway gt = new XorGateway();
		AndGateway gu = new AndGateway();
		AndGateway gv = new AndGateway();
		XorGateway gw = new XorGateway();
		XorGateway gx = new XorGateway();
		AndGateway gy = new AndGateway();
		AndGateway gz = new AndGateway();
		
		p.addControlFlow(ti,gq);
		p.addControlFlow(gq,gr);
		p.addControlFlow(gr,ta);
		p.addControlFlow(ta,gs);
		p.addControlFlow(gs,tb);
		p.addControlFlow(tb,gt);
		p.addControlFlow(gt,tc);
		p.addControlFlow(tc,gr);
		p.addControlFlow(gs,gu);
		p.addControlFlow(gu,td);
		p.addControlFlow(td,gw);
		p.addControlFlow(gw,te);
		p.addControlFlow(te,gz);
		p.addControlFlow(gz,to);
		p.addControlFlow(gt,gv);
		p.addControlFlow(gv,gx);
		p.addControlFlow(gx,tf);
		p.addControlFlow(tf,gy);
		p.addControlFlow(gy,tg);
		p.addControlFlow(tg,gz);
		p.addControlFlow(gu,gx);
		p.addControlFlow(gv,gw);
		p.addControlFlow(gq,gy);
		
		IOUtils.toFile("model3.dot", p.toDOT());
		
		NetSystem net = ProcessModel2NetSystem.transform(p);
		int cp = 1; int ct = 1;
		for (Place place : net.getPlaces()) place.setName("p"+cp++);
		for (Transition trans : net.getTransitions()) trans.setName("t"+ct++);
		net.loadNaturalMarking();
		IOUtils.toFile("net3.dot", net.toDOT());
		
		ProperCompletePrefixUnfolding unf = new ProperCompletePrefixUnfolding(net);
		OccurrenceNet bpnet = (OccurrenceNet)unf.getOccurrenceNet();
		IOUtils.toFile("unf3.dot", bpnet.toDOT());
	}
	
	public void test4() throws TransformationException, FileNotFoundException {
		ProcessModel p = new ProcessModel();
		
		Activity ti = new Activity("I");
		Activity to = new Activity("O");
		Activity td = new Activity("D");
		Activity te = new Activity("E");
		Activity tf = new Activity("F");
		Activity tg = new Activity("G");
		Activity tst = new Activity("ST");
		Activity ttv = new Activity("TV");
		
		AndGateway gq = new AndGateway();
		XorGateway grst = new XorGateway();
		AndGateway gu = new AndGateway();
		AndGateway gv = new AndGateway();
		XorGateway gw = new XorGateway();
		XorGateway gx = new XorGateway();
		AndGateway gy = new AndGateway();
		AndGateway gz = new AndGateway();
		
		p.addControlFlow(ti,gq);
		p.addControlFlow(gq,grst);
		p.addControlFlow(grst,tst);
		p.addControlFlow(grst,ttv);
		p.addControlFlow(tst,gu);
		p.addControlFlow(ttv,gv);
		p.addControlFlow(gu,td);
		p.addControlFlow(td,gw);
		p.addControlFlow(gw,te);
		p.addControlFlow(te,gz);
		p.addControlFlow(gz,to);
		p.addControlFlow(gv,gx);
		p.addControlFlow(gx,tf);
		p.addControlFlow(tf,gy);
		p.addControlFlow(gy,tg);
		p.addControlFlow(tg,gz);
		p.addControlFlow(gu,gx);
		p.addControlFlow(gv,gw);
		p.addControlFlow(gq,gy);
		
		IOUtils.toFile("model4.dot",p.toDOT());
		
		NetSystem net = ProcessModel2NetSystem.transform(p);
		int cp = 1; int ct = 1;
		for (Place place : net.getPlaces()) place.setName("p"+cp++);
		for (Transition trans : net.getTransitions()) trans.setName("t"+ct++);
		
		net.loadNaturalMarking();
		IOUtils.toFile("net4.dot", net.toDOT());
		
		ProperCompletePrefixUnfolding unf = new ProperCompletePrefixUnfolding(net);
		OccurrenceNet bpnet = (OccurrenceNet)unf.getOccurrenceNet();
		IOUtils.toFile("unf4.dot", bpnet.toDOT());
	}*/
}
