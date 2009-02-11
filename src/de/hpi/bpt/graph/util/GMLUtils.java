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
package de.hpi.bpt.graph.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.hpi.bpt.graph.abs.IEdge;
import de.hpi.bpt.graph.abs.IGraph;
import de.hpi.bpt.hypergraph.abs.IVertex;

/**
 * 
 * @author Artem Polyvyanyy
 *
 * @param <E>
 * @param <V>
 */
public class GMLUtils<E extends IEdge<V>, V extends IVertex> {
	
	public String serialize(IGraph<E,V> g) {
		if (g == null) return "";
		StringBuilder sb = new StringBuilder();
		
		Map<String,String> ids = new HashMap<String,String>();
		
		int i=0;
		Collection<V> vs = g.getVertices();
		Iterator<V> iv = vs.iterator();
		while (iv.hasNext()) {
			V v = iv.next();
			Integer integer = new Integer(i++);
			ids.put(v.getId(), integer.toString());
			sb.append(String.format("node [ id %s label \"%s\" ]\n", integer.toString(), v.getName()));
		}
		
		Collection<E> es = g.getEdges();
		Iterator<E> ie = es.iterator();
		while (ie.hasNext()) {
			E e = ie.next();
			sb.append(String.format("edge [ source %1s target %2s ]\n", ids.get(e.getV1().getId()),  ids.get(e.getV2().getId())));
		}
		
		return String.format("Creator \"jbpt.jar\"\ndirected 0\ngraph [\n%1s]\n", sb.toString());
	}
	
	public void serialize(IGraph<E,V> g, String fileName) {
		if (g == null) return;
		String gml = this.serialize(g);
		
		try{
			FileWriter fstream = new FileWriter(fileName);
	        BufferedWriter out = new BufferedWriter(fstream);
	        out.write(gml);
	        out.close();
    	} catch (Exception e){
    		System.err.println("Error: " + e.getMessage());
    	}
	}
}
