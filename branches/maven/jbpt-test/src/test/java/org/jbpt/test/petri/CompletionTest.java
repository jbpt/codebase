package org.jbpt.test.petri;

import junit.framework.TestCase;

import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.pm.Activity;
import org.jbpt.pm.AndGateway;
import org.jbpt.pm.Gateway;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.XorGateway;
import org.jbpt.pm.structure.ProcessModel2NetSystem;
import org.jbpt.throwable.TransformationException;
import org.jbpt.utils.IOUtils;

public class CompletionTest extends TestCase {

	public void test() throws TransformationException {
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
		
		NetSystem sys = ProcessModel2NetSystem.transform(p);
		int cp = 1; int ct = 1;
		for (Place place : sys.getPlaces()) place.setName("p"+cp++);
		for (Transition trans : sys.getTransitions()) trans.setName("t"+ct++);
		IOUtils.toFile("net.dot", sys.toDOT());
		
		/*Completion comp = new Completion();
		comp.completeSources(sys);
		IOUtils.toFile("net2.dot", sys.toDOT());
		comp.completeSinks(sys);
		IOUtils.toFile("net3.dot", sys.toDOT());*/
	}
}
