package de.hpi.bpt.process.petri.test;

import java.io.FileNotFoundException;

import junit.framework.TestCase;
import de.hpi.bpt.process.Activity;
import de.hpi.bpt.process.AndGateway;
import de.hpi.bpt.process.Gateway;
import de.hpi.bpt.process.ProcessModel;
import de.hpi.bpt.process.XorGateway;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;
import de.hpi.bpt.process.petri.unf.OccurrenceNet;
import de.hpi.bpt.process.petri.unf.SoundUnfoldingMSMS;
import de.hpi.bpt.process.petri.unf.Utils;
import de.hpi.bpt.process.petri.util.TransformationException;
import de.hpi.bpt.utils.IOUtils;


public class SoundUnfoldingMSMSTest extends TestCase {
	
	public void test1() throws TransformationException, FileNotFoundException {
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
		
		Utils.toFile("model.dot", p.toDOT());
		
		PetriNet net = Utils.process2net(p);
		int cp = 1; int ct = 1;
		for (Place place : net.getPlaces()) place.setName("p"+cp++);
		for (Transition trans : net.getTransitions()) trans.setName("t"+ct++);
		IOUtils.toFile("net.dot", net.toDOT());
		
		SoundUnfoldingMSMS unf = new SoundUnfoldingMSMS(net);
		OccurrenceNet bpnet = unf.getOccurrenceNet();
		IOUtils.toFile("unf.dot", bpnet.toDOT());
		IOUtils.toFile("unf_unsafe.dot", bpnet.toDOTcs(unf.getLocallyUnsafeConditions()));
		IOUtils.toFile("unf_deadlock.dot", bpnet.toDOTcs(unf.getLocalDeadlockConditions()));
		
		assertEquals(true, unf.isSound());
		assertEquals(0, unf.getLocalDeadlockConditions().size());
		assertEquals(6, unf.getLocallyUnsafeConditions().size());
	}
}
