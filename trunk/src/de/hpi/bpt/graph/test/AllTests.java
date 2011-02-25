package de.hpi.bpt.graph.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for de.hpi.bpt.graph.test");
		//$JUnit-BEGIN$
		suite.addTestSuite(ProcessTest.class);
		suite.addTestSuite(EPCTest.class);
		suite.addTestSuite(ERDFTest.class);
		suite.addTestSuite(CombinationGeneratorTest.class);
		suite.addTestSuite(MultiHyperGraphTest.class);
		suite.addTestSuite(DirectedGraphTest.class);
		suite.addTestSuite(MultiDirectedHyperGraphTest.class);
		suite.addTestSuite(DirectedFragmentsTest.class);
		suite.addTestSuite(HyperGraphTest.class);
		suite.addTestSuite(TCTreeTest.class);
		suite.addTestSuite(DirectedHyperGraphTest.class);
		suite.addTestSuite(GraphAlgorithmsTest_isConnected.class);
		suite.addTestSuite(BiconnectivityCheckTest.class);
		//$JUnit-END$
		return suite;
	}

}
