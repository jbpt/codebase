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
import de.hpi.bpt.process.petri.unf.SoundUnfoldingMSMS;
import de.hpi.bpt.process.petri.unf.Utils;
import de.hpi.bpt.process.petri.util.TransformationException;
import de.hpi.bpt.process.serialize.Process2DOT;
import de.hpi.bpt.utils.IOUtils;


public class SoundUnfoldingMSMSTest extends TestCase {
	
	public void test1() throws TransformationException, FileNotFoundException {
		Process p = new Process();
		
		Task b1 = new Task("B1");
		Task b2 = new Task("B2");
		Task e5 = new Task("e5");
		Task e6 = new Task("e6");
	
		Gateway y = new Gateway(GatewayType.AND);
		Gateway z = new Gateway(GatewayType.XOR);
		
		p.addTask(b1);
		p.addTask(b2);
		p.addTask(e5);
		p.addTask(e6);
		p.addGateway(y);
		p.addGateway(z);
		
		p.addControlFlow(b1,y);
		p.addControlFlow(b2,z);
		p.addControlFlow(y,z);
		p.addControlFlow(y,e5);
		p.addControlFlow(z,e6);
		
		Utils.toFile("model.dot", Process2DOT.convert(p));
		
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
		
		System.out.println(unf.getLocallyUnsafeConditions());
		System.out.println(unf.getLocalDeadlockConditions());
	}
}
