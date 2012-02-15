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
import de.hpi.bpt.process.petri.unf.SoundUnfolding;
import de.hpi.bpt.process.petri.unf.Utils;
import de.hpi.bpt.process.petri.util.TransformationException;
import de.hpi.bpt.utils.IOUtils;


public class SoundUnfoldingTest extends TestCase {
	
	public void test1() throws TransformationException, FileNotFoundException {
		ProcessModel p = new ProcessModel();
		
		Activity ti = new Activity("I");
		Activity to = new Activity("O");
		Activity ta = new Activity("A");
		Activity tb = new Activity("B");
		Activity tc = new Activity("C");
		Activity td = new Activity("D");
	
		Gateway s1 = new AndGateway();
		Gateway s2 = new AndGateway();
		Gateway s3 = new XorGateway();
		
		Gateway j1 = new XorGateway();
		Gateway j2 = new AndGateway();
		Gateway j3 = new AndGateway();
		
		p.addFlowNode(ti);
		p.addFlowNode(to);
		p.addFlowNode(ta);
		p.addFlowNode(tb);
		p.addFlowNode(tc);
		p.addFlowNode(td);
		p.addFlowNode(s1);
		p.addFlowNode(s2);
		p.addFlowNode(s3);
		p.addFlowNode(j1);
		p.addFlowNode(j2);
		p.addFlowNode(j3);
		
		p.addControlFlow(ti,s1);
		p.addControlFlow(s1,s2);
		p.addControlFlow(s1,s3);
		p.addControlFlow(s2,ta);
		p.addControlFlow(s2,tb);
		p.addControlFlow(ta,j1);
		p.addControlFlow(tb,j1);
		p.addControlFlow(s3,tc);
		p.addControlFlow(s3,td);
		p.addControlFlow(tc,j2);
		p.addControlFlow(td,j2);
		p.addControlFlow(j1,j3);
		p.addControlFlow(j2,j3);
		p.addControlFlow(j3,to);
		
		Utils.toFile("model.dot", p.toDOT());
		
		PetriNet net = Utils.process2net(p);
		int cp = 1; int ct = 1;
		for (Place place : net.getPlaces()) place.setName("p"+cp++);
		for (Transition trans : net.getTransitions()) trans.setName("t"+ct++);
		Utils.addInitialMarking(net);
		IOUtils.toFile("net.dot", net.toDOT());
		
		SoundUnfolding unf = new SoundUnfolding(net);
		OccurrenceNet bpnet = unf.getOccurrenceNet();
		IOUtils.toFile("unf.dot", bpnet.toDOT());
		IOUtils.toFile("unf_unsafe.dot", bpnet.toDOTcs(unf.getLocallyUnsafeConditions()));
		IOUtils.toFile("unf_deadlock.dot", bpnet.toDOTcs(unf.getLocalDeadlockConditions()));
		
		System.out.println(unf.getLocallyUnsafeConditions());
		System.out.println(unf.getLocalDeadlockConditions());
	}
}
