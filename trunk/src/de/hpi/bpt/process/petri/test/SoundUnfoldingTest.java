package de.hpi.bpt.process.petri.test;

import java.io.FileNotFoundException;

import junit.framework.TestCase;
import de.hpi.bpt.process.Gateway;
import de.hpi.bpt.process.GatewayType;
import de.hpi.bpt.process.Process;
import de.hpi.bpt.process.Task;
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
		Process p = new Process();
		
		Task ti = new Task("I");
		Task to = new Task("O");
		Task ta = new Task("A");
		Task tb = new Task("B");
		Task tc = new Task("C");
		Task td = new Task("D");
	
		Gateway s1 = new Gateway(GatewayType.AND);
		Gateway s2 = new Gateway(GatewayType.AND);
		Gateway s3 = new Gateway(GatewayType.XOR);
		
		Gateway j1 = new Gateway(GatewayType.XOR);
		Gateway j2 = new Gateway(GatewayType.AND);
		Gateway j3 = new Gateway(GatewayType.AND);
		
		p.addTask(ti);
		p.addTask(to);
		p.addTask(ta);
		p.addTask(tb);
		p.addTask(tc);
		p.addTask(td);
		p.addGateway(s1);
		p.addGateway(s2);
		p.addGateway(s3);
		p.addGateway(j1);
		p.addGateway(j2);
		p.addGateway(j3);
		
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
