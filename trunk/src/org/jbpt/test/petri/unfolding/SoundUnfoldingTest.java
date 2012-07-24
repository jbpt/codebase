package org.jbpt.test.petri.unfolding;

import java.io.FileNotFoundException;

import junit.framework.TestCase;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;
import org.jbpt.petri.unfolding.OccurrenceNet;
import org.jbpt.petri.unfolding.SoundUnfolding;
import org.jbpt.pm.Activity;
import org.jbpt.pm.AndGateway;
import org.jbpt.pm.Gateway;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.XorGateway;
import org.jbpt.pm.structure.ProcessModel2NetSystem;
import org.jbpt.throwable.TransformationException;
import org.jbpt.utils.IOUtils;


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
		
		IOUtils.toFile("model.dot", p.toDOT());
		
		INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> net = ProcessModel2NetSystem.transform(p);
		int cp = 1; int ct = 1;
		for (IPlace place : net.getPlaces()) place.setName("p"+cp++);
		for (ITransition trans : net.getTransitions()) trans.setName("t"+ct++);
		net.loadNaturalMarking();
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
