/**
 * Copyright (c) 2008 Artem Polyvyanyy
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.hpi.bpt.graph.test;

import java.util.Iterator;

import junit.framework.TestCase;
import de.hpi.bpt.graph.algo.GraphAlgorithms;
import de.hpi.bpt.graph.algo.spqr.SPQRType;
import de.hpi.bpt.graph.algo.spqr.SPQRTree;
import de.hpi.bpt.graph.algo.spqr.SPQRTreeNode;
import de.hpi.bpt.process.ControlFlow;
import de.hpi.bpt.process.Gateway;
import de.hpi.bpt.process.GatewayType;
import de.hpi.bpt.process.Node;
import de.hpi.bpt.process.Process;
import de.hpi.bpt.process.Task;


public class SPQRTreeTest extends TestCase {

	//@SuppressWarnings("all")
	public void testSomeBehavior() {
		GraphAlgorithms<ControlFlow,Node> ga = new GraphAlgorithms<ControlFlow,Node>();
		
		// create process model graph
		Process p = new Process();
		
		Task t1 = new Task("T1");
		Task t2 = new Task("T2");
		Task t3 = new Task("T3");
		Task t4 = new Task("T4");
		Task t5 = new Task("T5");
		Task t6 = new Task("T6");
		Task t7 = new Task("T7");
		Task t8 = new Task("T8");
		Task t9 = new Task("T9");
		Task t10 = new Task("T10");
		Task t11 = new Task("T11");
		Task t12 = new Task("T12");
		Task t13 = new Task("T13");
		Task t14 = new Task("T14");
		
		Gateway s1 = new Gateway(GatewayType.XOR,"S1");
		Gateway s2 = new Gateway(GatewayType.XOR,"S2");
		Gateway s3 = new Gateway(GatewayType.XOR,"S3");
		Gateway j1 = new Gateway(GatewayType.XOR,"J1");
		Gateway j2 = new Gateway(GatewayType.XOR,"J2");
		Gateway j3 = new Gateway(GatewayType.XOR,"J3");
		
		ControlFlow e1 =  p.addControlFlow(t1, s1);
		ControlFlow e2 =  p.addControlFlow(s1, t2);
		ControlFlow e3 =  p.addControlFlow(s1, t3);
		ControlFlow e4 =  p.addControlFlow(s1, t10);
		ControlFlow e5 =  p.addControlFlow(t2, s2);
		ControlFlow e6 =  p.addControlFlow(t3, j1);
		ControlFlow e7 =  p.addControlFlow(t10, t11);
		ControlFlow e8 =  p.addControlFlow(s2, t4);
		ControlFlow e9 =  p.addControlFlow(s2, t6);
		ControlFlow e10 =  p.addControlFlow(s2, t7);
		ControlFlow e11 =  p.addControlFlow(s2, j1);
		ControlFlow e12 =  p.addControlFlow(j1, t9);
		ControlFlow e13 =  p.addControlFlow(t4, t5);
		ControlFlow e14 =  p.addControlFlow(t9, s3);
		ControlFlow e15 =  p.addControlFlow(s3, j1);
		ControlFlow e16 =  p.addControlFlow(t11, t12);
		ControlFlow e17 =  p.addControlFlow(t12, j3);
		ControlFlow e18 =  p.addControlFlow(s3, j2);
		ControlFlow e19 =  p.addControlFlow(j2, t13);
		ControlFlow e20 =  p.addControlFlow(t7, t8);
		ControlFlow e21 =  p.addControlFlow(t8, j2);
		ControlFlow e22 =  p.addControlFlow(t5, j3);
		ControlFlow e23 =  p.addControlFlow(t6, j3);
		ControlFlow e24 =  p.addControlFlow(t13, j3);
		ControlFlow e25 =  p.addControlFlow(j3, t14);
		
		assertTrue(ga.isConnected(p));
		assertEquals(2,ga.getBoundaryVertices(p).size());
		assertEquals(20,p.countVertices());
		assertEquals(25,p.countEdges());
		
		
		SPQRTree<ControlFlow,Node> spqr = new SPQRTree<ControlFlow,Node>(p);
		
		assertEquals(3, spqr.getVertices(SPQRType.P).size());
		assertEquals(1, spqr.getVertices(SPQRType.R).size());
		assertEquals(10, spqr.getVertices(SPQRType.S).size());
		
		assertEquals(14, spqr.getVertices().size());
		
		
		System.out.println("==================================================");
		System.out.println(spqr.getRoot().getName() + ": " + spqr.getRoot().getBoundaryNodes());
		System.out.println("==================================================");
		Iterator<SPQRTreeNode<ControlFlow,Node>> i = spqr.getVertices().iterator();
		while (i.hasNext()) {
			SPQRTreeNode<ControlFlow,Node> nTree = i.next();
			System.out.println(nTree.getName() + ": " + nTree.getBoundaryNodes() + " " + nTree.getEntry() + " -> " + nTree.getExit());
		}
		System.out.println("==================================================");
		
		/*TriconnectedAbstraction<ControlFlow, Node> ta = new TriconnectedAbstraction<ControlFlow, Node>(p);
		System.out.println(ta.getAbstractionCandidates(t1.getId()));
		System.out.println(ta.getAbstractionCandidates(t2.getId()));
		System.out.println(ta.getAbstractionCandidates(t3.getId()));
		System.out.println(ta.getAbstractionCandidates(t4.getId()));
		System.out.println(ta.getAbstractionCandidates(t5.getId()));
		System.out.println(ta.getAbstractionCandidates(t6.getId()));
		System.out.println(ta.getAbstractionCandidates(t7.getId()));
		System.out.println(ta.getAbstractionCandidates(t8.getId()));
		System.out.println(ta.getAbstractionCandidates(t9.getId()));
		System.out.println(ta.getAbstractionCandidates(t10.getId()));
		System.out.println(ta.getAbstractionCandidates(t11.getId()));
		System.out.println(ta.getAbstractionCandidates(t12.getId()));
		System.out.println(ta.getAbstractionCandidates(t13.getId()));
		System.out.println(ta.getAbstractionCandidates(t14.getId()));*/
	}
}
