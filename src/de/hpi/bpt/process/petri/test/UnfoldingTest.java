package de.hpi.bpt.process.petri.test;

import java.io.FileNotFoundException;
import java.util.List;

import junit.framework.TestCase;
import de.hpi.bpt.process.Gateway;
import de.hpi.bpt.process.GatewayType;
import de.hpi.bpt.process.Process;
import de.hpi.bpt.process.Task;
import de.hpi.bpt.process.checks.structural.ProcessStructureChecker;
import de.hpi.bpt.process.petri.PNSerializer;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;
import de.hpi.bpt.process.petri.unf.OccurrenceNet;
import de.hpi.bpt.process.petri.unf.UnfoldingSetup;
import de.hpi.bpt.process.petri.unf.Unfolding;
import de.hpi.bpt.process.petri.unf.Utils;
import de.hpi.bpt.process.petri.util.TransformationException;
import de.hpi.bpt.process.serialize.Process2DOT;


public class UnfoldingTest extends TestCase {
	public void test1() throws TransformationException, FileNotFoundException {
		Process p = new Process();
		
		Task ti = new Task("I");
		Task to = new Task("O");
		Task ta = new Task("A");
		Task tb = new Task("B");
		Task tc = new Task("C");
		Task td = new Task("D");
	
		Gateway s1 = new Gateway(GatewayType.AND);
		Gateway j2 = new Gateway(GatewayType.XOR);
		Gateway s2 = new Gateway(GatewayType.XOR);
		Gateway j1 = new Gateway(GatewayType.AND);
		
		p.addTask(ti);
		p.addTask(to);
		p.addTask(ta);
		p.addTask(tb);
		p.addTask(tc);
		p.addTask(td);
		p.addGateway(s1);
		p.addGateway(s2);
		p.addGateway(j1);
		p.addGateway(j2);
		
		p.addControlFlow(ti,s1);
		p.addControlFlow(s1,ta);
		p.addControlFlow(s1,s2);
		p.addControlFlow(ta,j1);
		p.addControlFlow(s2,tb);
		p.addControlFlow(s2,tc);
		p.addControlFlow(tb,j2);
		p.addControlFlow(tc,j2);
		p.addControlFlow(j2,td);
		p.addControlFlow(td,j1);
		p.addControlFlow(j1,to);
		
		List<String> errors = ProcessStructureChecker.checkStructure(p);
		if (errors.size()>0)
			for (String e : errors) System.err.println(e);
		
		Utils.toFile("model.dot", Process2DOT.convert(p));
		
		PetriNet net = Utils.process2net(p);
		int cp = 1; int ct = 1;
		for (Place place : net.getPlaces()) place.setName("p"+cp++);
		for (Transition trans : net.getTransitions()) trans.setName("t"+ct++);
		Utils.addInitialMarking(net);
		//net.getMarkedPlaces().iterator().next().setTokens(2);
		PNSerializer.toDOT("net.dot",net);
		
		UnfoldingSetup conf = new UnfoldingSetup();
		//conf.MAX_EVENTS = 5;
		Unfolding bp = new Unfolding(net,conf);
		bp.printOrderingRelations();
		
		OccurrenceNet bpnet = bp.getOccurrenceNet();
		PNSerializer.toDOT("unf.dot",bpnet);
	}
	
	public void test2() throws TransformationException, FileNotFoundException {
		Process p = new Process();
		
		Task ti = new Task("I");
		Task to = new Task("O");
		Task ta = new Task("A");
		Task tb = new Task("B");
	
		Gateway s1 = new Gateway(GatewayType.XOR);
		Gateway j1 = new Gateway(GatewayType.XOR);
		
		p.addTask(ti);
		p.addTask(to);
		p.addTask(ta);
		p.addTask(tb);
		p.addGateway(s1);
		p.addGateway(j1);
		
		p.addControlFlow(ti,j1);
		p.addControlFlow(j1,ta);
		p.addControlFlow(ta,s1);
		p.addControlFlow(s1,tb);
		p.addControlFlow(tb,j1);
		p.addControlFlow(s1,to);
		
		List<String> errors = ProcessStructureChecker.checkStructure(p);
		if (errors.size()>0)
			for (String e : errors) System.err.println(e);
		
		Utils.toFile("model2.dot", Process2DOT.convert(p));
		
		PetriNet net = Utils.process2net(p);
		int cp = 1; int ct = 1;
		for (Place place : net.getPlaces()) place.setName("p"+cp++);
		for (Transition trans : net.getTransitions()) trans.setName("t"+ct++);
		Utils.addInitialMarking(net);
		PNSerializer.toDOT("net2.dot",net);
		
		Unfolding bp = new Unfolding(net);
		bp.printOrderingRelations();
		
		OccurrenceNet bpnet = bp.getOccurrenceNet();
		bpnet.toDOT("unf2.dot");
	}
}
