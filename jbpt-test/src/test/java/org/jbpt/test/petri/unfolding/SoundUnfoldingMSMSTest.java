package org.jbpt.test.petri.unfolding;

import junit.framework.TestCase;


public class SoundUnfoldingMSMSTest extends TestCase {
	
	/*public void test1() throws TransformationException, FileNotFoundException {
		ProcessModel p = new ProcessModel();
		
		Activity b1 = new Activity("B1");
		Activity b2 = new Activity("B2");
		Activity e5 = new Activity("e5");
		Activity e6 = new Activity("e6");
	
		Gateway y = new AndGateway();
		Gateway z = new XorGateway();
		
		p.addFlowNode(b1);
		p.addFlowNode(b2);
		p.addFlowNode(e5);
		p.addFlowNode(e6);
		p.addFlowNode(y);
		p.addFlowNode(z);
		
		p.addControlFlow(b1,y);
		p.addControlFlow(b2,z);
		p.addControlFlow(y,z);
		p.addControlFlow(y,e5);
		p.addControlFlow(z,e6);
		
		IOUtils.toFile("model.dot", p.toDOT());
		
		NetSystem net = ProcessModel2NetSystem.transform(p);
		int cp = 1; int ct = 1;
		for (Place place : net.getPlaces()) place.setName("p"+cp++);
		for (Transition trans : net.getTransitions()) trans.setName("t"+ct++);
		IOUtils.toFile("net.dot", net.toDOT());
		
		SoundUnfoldingMSMS unf = new SoundUnfoldingMSMS(net);
		IOccurrenceNet bpnet = unf.getOccurrenceNet();
		IOUtils.toFile("unf.dot", bpnet.toDOT());
		IOUtils.toFile("unf_unsafe.dot", bpnet.toDOTcs(unf.getLocallyUnsafeConditions()));
		IOUtils.toFile("unf_deadlock.dot", bpnet.toDOTcs(unf.getLocalDeadlockConditions()));
		
		unf.completeOriginativeSystemWithCorrectInstantiations();
		IOUtils.toFile("net2.dot", net.toDOT());
		
		assertEquals(true, unf.isSound());
		assertEquals(0, unf.getLocalDeadlockConditions().size());
		assertEquals(6, unf.getLocallyUnsafeConditions().size());
	}*/
}
