package org.jbpt.petri.behavior;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

/**
 * Abstract class for all kinds of analyses. Currently features utility functionality, 
 * such as 
 *  - methods to write csv files
 *  - average certain values given in a map
 * 
 * @author matthias.weidlich
 *
 */
public abstract class AbstractAnalysis {
	
	/**
	 * Helper method: simply writes a line of a csv file.
	 * 
	 * @param out the writer 
	 * @param row the line as a string array
	 */
	protected void print(PrintWriter out, String[] row) {
		int i=0;
		for (String str: row) {
			if ((++i) < row.length) {
				out.print(str);
				out.print(";");
			} else {
				out.println(str);
			}
		}
	}
	
	/**
	 * Writes the given data to a csv file.
	 * 
	 * @param fileName the name of the csv file
	 * @param captions a string array with the captions for the csv file
	 * @param rows a set of string arrays, the lines to write 
	 */
	protected void writeResultsToFile(String fileName, String[] captions, Set<String[]> rows) {
		
		try {
			FileOutputStream stream = new FileOutputStream(new File(fileName));
			PrintWriter out = new PrintWriter(stream);
	
			print(out, captions);
			
			for (String[] row : rows) {
				print(out, row);
			}
			
			out.flush();
			stream.close();
			System.out.println("Saved results to " + fileName);
			
		} catch (Exception e) {
			System.out.println("Failed to write the results to " + fileName);
			e.printStackTrace();
		}
	}
	
	/**
	 * Compute the average of the values given in a map for 
	 * a given set of map keys.
	 * 
	 * @param keys
	 * @param map
	 * @return the average of the values in the map for the given keys
	 */
	protected long average(Set<String> keys, Map<String,Long> map) {
		long sum = 0;
		for(String s : keys)
			sum += map.get(s);
		return Math.round(sum/keys.size());
	}


}
