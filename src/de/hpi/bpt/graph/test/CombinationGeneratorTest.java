package de.hpi.bpt.graph.test;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;
import de.hpi.bpt.graph.algo.CombinationGenerator;

public class CombinationGeneratorTest extends TestCase{

	public void testSomeBehavior() {
		Collection<String> c = new ArrayList<String>();
		c.add("a"); c.add("b"); c.add("c"); c.add("d");
		
		CombinationGenerator<String> cg = new CombinationGenerator<String>(c, 3);
		while (cg.hasMore ()) {
			System.out.println(cg.getNextCombination());
		}
	}

}
