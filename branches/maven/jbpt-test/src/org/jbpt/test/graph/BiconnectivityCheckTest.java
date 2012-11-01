package org.jbpt.test.graph;

import org.jbpt.algo.tree.tctree.BiconnectivityCheck;
import org.jbpt.pm.Activity;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.Gateway;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.XorGateway;

import junit.framework.TestCase;

public class BiconnectivityCheckTest extends TestCase {
	
	public void testIsBiconnected() {
		//		  --- t3 --- t4 ---
		//		  |				  |
		// t1 -- s2 ------------ j5 -- t9
		//	|	  |				  |		|
		//	|	  |_ s6 ---- j7 __|		|
		// 	|		  |_ t8 _|			|
		//	----------------------------- 
		
		ProcessModel p = new ProcessModel();
		
		Activity t1 = new Activity("1");
		Activity t3 = new Activity("3");
		Activity t4 = new Activity("4");
		Activity t8 = new Activity("8");
		Activity t9 = new Activity("9");
		
		Gateway s2 = new XorGateway("2");
		Gateway s6 = new XorGateway("6");
		Gateway j7 = new XorGateway("7");
		Gateway j5 = new XorGateway("5");
		
		p.addControlFlow(t1, s2);
		p.addControlFlow(s2, t3);
		p.addControlFlow(s2, s6);
		p.addControlFlow(s2, j5);
		p.addControlFlow(t3, t4);
		p.addControlFlow(t4, j5);
		p.addControlFlow(s6, j7);
		p.addControlFlow(s6, t8);
		p.addControlFlow(t8, j7);
		p.addControlFlow(j7, j5);
		p.addControlFlow(j5, t9);
		p.addControlFlow(t9, t1);
		
		BiconnectivityCheck<ControlFlow<FlowNode>, FlowNode> check = new BiconnectivityCheck<ControlFlow<FlowNode>, FlowNode>(p);
		
		assertTrue(check.isBiconnected());
	}
	
	public void testIsNotBiconnected() {
		//		  --- t3 --- t4 ---
		//		  |				  |
		// t1 -- s2 ------------ j5 -- t9
		//		  |				  |		
		//		  |_ s6 ---- j7 __|		
		// 			  |_ t8 _|			 
		
		ProcessModel p = new ProcessModel();
		
		Activity t1 = new Activity("1");
		Activity t3 = new Activity("3");
		Activity t4 = new Activity("4");
		Activity t8 = new Activity("8");
		Activity t9 = new Activity("9");
		
		Gateway s2 = new XorGateway("2");
		Gateway s6 = new XorGateway("6");
		Gateway j7 = new XorGateway("7");
		Gateway j5 = new XorGateway("5");
		
		p.addControlFlow(t1, s2);
		p.addControlFlow(s2, t3);
		p.addControlFlow(s2, s6);
		p.addControlFlow(s2, j5);
		p.addControlFlow(t3, t4);
		p.addControlFlow(t4, j5);
		p.addControlFlow(s6, j7);
		p.addControlFlow(s6, t8);
		p.addControlFlow(t8, j7);
		p.addControlFlow(j7, j5);
		p.addControlFlow(j5, t9);
		
		BiconnectivityCheck<ControlFlow<FlowNode>, FlowNode> check = new BiconnectivityCheck<ControlFlow<FlowNode>, FlowNode>(p);
		
		assertFalse(check.isBiconnected());
	}
}
