package org.jbpt.test.graph;

import java.util.List;

import junit.framework.TestCase;

import org.jbpt.graph.algo.TreeTraversals;
import org.jbpt.graph.algo.bctree.BCTree;
import org.jbpt.graph.algo.bctree.BCTreeNode;
import org.jbpt.pm.Activity;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.Gateway;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.XorGateway;
import org.jbpt.utils.IOUtils;

public class BCTreeTest extends TestCase {
	
	/*public void testIsBiconnected() {
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
		
		BCTree<ControlFlow<FlowNode>,FlowNode> bctree = new BCTree<>(p);
		
		System.out.println(bctree.getArticulationPoints().size());
	}*/
	
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

		BCTree<ControlFlow<FlowNode>,FlowNode> bctree = new BCTree<ControlFlow<FlowNode>,FlowNode>(p);
		
		System.out.println(bctree.getArticulationPoints().size());
		System.out.println(bctree.getArticulationPoints());
		System.out.println(bctree.getBiconnectedComponents().size());
		
		for (BCTreeNode<ControlFlow<FlowNode>,FlowNode> node : bctree.getBiconnectedComponents()) {
			System.out.println(node.getBiconnectedComponent());
		}
		
		TreeTraversals<BCTreeNode<ControlFlow<FlowNode>,FlowNode>> traversals = new TreeTraversals<BCTreeNode<ControlFlow<FlowNode>,FlowNode>>();
		List<BCTreeNode<ControlFlow<FlowNode>,FlowNode>> postOrder = traversals.postOrderTraversal(bctree);
		
		System.out.println("---");
		for (BCTreeNode<ControlFlow<FlowNode>,FlowNode> n : postOrder) {
			System.out.println(n);
		}
		System.out.println("---");
		
		IOUtils.toFile("tree.dot", bctree.toDOT());
	}
}
