package de.hpi.bpt.process.petri.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import de.hpi.bpt.process.petri.CachePetriNet;
import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;

public class WoflanUtils {

	static int counter = 0;
	
	public static String formatId(String id) {
		id = id.replace(" ", "_");
		return id;
	}
	
	public static PetriNet parse(File file) throws IOException {
		PetriNet net = new CachePetriNet();
		
		try {
			FileReader input = new FileReader(file);
			BufferedReader bufRead = new BufferedReader(input);
			
			String line = bufRead.readLine();
			
			Map<String,Place> s2p = new HashMap<String, Place>();
			
			while (line != null) {
				// parse line
				line = line.trim();
				if (line.startsWith("place")) { // create place
					Place p = new Place();
					String label = "";
					if (line.contains("init")) {
						line = line.replace("  ", " ");
						label = line.substring(7, line.indexOf("init")-2);
						
						String tokens = line.substring(line.indexOf("init") + 5, line.indexOf(";"));
						p.setTokens(Integer.valueOf(tokens));
					}
					else label = line.substring(7, line.length()-2);
					
					p.setId(label);
					net.getPlaces().add(p);
					s2p.put(label,p);
				}
				
				if (line.startsWith("trans")) { // create place
					Transition t = new Transition();
					String label = line.substring(7, line.length()-1);
					t.setId(label);
					net.getTransitions().add(t);
					
					String lineIn = bufRead.readLine();
					lineIn = lineIn.trim();
					StringTokenizer st = new StringTokenizer(lineIn.substring(3));
					while (st.hasMoreTokens()) {
						String s = st.nextToken();
						net.addFlow(s2p.get(s.substring(1,s.length()-1)), t);
					}
	
					String lineOut = bufRead.readLine();
					lineOut = lineOut.trim();
					StringTokenizer st2 = new StringTokenizer(lineOut.substring(4));
					while (st2.hasMoreTokens()) {
						String s = st2.nextToken();
						net.addFlow(t, s2p.get(s.substring(1,s.length()-1)));
					}
				}
				
				line = bufRead.readLine();
			}
		} catch (Exception e) {
			//System.err.println(file.getAbsolutePath());
			//System.err.println(++counter);
			return null;
		}
	    
		return net;
	}
	
	public static void write(File file, PetriNet net) {
		
		try {
			FileWriter output = new FileWriter(file);
			BufferedWriter bufWriter = new BufferedWriter(output);
			
			bufWriter.write("\n");
			for (Place p : net.getPlaces()) {
				if (p.getTokens() > 0)
					bufWriter.write("place \"" + formatId(p.getId()) + "\" init "+p.getTokens()+";\n");
				else
					bufWriter.write("place \"" + formatId(p.getId()) + "\";\n");
			}
			bufWriter.write("\n");
			for (Transition t : net.getTransitions()) {
				bufWriter.write("trans \"" + formatId(t.getId()) + "\"\n");
				bufWriter.write("in ");
				for (Node n : net.getPredecessors(t)) {
					bufWriter.write("\"" + formatId(n.getId()) + "\" ");
				}
				bufWriter.write("\n");
				bufWriter.write("out ");
				for (Node n : net.getSuccessors(t)) {
					bufWriter.write("\"" + formatId(n.getId()) + "\" ");
				}
				bufWriter.write("\n;\n");
			}
			
			bufWriter.flush();
			bufWriter.close();
			output.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
