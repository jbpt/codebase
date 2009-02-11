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
package de.hpi.bpt.graph.algo.spqr;

import java.util.ArrayList;
import java.util.Collection;

import de.hpi.bpt.graph.abs.IEdge;
import de.hpi.bpt.hypergraph.abs.IVertex;
import de.hpi.bpt.hypergraph.abs.Vertex;

/**
 * 
 * @author Artem Polyvyanyy
 *
 */
public class SPQRTreeNode<E extends IEdge<V>, V extends IVertex> extends Vertex {
	
	private SPQRType type = SPQRType.UNDEFINED;
	
	private SPQRTreeSkeleton<E,V> skeleton;
	
	private Collection<V> boundary = new ArrayList<V>();
	
	private V entry = null;
	
	private V exit = null;
	
	/**
	 * Constructor
	 */
	public SPQRTreeNode() {
		super();
	}
	
	/**
	 * Constructor
	 * @param name Node name
	 */
	public SPQRTreeNode(String name) {
		super(name);
	}

	public SPQRType getType() {
		return type;
	}

	public void setType(SPQRType type) {
		this.type = type;
	}
	
	public SPQRTreeSkeleton<E,V> getSkeleton() {
		return this.skeleton;
	}

	public void setSkeleton(SPQRTreeSkeleton<E,V> skeleton) {
		this.skeleton = skeleton;
	}
	
	public Collection<V> getBoundaryNodes() {
		return new ArrayList<V>(this.boundary);
	}

	public void setBoundaryNodes(Collection<V> boundary) {
		if (boundary == null || boundary.size()!=2) return;
		
		this.boundary = new ArrayList<V>(boundary);
	}
	

	public V getEntry() {
		return entry;
	}

	public void setEntry(V entry) {
		this.entry = entry;
	}

	public V getExit() {
		return exit;
	}

	public void setExit(V exit) {
		this.exit = exit;
	}
	
	public void setEntryAndExit(V entry, V exit) {
		this.setEntry(entry);
		this.setExit(exit);
		this.boundary.clear();
		boundary.add(entry);
		boundary.add(exit);
	}

}
