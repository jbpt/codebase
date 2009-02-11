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
