package de.hpi.bpt.process.petri.bp.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllCBPTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllCBPTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(RelSetAlgebraTest.class);
		suite.addTestSuite(CBPComputationTest.class);
		suite.addTestSuite(CBPCreatorIdempotenceTest.class);
		suite.addTestSuite(BPSimTest.class);
		suite.addTestSuite(RelSetComputationTest.class);
		suite.addTestSuite(RelSetLogCreatorTest.class);
		//$JUnit-END$
		return suite;
	}

}
