package de.hpi.bpt.graph.test;

import junit.framework.TestCase;
import de.hpi.bpt.graph.algo.tctree.BiconnectivityCheck;
import de.hpi.bpt.process.ControlFlow;
import de.hpi.bpt.process.Gateway;
import de.hpi.bpt.process.GatewayType;
import de.hpi.bpt.process.Node;
import de.hpi.bpt.process.Process;
import de.hpi.bpt.process.Task;

public class BiconnectivityCheckTest extends TestCase {
	
	public void testIsBiconnected() {
		//		  --- t3 --- t4 ---
		//		  |				  |
		// t1 -- s2 ------------ j5 -- t9
		//	|	  |				  |		|
		//	|	  |_ s6 ---- j7 __|		|
		// 	|		  |_ t8 _|			|
		//	----------------------------- 
		
		Process p = new Process();
		
		Task t1 = new Task("1");
		Task t3 = new Task("3");
		Task t4 = new Task("4");
		Task t8 = new Task("8");
		Task t9 = new Task("9");
		
		Gateway s2 = new Gateway(GatewayType.XOR, "2");
		Gateway s6 = new Gateway(GatewayType.XOR, "6");
		Gateway j7 = new Gateway(GatewayType.XOR, "7");
		Gateway j5 = new Gateway(GatewayType.XOR, "5");
		
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
		
		BiconnectivityCheck<ControlFlow, Node> check = new BiconnectivityCheck<ControlFlow, Node>(p);
		
		assertTrue(check.isBiconnected());
	}
	
	public void testIsNotBiconnected() {
		//		  --- t3 --- t4 ---
		//		  |				  |
		// t1 -- s2 ------------ j5 -- t9
		//		  |				  |		
		//		  |_ s6 ---- j7 __|		
		// 			  |_ t8 _|			 
		
		Process p = new Process();
		
		Task t1 = new Task("1");
		Task t3 = new Task("3");
		Task t4 = new Task("4");
		Task t8 = new Task("8");
		Task t9 = new Task("9");
		
		Gateway s2 = new Gateway(GatewayType.XOR, "2");
		Gateway s6 = new Gateway(GatewayType.XOR, "6");
		Gateway j7 = new Gateway(GatewayType.XOR, "7");
		Gateway j5 = new Gateway(GatewayType.XOR, "5");
		
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
		
		BiconnectivityCheck<ControlFlow, Node> check = new BiconnectivityCheck<ControlFlow, Node>(p);
		
		assertFalse(check.isBiconnected());
	}
}
