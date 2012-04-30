package org.jbpt.petri.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;

/**
 * Woflan parser/serializer
 * 
 * @author Artem Polyvyanyy, Matthias Weidlich
 */
public class WoflanSerializer {

	protected static int counter = 0;
	
	protected static String getId() {
		counter++;
		return String.valueOf(counter);
	}
	
	public static String formatId(String id) {
		id = id.replace(" ", "_");
		return id;
	}
	
	public static NetSystem parse(File file) {
		NetSystem sys = new NetSystem();
		
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
						sys.getMarking().put(p, Integer.valueOf(tokens));
					}
					else label = line.substring(7, line.length()-2);
					
					p.setId(getId());
					p.setName(label);
					sys.getPlaces().add(p);
					s2p.put(label,p);
				}
				
				if (line.startsWith("trans")) { // create place
					Transition t = new Transition();
					String label = line.substring(7, line.length()-1);
					t.setId(getId());
					t.setName(label);
					sys.getTransitions().add(t);
					
					String lineIn = bufRead.readLine();
					lineIn = lineIn.trim();
					StringTokenizer st = new StringTokenizer(lineIn.substring(3));
					while (st.hasMoreTokens()) {
						String s = st.nextToken();
						sys.addFlow(s2p.get(s.substring(1,s.length()-1)), t);
					}
	
					String lineOut = bufRead.readLine();
					lineOut = lineOut.trim();
					StringTokenizer st2 = new StringTokenizer(lineOut.substring(4));
					while (st2.hasMoreTokens()) {
						String s = st2.nextToken();
						sys.addFlow(t, s2p.get(s.substring(1,s.length()-1)));
					}
				}
				
				line = bufRead.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
		return sys;
	}
	
	public static void serialize(File file, NetSystem sys) {
		
		try {
			FileWriter output = new FileWriter(file);
			BufferedWriter bufWriter = new BufferedWriter(output);
			
			bufWriter.write("\n");
			for (Place p : sys.getPlaces()) {
				if (sys.getMarking().get(p) > 0)
					bufWriter.write("place \"" + formatId(p.getId()) + "\" init "+sys.getMarking().get(p)+";\n");
				else
					bufWriter.write("place \"" + formatId(p.getId()) + "\";\n");
			}
			bufWriter.write("\n");
			for (Transition t : sys.getTransitions()) {
				bufWriter.write("trans \"" + formatId(t.getId()) + "\"\n");
				bufWriter.write("in ");
				for (Node n : sys.getDirectPredecessors(t)) {
					bufWriter.write("\"" + formatId(n.getId()) + "\" ");
				}
				bufWriter.write("\n");
				bufWriter.write("out ");
				for (Node n : sys.getDirectSuccessors(t)) {
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
