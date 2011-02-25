package de.hpi.bpt.process.petri;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class PNSerializer {
	
	public static void toDOT(String fileName, PetriNet net) throws FileNotFoundException {
		PrintStream out = new PrintStream(fileName);
		
		out.println("digraph G {");
		for (Transition t : net.getTransitions())
			out.printf("\tn%s[shape=box,label=\"%s\"];\n", t.getId().replace("-", ""), t.getName());
		for (Place n : net.getPlaces())
			out.printf("\tn%s[shape=circle,label=\"%s\"];\n", n.getId().replace("-", ""), n.getName());
		
		for (Flow f: net.getFlowRelation()) {
			out.printf("\tn%s->n%s;\n", f.getSource().getId().replace("-", ""), f.getTarget().getId().replace("-", ""));
		}
		out.println("}");
		
		out.close();
	}
}
