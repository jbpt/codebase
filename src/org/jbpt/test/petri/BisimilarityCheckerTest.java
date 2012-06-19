package org.jbpt.test.petri;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import org.jbpt.petri.util.BisimilarityChecker2;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.io.JSON2Process;
import org.jbpt.throwable.SerializationException;

import junit.framework.TestCase;

public class BisimilarityCheckerTest extends TestCase {

	protected static final String ORIGINAL_DIR = "models/process_json/acyclic/original";
	protected static final String STRUCTURED_DIR = "models/process_json/acyclic/structured";
	
	public void testComparison() {
		File originalDir = new File(ORIGINAL_DIR);
		File structuredDir = new File(STRUCTURED_DIR);
		HashSet<String> files = new HashSet<String>();
		for (String name:originalDir.list()) {
			if (name.endsWith(".json"))
				files.add(name);
		}
		boolean result = true;
		for (String name:structuredDir.list()) {
			if (name.endsWith(".json") && files.contains(name))
				try {
					ProcessModel original = loadProcess(ORIGINAL_DIR + File.separator + name);
					ProcessModel structured = loadProcess(STRUCTURED_DIR + File.separator + name);
					BisimilarityChecker2 ec = new BisimilarityChecker2(original, structured);
					//assertTrue("No bisimilar result for structuring of: " + name, ec.areBisimilar());
					if (ec.areBisimilar())
						System.out.println(name + " passed");
					else
						System.err.println(name + " failed");
					result &= ec.areBisimilar();
				} catch (Exception e) {
					System.err.println("Couldn't run test for file: " + name);
					e.printStackTrace();
				}
		}
		assertTrue("At least one example isn't bisimilar.", result);
	}
	
	protected ProcessModel loadProcess(String filename) throws SerializationException, IOException {
		String line;
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		reader.close();
		return JSON2Process.convert(sb.toString());
	}
		
}
