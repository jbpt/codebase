package org.jbpt.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

public class IOUtils {

	private static final String DEFAULT_GRAPHVIZ_DEFAULT_PATH = "dot";
	private static final String DEFAULT_GRAPHVIZ_WINDOWS_PATH = "C://Program Files (x86)//Graphviz 2.28//bin//dot.exe";
	private static final String DEFAULT_GRAPHVIZ_LINUX_PATH = "/usr/bin/dot";
	
	private static final String TARGET_PREFIX = "target/";
	
	public static void toFile(String fileName, String content) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(TARGET_PREFIX + fileName));
			out.write(content);
			out.close();
		}
		catch (IOException e) {
			System.err.println(e.getMessage());		
		}
	}
	
	/**
	 * Invoke DOT and saves the created PNG to fileName.
	 * 
	 * @param fileName
	 *            of the resulting PNG file
	 * @param dotSource
	 *            graph in DOT format to save as PNG
	 * @param extraDotParameters
	 *            array of additional parameters to pass as arguments to DOT
	 * @throws IOException
	 *             in case the DOT binary is not found or the file can't be
	 *             written
	 */
	public static void invokeDOT(String fileName, String dotSource,
			String... extraDotParameters) throws IOException {
		final String[] args;
		if (extraDotParameters != null && extraDotParameters.length > 0) {
			args = new String[extraDotParameters.length+1];
			// DOT binary has to be 1st parameter
			args[0] = getDOTPath();
			// Add extra parameters
			for (int i = 0; i < extraDotParameters.length; i++) {
				args[i+1] = extraDotParameters[i];
			}
		} else {
			// Use default settings
			args = new String[] { getDOTPath(), "-Eshape=normal",
					"-Nshape=ellipse", "-Tpng"};
		}
		final ProcessBuilder pb = new ProcessBuilder(args);
		pb.redirectOutput(new File(fileName));
		pb.redirectErrorStream(true);
		final Process dotProcess = pb.start();
		BufferedWriter out = new BufferedWriter(new PrintWriter(
				dotProcess.getOutputStream()));
		out.write(dotSource);
		out.flush();
	}
	
	private static String getDOTPath() throws IOException {
		if (DEFAULT_GRAPHVIZ_DEFAULT_PATH != null) {
			if (new File(DEFAULT_GRAPHVIZ_DEFAULT_PATH).exists()) {
				return DEFAULT_GRAPHVIZ_DEFAULT_PATH;
			}
		}

		if (DEFAULT_GRAPHVIZ_LINUX_PATH != null) {
			if (new File(DEFAULT_GRAPHVIZ_LINUX_PATH).exists()) {
				return DEFAULT_GRAPHVIZ_LINUX_PATH;
			}
		}

		if (DEFAULT_GRAPHVIZ_WINDOWS_PATH != null) {
			if (new File(DEFAULT_GRAPHVIZ_WINDOWS_PATH).exists()) {
				return DEFAULT_GRAPHVIZ_WINDOWS_PATH;
			}
		}

		throw new IOException("Can not find Graphviz binary!");
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
			System.out.println("Saved results to " + fileName);} catch (Exception e) {
			System.out.println("Failed to write the results to " + fileName);
			e.printStackTrace();
		}
	}

}
