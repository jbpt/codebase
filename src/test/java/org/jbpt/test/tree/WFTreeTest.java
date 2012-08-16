package org.jbpt.test.tree;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import org.jbpt.algo.tree.rpst.IRPSTNode;
import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.petri.Flow;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.petri.wftree.WFTree;
import org.jbpt.petri.wftree.WFTreeBondType;
import org.jbpt.utils.IOUtils;

public class WFTreeTest extends TestCase {
	
	/**
	 * This test is taken from: 
	 * Matthias Weidlich, Artem Polyvyanyy, Jan Mendling, Mathias Weske: Causal Behavioural Profiles - Efficient Computation, Applications, and Evaluation. Fundam. Inform. 113(3-4): 399-435 (2011). 
	 * See Figure 6(a).
	 */
	public void testWSFM10_Figure6() {
		System.out.println("FUIN'11: Figure 6(a)");
		NetSystem net = new NetSystem();
		
		Place p1 = new Place("p1");
		Place p2 = new Place("p2");
		Place p3 = new Place("p3");
		Place p4 = new Place("p4");
		Place p5 = new Place("p5");
		Place p6 = new Place("p6");
		Place p7 = new Place("p7");
		Place p8 = new Place("p8");
		Place p9 = new Place("p9");
		Place p10 = new Place("p10");
		Place p11 = new Place("p11");
		Place p12 = new Place("p12");
		Place p13 = new Place("p13");
		
		Transition tA = new Transition("A");
		Transition tB = new Transition("B");
		Transition tC = new Transition("C");
		Transition tD = new Transition("D");
		Transition tE = new Transition("E");
		Transition tF = new Transition("F");
		Transition tG = new Transition("G");
		Transition tH = new Transition("H");
		Transition tI = new Transition("I");
		Transition tJ = new Transition("J");
		Transition tK = new Transition("K");
		
		net.addFlow(p1,tA);
		net.addFlow(tA,p2);
		net.addFlow(p2,tB);
		net.addFlow(tB,p3);
		net.addFlow(p3,tC);
		net.addFlow(tC,p2);
		net.addFlow(p3,tD);
		net.addFlow(tD,p4);
		net.addFlow(p3,tE);
		net.addFlow(tE,p4);
		net.addFlow(p4,tK);
		net.addFlow(tA,p5);
		net.addFlow(p5,tF);
		net.addFlow(tF,p6);
		net.addFlow(p6,tG);
		net.addFlow(tG,p7);
		net.addFlow(p7,tI);
		net.addFlow(tF,p8);
		net.addFlow(p8,tH);
		net.addFlow(tH,p9);
		net.addFlow(p9,tI);
		net.addFlow(tI,p10);
		net.addFlow(p10,tK);
		net.addFlow(tH,p11);
		net.addFlow(p11,tJ);
		net.addFlow(tJ,p12);
		net.addFlow(p12,tK);
		net.addFlow(tK,p13);
		
		assertTrue(PetriNet.STRUCTURAL_CHECKS.isWorkflowNet(net));
		
		WFTree wfTree = new WFTree(net);
		IOUtils.toFile("rpst.dot", wfTree.toDOT());
		//wfTree.debug();
		
		performBasicChecks(net,wfTree);
		assertEquals(44,wfTree.getRPSTNodes().size());
		assertEquals(28,wfTree.getRPSTNodes(TCType.TRIVIAL).size());
		assertEquals(3,wfTree.getRPSTNodes(TCType.BOND).size());
		assertEquals(12,wfTree.getRPSTNodes(TCType.POLYGON).size());
		assertEquals(1,wfTree.getRPSTNodes(TCType.RIGID).size());
		
		assertEquals(1,wfTree.getRPSTNodes(WFTreeBondType.LOOP).size());
		assertEquals(1,wfTree.getRPSTNodes(WFTreeBondType.PLACE_BORDERED).size());
		assertEquals(1,wfTree.getRPSTNodes(WFTreeBondType.TRANSITION_BORDERED).size());
		assertEquals(2,wfTree.getChildren(wfTree.getRPSTNodes(WFTreeBondType.LOOP).iterator().next()).size());
		assertEquals(4,wfTree.getDownwardPath(wfTree.getRoot(),wfTree.getRPSTNodes(WFTreeBondType.LOOP).iterator().next()).size());
	}
	
	private void performBasicChecks(NetSystem net, WFTree wfTree) {
		for (IRPSTNode<Flow,Node> node : wfTree.getRPSTNodes()) {
			assertTrue(net.getEdges().containsAll(node.getFragment()));
			
			Collection<Flow> edges = new ArrayList<Flow>();
			for (IRPSTNode<Flow,Node> child : wfTree.getChildren(node)) {
				edges.addAll(child.getFragment());
			}
			
			assertTrue(node.getFragment().containsAll(edges));
		}
	}
}
