package de.hpi.bpt.process.serialize;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import de.hpi.bpt.process.ControlFlow;
import de.hpi.bpt.process.Gateway;
import de.hpi.bpt.process.GatewayType;
import de.hpi.bpt.process.Process;
import de.hpi.bpt.process.Task;

public class Process2DOT {
	
	/**
	 * @param p Process to serialize
	 * @param fileName Name of the file to serialize to
	 * @throws FileNotFoundException
	 */
	public static void convert(Process p, String fileName) throws FileNotFoundException {
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
