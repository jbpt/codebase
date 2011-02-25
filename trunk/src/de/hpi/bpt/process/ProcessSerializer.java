package de.hpi.bpt.process;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class ProcessSerializer {
	public static void toDOT(String fileName, Process p) throws FileNotFoundException {
		PrintStream out = new PrintStream(fileName);
		
		out.println("digraph G {");
		for (Task t : p.getTasks())
			out.printf("\tn%s[shape=box,label=\"%s\"];\n", t.getId().replace("-", ""), t.getName());
		for (Gateway g : p.getGateways(GatewayType.AND))
			out.printf("\tn%s[shape=diamond,label=\"%s\"];\n", g.getId().replace("-", ""), "AND");
		for (Gateway g : p.getGateways(GatewayType.XOR))
			out.printf("\tn%s[shape=diamond,label=\"%s\"];\n", g.getId().replace("-", ""), "XOR");
		for (Gateway g : p.getGateways(GatewayType.OR))
			out.printf("\tn%s[shape=diamond,label=\"%s\"];\n", g.getId().replace("-", ""), "OR");
		for (Gateway g : p.getGateways(GatewayType.UNDEFINED))
			out.printf("\tn%s[shape=diamond,label=\"%s\"];\n", g.getId().replace("-", ""), "?");
		
		for (ControlFlow cf: p.getControlFlow()) {
			out.printf("\tn%s->n%s;\n", cf.getSource().getId().replace("-", ""), cf.getTarget().getId().replace("-", ""));
		}
		out.println("}");
		
		out.close();
	}
}
