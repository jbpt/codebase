package org.jbpt.test.graph;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ThousandTimesTest {

	public static Test suite() {
		TestSuite suite = new TestSuite(ThousandTimesTest.class.getName());
		//$JUnit-BEGIN$
		for (int i=0; i<1000; i++) {
			suite.addTestSuite(TCTreeTest.class);
		}
		//$JUnit-END$
		return suite;
	}

}
