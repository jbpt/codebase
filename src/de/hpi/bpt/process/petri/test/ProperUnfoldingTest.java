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
import de.hpi.bpt.process.petri.unf.ProperUnfolding;
import de.hpi.bpt.process.petri.unf.Utils;
import de.hpi.bpt.process.petri.util.TransformationException;
import de.hpi.bpt.process.serialize.Process2DOT;
import de.hpi.bpt.utils.IOUtils;


public class ProperUnfoldingTest extends TestCase {
	
	public void test1() throws TransformationException, FileNotFoundException {
		Process p = new Process();
		
		Task ti = new Task("I");
		Task to = new Task("O");
		Task ta = new Task("A");
		Task tb = new Task("B");
		Task tc = new Task("C");
		Task td = new Task("D");
		Task te = new Task("E");
		Task tf = new Task("F");
		Task tg = new Task("G");
		Task th = new Task("H");
		Task tj = new Task("J");
		Task tk = new Task("K");
	
		Gateway gq = new Gateway(GatewayType.XOR);
		Gateway gr = new Gateway(GatewayType.XOR);
		Gateway gs = new Gateway(GatewayType.XOR);
		Gateway gt = new Gateway(GatewayType.XOR);
		Gateway gu = new Gateway(GatewayType.XOR);
		Gateway gv = new Gateway(GatewayType.AND);
		Gateway gw = new Gateway(GatewayType.AND);
		Gateway gx = new Gateway(GatewayType.XOR);
		Gateway gy = new Gateway(GatewayType.XOR);
		Gateway gz = new Gateway(GatewayType.AND);
		
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
		
		Utils.toFile("model1.dot", Process2DOT.convert(p));
		
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
		Process p = new Process();
		
		Task ti = new Task("I");
		Task to = new Task("O");
		Task ta = new Task("A");
		Task tb = new Task("B");
		Task tc = new Task("C");
		Task td = new Task("D");
		Task te = new Task("E");
		Task tf = new Task("F");
		Task tg = new Task("G");
		Task th = new Task("H");
		Task tj = new Task("J");
		Task tk = new Task("K");
	
		Gateway gq = new Gateway(GatewayType.AND);
		Gateway gr = new Gateway(GatewayType.XOR);
		Gateway gs = new Gateway(GatewayType.XOR);
		Gateway gt = new Gateway(GatewayType.XOR);
		Gateway gu = new Gateway(GatewayType.AND);
		Gateway gv = new Gateway(GatewayType.AND);
		Gateway gw = new Gateway(GatewayType.XOR);
		Gateway gx = new Gateway(GatewayType.XOR);
		Gateway gy = new Gateway(GatewayType.AND);
		Gateway gz = new Gateway(GatewayType.AND);
		
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
		
		Utils.toFile("model2.dot", Process2DOT.convert(p));
		
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
}
