/**
 * Copyright (c) 2008 Artem Polyvyanyy
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
package de.hpi.bpt.process.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.hpi.bpt.hypergraph.abs.Vertex;

/**
 * 
 * @author Artem Polyvyanyy
 *
 */
public class ProcessEngine {
	
	public static void execute(IProcess process) throws IOException {
		while (!process.isTerminated()) {
			process.serialize();
			System.out.println("================================================================================");
			System.out.println("Current process state");
			System.out.println(process);
			
			List<Vertex> as = new ArrayList<Vertex>(process.getEnabledElements());
			System.out.println("--------------------------------------------------------------------------------");
			for (int i=0; i<as.size(); i++)
				System.out.println(String.format("%1s. %1s, %1s", i+1, as.get(i).getName(), as.get(i).getDescription()));
			System.out.println("--------------------------------------------------------------------------------");
			
			InputStreamReader isr = new InputStreamReader(System.in);
		    BufferedReader stdin = new BufferedReader(isr);
		    System.out.print("Select an element to fire: ");
		    String input = stdin.readLine();
		    int option = Integer.parseInt(input);
		    
		    process.fire(as.get(option-1));
		}
		
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println(process);
		process.serialize();
		System.out.println("Process terminated");
	}
}
