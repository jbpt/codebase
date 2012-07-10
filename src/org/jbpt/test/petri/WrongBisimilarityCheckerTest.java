package org.jbpt.test.petri;

import java.io.File;
import java.util.HashSet;

import org.jbpt.petri.behavior.BisimilarityChecker2;
import org.jbpt.pm.ProcessModel;


public class WrongBisimilarityCheckerTest extends BisimilarityCheckerTest {
	
	private static final String WRONG_DIR = "models/process_json/acyclic/wrong";
	
	@Override
	public void testComparison() {
		File originalDir = new File(ORIGINAL_DIR);
		File wrongDir = new File(WRONG_DIR);
		HashSet<String> files = new HashSet<String>();
		for (String name:originalDir.list()) {
			if (name.endsWith(".json"))
				files.add(name);
		}
		for (String name:wrongDir.list()) {
			if (name.endsWith(".json") && files.contains(name))
				try {
					ProcessModel original = loadProcess(ORIGINAL_DIR + File.separator + name);
					ProcessModel wrong = loadProcess(WRONG_DIR + File.separator + name);
					BisimilarityChecker2 ec = new BisimilarityChecker2(original, wrong);
					assertFalse("The result shouldn't be bisimilar: " + name, ec.areBisimilar());
					System.out.println(name + " passed");
				} catch (Exception e) {
					System.err.println("Couldn't run test for file: " + name);
					e.printStackTrace();
				}
		}
	}
	
}
