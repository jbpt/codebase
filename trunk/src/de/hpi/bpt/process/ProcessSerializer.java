/**
 * Copyright (c) 2010 Artem Polyvyanyy
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
