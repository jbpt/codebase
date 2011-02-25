package de.hpi.bpt.graph.test;

import java.util.Collection;

import junit.framework.TestCase;
import de.hpi.bpt.graph.algo.GraphAlgorithms;
import de.hpi.bpt.process.epc.Connection;
import de.hpi.bpt.process.epc.Connector;
import de.hpi.bpt.process.epc.ConnectorType;
import de.hpi.bpt.process.epc.ControlFlow;
import de.hpi.bpt.process.epc.Document;
import de.hpi.bpt.process.epc.EPC;
import de.hpi.bpt.process.epc.Event;
import de.hpi.bpt.process.epc.FlowObject;
import de.hpi.bpt.process.epc.Function;
import de.hpi.bpt.process.epc.ProcessInterface;

/**
 * Let's test EPCs
 * 
 * @author Artem Polyvyanyy
 */
public class EPCTest extends TestCase {
	EPC epc = new EPC();
	GraphAlgorithms<ControlFlow, FlowObject> ga = new GraphAlgorithms<ControlFlow, FlowObject>();
	
	@SuppressWarnings("all")
	public void testSomeBehavior() {
		// Basic EPC (events,functions,connectors,process interfaces)
		
		Event e1 = new Event("E1");
		Event e2 = new Event("E2");
		Event e3 = new Event("E3");
		Event e4 = new Event("E4");
		Event e5 = new Event("E5");
		Event e6 = new Event("E6");
		Event e7 = new Event("E7");
		
		Function f1 = new Function("F1");
		Function f2 = new Function("F2");
		Function f3 = new Function("F3");
		Function f4 = new Function("F4");

		Connector c1 = new Connector(ConnectorType.XOR);
		Connector c2 = new Connector(ConnectorType.XOR);
		
		ProcessInterface p1 = new ProcessInterface("P1");
		
		ControlFlow cf1 = epc.addControlFlow(e1, f1);
		ControlFlow cf2 = epc.addControlFlow(f1, c1);
		ControlFlow cf3 = epc.addControlFlow(c1, e2);
		ControlFlow cf4 = epc.addControlFlow(c1, e3);
		ControlFlow cf5 = epc.addControlFlow(e2, f2);
		ControlFlow cf6 = epc.addControlFlow(e3, f3);
		ControlFlow cf7 = epc.addControlFlow(f2, e4);
		ControlFlow cf8 = epc.addControlFlow(f3, e5);
		ControlFlow cf9 = epc.addControlFlow(e4, c2);
		ControlFlow cf10 = epc.addControlFlow(e5, c2);
		ControlFlow cf11 = epc.addControlFlow(c2, f4);
		ControlFlow cf12 = epc.addControlFlow(f4, e6);
		ControlFlow cf13 = epc.addControlFlow(e6, p1);
		
		assertTrue(ga.isConnected(epc));
		
		epc.addFlowObject(e7);
		
		assertFalse(ga.isConnected(epc));
		
		ControlFlow cf14 = epc.addControlFlow(p1, e7);
		
		assertTrue(ga.isConnected(epc));
		
		// get elements of the EPC
		assertEquals(4, epc.getFunctions().size());
		assertEquals(7, epc.getEvents().size());
		assertEquals(2, epc.getConnectors().size());
		assertEquals(1, epc.getProcessInterfaces().size());
		assertEquals(14, epc.getControlFlow().size());
		assertEquals(14, epc.getFlowObjects().size());
		
		assertTrue(epc.getEntries().iterator().next().equals(e1));
		assertTrue(epc.getExits().iterator().next().equals(e7));
		
		assertTrue(epc.isSplit(c1));
		assertFalse(epc.isJoin(c1));
		assertFalse(epc.isSplit(c2));
		assertTrue(epc.isJoin(c2));
		
		epc.removeFlowObject(f2);
		assertTrue(ga.isConnected(epc));
		assertEquals(13, epc.getFlowObjects().size());
		assertEquals(12, epc.countEdges());
		assertEquals(2, epc.getEntries().size());
		assertEquals(2, epc.getExits().size());
		
		ControlFlow cf15 = epc.addControlFlow(e2, e4);
		assertEquals(1, epc.getEntries().size());
		assertEquals(1, epc.getExits().size());
		
		assertNotNull(cf15.setTarget(c2));
		assertEquals(2, epc.getEntries().size());
		assertEquals(1, epc.getExits().size());
		
		assertEquals(3, epc.getIncomingControlFlow(c2).size());
		assertEquals(1, epc.getOutgoingControlFlow(c2).size());
		
		Collection<ControlFlow> cf = epc.getOutgoingControlFlow(c2);
		
		assertNotNull(cf15.setSource(c1));
		assertEquals(2, epc.getEntries().size());
		assertEquals(2, epc.getExits().size());
		
		assertEquals(1, epc.getIncomingControlFlow(c1).size());
		assertEquals(3, epc.getOutgoingControlFlow(c1).size());
		assertTrue(ga.isConnected(epc));
		
		assertNotNull(epc.removeFlowObject(f3));
		assertEquals(3, epc.getEntries().size());
		assertEquals(3, epc.getExits().size());
		
		assertEquals(2, epc.getFunctions().size());
		assertEquals(7, epc.getEvents().size());
		assertEquals(2, epc.getConnectors().size());
		assertEquals(1, epc.getProcessInterfaces().size());
		assertEquals(11, epc.getControlFlow().size());
		assertEquals(12, epc.getFlowObjects().size());
		
		assertTrue(ga.isConnected(epc));
		
		assertNotNull(epc.removeControlFlow(cf15));
		assertEquals(10, epc.getControlFlow().size());
		
		assertFalse(ga.isConnected(epc));
		
		assertNull(cf15.setTarget(e5));
		assertEquals(10, epc.getControlFlow().size());
		
		Document d1 = new Document("D1");
		Document d2 = new Document("D2");
		assertNotNull(epc.addNonFlowObject(d1));
		assertEquals(1, epc.getNonFlowObjects().size());
		Connection cxn1 = epc.connectNonFlowObject(f1, d1);
		assertNotNull(cxn1);
		Connection cxn2 = epc.connectNonFlowObject(d1, f3);
		Connection cxn3 = epc.connectNonFlowObject(d2, f3);
		assertEquals(2, epc.getNonFlowObjects().size());
		
		assertEquals(2, epc.getInputNonFlowObjects(f3).size());
		assertEquals(0, epc.getOutputNonFlowObjects(f3).size());
		assertEquals(0, epc.getInputNonFlowObjects(f1).size());
		assertEquals(1, epc.getOutputNonFlowObjects(f1).size());
		
		assertEquals(1, epc.getOutputFlowObjects(d1).size());
		assertNotNull(epc.removeNonFlowObject(d1));
		assertEquals(1, epc.getNonFlowObjects().size());
	}
}
