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

import de.hpi.bpt.graph.abs.IEdge;
import de.hpi.bpt.hypergraph.abs.IVertex;

/**
 * This map is a convenient solution to store values for edges.
 * 
 * @author Christian Wiggert
 *
 */
public class EdgeMap<E extends IEdge<V>, V extends IVertex> extends HashMap<E, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3122883772335954023L;
	
	public int getInt(E edge) {
		return (Integer) this.get(edge);
	}
	
	public void setInt(E edge, int i) {
		this.put(edge, i);
	}
	
	public boolean getBool(E edge) {
		if (this.get(edge) == null)
			return false;
		return (Boolean) this.get(edge);
	}
	
	public void setBool(E edge, boolean flag) {
		this.put(edge, flag);
	}
	
	public void initialiseWithFalse() {
		for (E edge:this.keySet()) {
			this.put(edge, false);
		}
	}
}
