package org.jbpt.petri.behavior;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

public abstract class AbstractAnalysis {
	
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
	
	protected long average(Set<String> keys, Map<String,Long> map) {
		long sum = 0;
		for(String s : keys)
			sum += map.get(s);
		return Math.round(sum/keys.size());
	}


}
