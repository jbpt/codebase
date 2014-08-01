package org.jbpt.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jbpt.test.bp.BPSimTest;
import org.jbpt.test.bp.CBPComputationTest;
import org.jbpt.test.bp.CBPCreatorIdempotenceTest;
import org.jbpt.test.bp.RelSetAlgebraTest;
import org.jbpt.test.bp.RelSetComputationTest;
import org.jbpt.test.bp.RelSetLogCreatorTest;
import org.jbpt.test.petri.StateSpaceTest;
import org.jbpt.test.petri.unfolding.ProperCompletePrefixUnfoldingTest;
import org.jbpt.test.tree.BCTreeExtensiveTest;
import org.jbpt.test.tree.BCTreeTest;
import org.jbpt.test.tree.RPSTExtensiveTest;
import org.jbpt.test.tree.RPSTTest;
import org.jbpt.test.tree.TCTreeExtensiveTest;
import org.jbpt.test.tree.TCTreeTest;
import org.jbpt.test.tree.WFTreeTest;

public class AllTests {
	
	/**
	 * Please include here all tests that MUST run for every jBPT commit!
	 */
	public static Test suite() {		
		TestSuite suite = new TestSuite(AllTests.class.getName());
		
		// Behavioral Profile tests [BEGIN]
		suite.addTestSuite(RelSetAlgebraTest.class);
		suite.addTestSuite(CBPComputationTest.class);
		suite.addTestSuite(CBPCreatorIdempotenceTest.class);
		suite.addTestSuite(BPSimTest.class);
		suite.addTestSuite(RelSetComputationTest.class);
		suite.addTestSuite(RelSetLogCreatorTest.class);
		// Behavioral Profile tests [END]
		
		// Tests of jBPT trees [BEGIN]
		suite.addTestSuite(BCTreeExtensiveTest.class);
		suite.addTestSuite(BCTreeTest.class);
		suite.addTestSuite(RPSTExtensiveTest.class);
		suite.addTestSuite(RPSTTest.class);
		suite.addTestSuite(TCTreeExtensiveTest.class);
		suite.addTestSuite(TCTreeTest.class);
		suite.addTestSuite(WFTreeTest.class);
		// Tests of jBPT trees [END]
		
		// Tests of unfolding [BEGIN]		
		suite.addTestSuite(ProperCompletePrefixUnfoldingTest.class);
		// Tests of unfolding [END]
		
		// Tests of Petri nets [BEGIN]
		suite.addTestSuite(StateSpaceTest.class);
		// Tests of Petri nets [END]
		
		return suite;
	}
}
