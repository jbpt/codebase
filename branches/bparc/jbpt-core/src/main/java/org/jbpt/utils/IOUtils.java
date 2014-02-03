package org.jbpt.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class IOUtils {

	private static final String DEFAULT_GRAPHVIZ_DEFAULT_PATH = "dot";
	private static final String DEFAULT_GRAPHVIZ_WINDOWS_PATH = "C://Program Files (x86)//Graphviz 2.28//bin//dot.exe";
	private static final String DEFAULT_GRAPHVIZ_LINUX_PATH = "/usr/bin/dot";
	
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
	public static void invokeDOT(String directory, String fileName, String dotSource,
			String... extraDotParameters) throws IOException {
		final String[] args;
		if (extraDotParameters != null && extraDotParameters.length > 0 
				&&  !extraDotParameters[0].isEmpty()) {
			args = new String[extraDotParameters.length+4];
			// DOT binary has to be 1st parameter
			args[0] = getDOTPath();
			// Add extra parameters
			for (int i = 0; i < extraDotParameters.length; i++) {
				args[i+1] = extraDotParameters[i];
			}
			args[extraDotParameters.length] = "-Tpng";
			args[extraDotParameters.length+1] = "-v";
			args[extraDotParameters.length+2] = "-o"+fileName; 
		} else {
			// Use default settings
			args = new String[] { getDOTPath(), "-Eshape=normal",
					"-Nshape=ellipse", "-Tpng", "-v", "-o"+fileName};
		}
		
		final ProcessBuilder pb = new ProcessBuilder(args);
		pb.directory(new File(directory));
		pb.redirectErrorStream(true);
		
		final Process dotProcess = pb.start();
		BufferedWriter out = new BufferedWriter(new PrintWriter(
				dotProcess.getOutputStream()));
		out.write(dotSource);
		out.flush();
		out.close();
				
		BufferedReader br = new BufferedReader(new InputStreamReader(dotProcess.getInputStream()));
		
		@SuppressWarnings("unused")
		int line;
		while ((line = br.read()) != -1) {
			//NoOp just consume every output silently
		}

		//Wait until Graphviz finishes
		try {
		    dotProcess.waitFor();
		} catch (InterruptedException e) {
		    Thread.interrupted();
		}
	}
	
	public static void invokeDOT(String directory, String fileName, String dotSource) throws IOException {
		invokeDOT(directory, fileName, dotSource, new String[0]);
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
				out.print(",");
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
	public static void writeResultsToFile(String fileName, String[] captions, Collection<String[]> rows) {
		
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
	
    public static Document loadDocumentFromFile(String fileName) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new File(fileName));

		return document;
	}
	
	public static void saveDocumentToFile(Document doc, String fileName) throws TransformerFactoryConfigurationError, FileNotFoundException, TransformerException {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		DOMSource        source = new DOMSource( doc );
		FileOutputStream os     = new FileOutputStream( new File(fileName) );
		StreamResult     result = new StreamResult( os );
		transformer.transform( source, result );
	}
}
