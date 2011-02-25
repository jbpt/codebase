package de.hpi.bpt.graph.test;

import java.io.BufferedReader;
import java.io.FileReader;

import junit.framework.TestCase;
import de.hpi.bpt.process.Process;

/**
 * 
 * @author Artem Polyvyanyy
 *
 */
public class ERDFTest extends TestCase {
	
	public void testSomeBehavior() throws Exception {
		String erdfString = ERDFTest.readFileAsString("erdf.xml");
		
		Process p = new Process();
		p.parseERDF(erdfString);
		
		System.out.println(p.getVertices());
		System.out.println(p.getEdges());
	}

	private static String readFileAsString(String filePath) throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}

}
