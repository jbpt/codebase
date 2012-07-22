package org.jbpt.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

public class IOUtils {
	
	public static void toFile(String fileName, String content) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
			out.write(content);
			out.close();
		}
		catch (IOException e) {
			System.err.println(e.getMessage());		
		}
	}

	/**
	 * Helper method: simply writes a line of a csv file.
	 * 
	 * @param out the writer 
	 * @param row the line as a string array
	 */
	public static void print(PrintWriter out, String[] row) {
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
	public static void writeResultsToFile(String fileName, String[] captions, Set<String[]> rows) {
		
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

}
