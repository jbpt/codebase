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
package de.hpi.bpt.graph.algo.rpst;

import de.hpi.bpt.graph.abs.AbstractDirectedGraph;
import de.hpi.bpt.graph.abs.IDirectedEdge;
import de.hpi.bpt.graph.abs.IDirectedGraph;
import de.hpi.bpt.graph.algo.tctree.TCType;
import de.hpi.bpt.hypergraph.abs.IVertex;
import de.hpi.bpt.hypergraph.abs.Vertex;

public class RPSTNode<E extends IDirectedEdge<V>, V extends IVertex> extends Vertex {

	private boolean isQuasi = false;

	private V entry = null;
	
	private V exit = null;
	
	private TCType type = TCType.UNDEFINED;
	
	private RPSTSkeleton<E,V> skeleton = new RPSTSkeleton<E,V>();
	
	private AbstractDirectedGraph<E,V> fragment = new AbstractDirectedGraph<E,V>();
	
	public boolean isQuasi() {
		return isQuasi;
	}
	
	public IDirectedGraph<E,V> getFragment() {
		return (IDirectedGraph<E,V>) this.fragment;
	}

	protected void setQuasi(boolean isQuasi) {
		this.isQuasi = isQuasi;
	}
	
	public V getEntry() {
		return this.entry;
	}

	protected void setEntry(V entry) {
		this.entry = entry;
	}

	public V getExit() {
		return this.exit;
	}

	protected void setExit(V exit) {
		this.exit = exit;
	}
	
	public RPSTSkeleton<E,V> getSkeleton() {
		return this.skeleton;
	}
	
	public TCType getType() {
		return this.type;
	}
	
	protected void setType(TCType type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return (this.isQuasi ? "*" : "")+this.getName() + " [" + this.entry + "," + this.exit + "] - " + this.getSkeleton() + " - " + this.getSkeleton().getVirtualEdges() + " : " + this.getFragment();
	}

}
