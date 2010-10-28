/**
 * Copyright (c) 2010 Christian Wiggert
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
package de.hpi.bpt.graph.algo.tctree;

import java.util.HashMap;

import de.hpi.bpt.hypergraph.abs.IVertex;

/**
 * This map is a convenient solution to store values for edges.
 * 
 * @author Christian Wiggert
 *
 */
public class NodeMap<V extends IVertex> extends HashMap<V, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -474286340181229387L;
	
	public int getInt(V node) {
		return (Integer) this.get(node);
	}
	
	public void setInt(V node, int i) {
		this.put(node, i);
	}
	
	public boolean getBool(V node) {
		return (Boolean) this.get(node);
	}
	
	public void setBool(V node, boolean flag) {
		this.put(node, flag);
	}
}
