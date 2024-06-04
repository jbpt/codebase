package org.jbpt.pm.gen.bootstrap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class InputGenerator2 {	
	public static void main(String[] args) throws IOException {
    	String dataFile = "./inputs_small.csv";
    	
    	File path = new File("./systems/");
	    File [] files = path.listFiles();
	    for (int i = 0; i < files.length; i++) {
	    	File f = files[i];
	    	if (!files[i].isFile()) continue;
	    	String fName = f.getName();
	    	if (!fName.endsWith("pnml")) continue;
	    	
	    	for (int logSize = 256; logSize <= 4096; logSize*=2) {					// +5 iterations [2^8, 2^9, ..., 2^12]
	        	for (int noiseLevel=0; noiseLevel<=4; noiseLevel+=2) {				// +3 iterations [0.0, 0.5, 1.0]
	        		for (int k = 1; k <= 3; k++) {									// +3 iterations [1,2,3]
	        			for (int g=32; g<=256; g*=2) { 							    // +4 iterations [2^5, 2^6, 2^7, 2^8]
	        				for (int n=1024; n<=65536; n*=2) { 						// +7 iterations [2^10, 2^11, ..., 2^16]
	        					if (!(n > 4*logSize)) continue;
	        					if (!(n > 8*g)) continue;
	        					for (int m=128; m<=128; m*=2) { 					// +1 iteration  [128]
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
