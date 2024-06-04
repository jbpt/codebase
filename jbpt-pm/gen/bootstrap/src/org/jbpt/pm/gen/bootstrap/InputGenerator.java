package org.jbpt.pm.gen.bootstrap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class InputGenerator {	
	public static void main(String[] args) throws IOException {
    	String dataFile = "./inputs.csv";
    	
    	File path = new File("./systems/");
	    File [] files = path.listFiles();
	    for (int i = 0; i < files.length; i++) {
	    	File f = files[i];
	    	if (!files[i].isFile()) continue;
	    	String fName = f.getName();
	    	if (!fName.endsWith("pnml")) continue;
	    	
	    	for (int logSize = 128; logSize <= 16384; logSize*=2) {					// 8 iterations [2^7, 2^8, ..., 2^15]
	        	for (int noiseLevel=0; noiseLevel<=4; noiseLevel++) {				// 5 iterations [0.00, 0.25, ..., 1.00]
	        		for (int k = 1; k <= 3; k++) {									// 3 iterations [1,2,3]
	        			for (int g=1024; g<=65536; g*=2) { 							// 7 iterations [2^10, 2^11, ..., 2^16]
	        				for (int n=1024; n<=65536; n*=2) { 						// 7 iterations [2^10, 2^11, ..., 2^16]
	        					for (int m=8; m<=64; m*=2) { 						// 4 iterations [8,16,32,64]
	        						appendToFile(dataFile,""+fName+","+logSize+","+(noiseLevel*0.25)+","+k+","+g+","+n+","+m);
	        					}
	        				}
	        			}
	        		}
	        	}
	        }
	    }
	}
	
	private static void appendToFile(String file, String content) throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
    	out.println(content);
    	out.close();
	}
}
