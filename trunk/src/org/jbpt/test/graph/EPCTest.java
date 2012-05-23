package org.jbpt.test.graph;

import org.jbpt.graph.algo.GraphAlgorithms;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.Event;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.Gateway;
import org.jbpt.pm.OrGateway;
import org.jbpt.pm.Resource;
import org.jbpt.pm.epc.Document;
import org.jbpt.pm.epc.Epc;
import org.jbpt.pm.epc.Function;
import org.jbpt.pm.epc.ProcessInterface;
import org.jbpt.pm.epc.XorConnector;

import junit.framework.TestCase;

/**
 * Let's test EPCs
 * 
 * @author Artem Polyvyanyy
 */
public class EPCTest extends TestCase {
	
	public void testSomeBehavior() {
		// Basic EPC (events,functions,connectors,process interfaces)
		Epc epc = new Epc();
		GraphAlgorithms<ControlFlow<FlowNode>, FlowNode> ga = new GraphAlgorithms<ControlFlow<FlowNode>, FlowNode>();
		
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

		Gateway c1 = new XorConnector();
		Gateway c2 = new XorConnector();
		
		ProcessInterface p1 = new ProcessInterface("P1");
		
		epc.addControlFlow(e1, f1);
		epc.addControlFlow(f1, c1);
		epc.addControlFlow(c1, e2);
		epc.addControlFlow(c1, e3);
		epc.addControlFlow(e2, f2);
		epc.addControlFlow(e3, f3);
		epc.addControlFlow(f2, e4);
		epc.addControlFlow(f3, e5);
		epc.addControlFlow(e4, c2);
		epc.addControlFlow(e5, c2);
		epc.addControlFlow(c2, f4);
		epc.addControlFlow(f4, e6);
		epc.addControlFlow(e6, p1);
		
		assertTrue(ga.isConnected(epc));
		
		epc.addFlowNode(e7);
		
		assertFalse(ga.isConnected(epc));
		
		epc.addControlFlow(p1, e7);
		
		assertTrue(ga.isConnected(epc));
		
		// get elements of the EPC
		assertEquals(4, epc.getFunctions().size());
		assertEquals(7, epc.getEvents().size());
		assertEquals(2, epc.getGateways().size());
		assertEquals(1, epc.getProcessInterfaces().size());
		assertEquals(14, epc.getControlFlow().size());
		assertEquals(14, epc.getFlowNodes().size());
		
		assertTrue(epc.getEntries().iterator().next().equals(e1));
		assertTrue(epc.getExits().iterator().next().equals(e7));
		
		assertTrue(c1.isSplit());
		assertFalse(c1.isJoin());
		assertFalse(c2.isSplit());
		assertTrue(c2.isJoin());
		
		epc.removeFlowNode(f2);
		assertTrue(ga.isConnected(epc));
		assertEquals(13, epc.getFlowNodes().size());
		assertEquals(12, epc.countEdges());
		assertEquals(2, epc.getEntries().size());
		assertEquals(2, epc.getExits().size());
		
		ControlFlow<FlowNode> cf15 = epc.addControlFlow(e2, e4);
		assertEquals(1, epc.getEntries().size());
		assertEquals(1, epc.getExits().size());
		
		assertNotNull(cf15.setTarget(c2));
		assertEquals(2, epc.getEntries().size());
		assertEquals(1, epc.getExits().size());
		
		assertEquals(3, epc.getIncomingControlFlow(c2).size());
		assertEquals(1, epc.getOutgoingControlFlow(c2).size());
		
		epc.getOutgoingControlFlow(c2);
		
		assertNotNull(cf15.setSource(c1));
		assertEquals(2, epc.getEntries().size());
		assertEquals(2, epc.getExits().size());
		
		assertEquals(1, epc.getIncomingControlFlow(c1).size());
		assertEquals(3, epc.getOutgoingControlFlow(c1).size());
		assertTrue(ga.isConnected(epc));
		
		assertNotNull(epc.removeFlowNode(f3));
		assertEquals(3, epc.getEntries().size());
		assertEquals(3, epc.getExits().size());
		
		assertEquals(2, epc.getFunctions().size());
		assertEquals(7, epc.getEvents().size());
		assertEquals(2, epc.getGateways().size());
		assertEquals(1, epc.getProcessInterfaces().size());
		assertEquals(11, epc.getControlFlow().size());
		assertEquals(12, epc.getFlowNodes().size());
		
		assertTrue(ga.isConnected(epc));
		
		assertNotNull(epc.removeControlFlow(cf15));
		assertEquals(10, epc.getControlFlow().size());
		
		assertFalse(ga.isConnected(epc));
		
		assertNull(cf15.setTarget(e5));
		assertEquals(10, epc.getControlFlow().size());
		
		Document d1 = new Document("D1");
		Document d2 = new Document("D2");
		assertNotNull(epc.addNonFlowNode(d1));
		assertEquals(1, epc.getNonFlowNodes().size());

		f1.addWriteDocument(d1);
		d1.addReadingFlowNode(f3);
		d2.addReadingFlowNode(f3);
		
		assertEquals(2, epc.getNonFlowNodes().size());
		
		assertEquals(2, epc.getInputNonFlowNodes(f3).size());
		assertEquals(0, epc.getOutputNonFlowNodes(f3).size());
		assertEquals(0, epc.getInputNonFlowNodes(f1).size());
		assertEquals(1, epc.getOutputNonFlowNodes(f1).size());
		
		assertEquals(1, epc.getOutputFlowNodes(d1).size());
		assertNotNull(epc.removeNonFlowNode(d1));
		assertEquals(1, epc.getNonFlowNodes().size());
	}
	
	public void testFiltering(){
		Epc epc = new Epc();
		Event e1 = new Event();
		Event e2 = new Event();
		Function f1 = new Function();
		Function f2 = new Function();
		Function f3 = new Function();
		Gateway g1 = new OrGateway();
		
		epc.addControlFlow(e1, f1);
		epc.addControlFlow(f1, g1);
		epc.addControlFlow(g1, f2);
		epc.addControlFlow(g1, f3);
		epc.addControlFlow(f2, e2);
		
		assertEquals(3, epc.filter(Function.class).size());		
	}
	
	public void testCloning(){
		Epc epc = new Epc();
		Event e1 = new Event();
		Function f1 = new Function();		
		Gateway g1 = new OrGateway();
		Document d1 = new Document();
		Resource r = new Resource();
		
		epc.addControlFlow(e1, f1);
		assertEquals(1, epc.countEdges());
		assertTrue(epc.getNonFlowNodes().isEmpty());
		
		Epc clonedEpc = (Epc) epc.clone();
		
		assertEquals(1, clonedEpc.countEdges());
		assertTrue(clonedEpc.getNonFlowNodes().isEmpty());
		
		clonedEpc.addNonFlowNode(d1);
		assertTrue(epc.getNonFlowNodes().isEmpty());
		
		clonedEpc.addControlFlow((FlowNode) clonedEpc.filter(Function.class).iterator().next(), g1);
		assertEquals(1, epc.countEdges());
		assertEquals(2, clonedEpc.countEdges());
		
		f1.addResource(r);
		for (FlowNode node : clonedEpc.getFlowNodes()){
			assertTrue(node.getResources().isEmpty());
		}
		
		clonedEpc.getFlowNodes().iterator().next().addReadWriteDocument(d1);
		assertEquals(1, clonedEpc.getFlowNodes(d1).size());
		assertTrue(epc.getFlowNodes(d1).isEmpty());
	}
}
