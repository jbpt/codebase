package org.jbpt.petri.test;

import java.io.FileNotFoundException;

import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.petri.unf.OccurrenceNet;
import org.jbpt.petri.unf.ProperUnfolding;
import org.jbpt.petri.unf.Utils;
import org.jbpt.petri.util.TransformationException;
import org.jbpt.pm.Activity;
import org.jbpt.pm.AndGateway;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.XorGateway;
import org.jbpt.utils.IOUtils;

import junit.framework.TestCase;


public class ProperUnfoldingTest extends TestCase {
	
	public void test1() throws TransformationException, FileNotFoundException {
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
		
		Utils.toFile("model1.dot", p.toDOT());
		
		PetriNet net = Utils.process2net(p);
		int cp = 1; int ct = 1;
		for (Place place : net.getPlaces()) place.setName("p"+cp++);
		for (Transition trans : net.getTransitions()) trans.setName("t"+ct++);
		Utils.addInitialMarking(net);
		IOUtils.toFile("net1.dot", net.toDOT());
		
		ProperUnfolding unf = new ProperUnfolding(net);
		OccurrenceNet bpnet = unf.getOccurrenceNet();
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
		
		Utils.toFile("model2.dot", p.toDOT());
		
		PetriNet net = Utils.process2net(p);
		int cp = 1; int ct = 1;
		for (Place place : net.getPlaces()) place.setName("p"+cp++);
		for (Transition trans : net.getTransitions()) trans.setName("t"+ct++);
		Utils.addInitialMarking(net);
		IOUtils.toFile("net2.dot", net.toDOT());
		
		ProperUnfolding unf = new ProperUnfolding(net);
		OccurrenceNet bpnet = unf.getOccurrenceNet();
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
		
		Utils.toFile("model3.dot", p.toDOT());
		
		PetriNet net = Utils.process2net(p);
		int cp = 1; int ct = 1;
		for (Place place : net.getPlaces()) place.setName("p"+cp++);
		for (Transition trans : net.getTransitions()) trans.setName("t"+ct++);
		Utils.addInitialMarking(net);
		IOUtils.toFile("net3.dot", net.toDOT());
		
		ProperUnfolding unf = new ProperUnfolding(net);
		OccurrenceNet bpnet = unf.getOccurrenceNet();
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
		
		Utils.toFile("model4.dot",p.toDOT());
		
		PetriNet net = Utils.process2net(p);
		int cp = 1; int ct = 1;
		for (Place place : net.getPlaces()) place.setName("p"+cp++);
		for (Transition trans : net.getTransitions()) trans.setName("t"+ct++);
		Utils.addInitialMarking(net);
		IOUtils.toFile("net4.dot", net.toDOT());
		
		ProperUnfolding unf = new ProperUnfolding(net);
		OccurrenceNet bpnet = unf.getOccurrenceNet();
		IOUtils.toFile("unf4.dot", bpnet.toDOT());
	}
}
