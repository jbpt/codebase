package de.hpi.bpt.graph.test;

import junit.framework.TestCase;
import de.hpi.bpt.process.ControlFlow;
import de.hpi.bpt.process.Process;
import de.hpi.bpt.process.SubProcess;
import de.hpi.bpt.process.Task;


public class ProcessTest extends TestCase {
	Process p = new Process();
	Process sp = new Process();
	
	public void testSomeBehavior() {
		Task t1 = new Task("T1");
		Task t2 = new Task("T2");
		SubProcess s1 = new SubProcess("S1");
		s1.setProcess(sp);
		
		ControlFlow cf1 = p.addControlFlow(t1,s1);
		ControlFlow cf2 = p.addControlFlow(s1,t2);
		
		assertNotNull(cf1);
		assertNotNull(cf2);
	}
}
