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
package de.hpi.bpt.graph.algo.tctree;

import java.util.ArrayList;
import java.util.Collection;

import de.hpi.bpt.graph.abs.IEdge;
import de.hpi.bpt.hypergraph.abs.IVertex;
import de.hpi.bpt.hypergraph.abs.Vertex;

public class TCTreeNode<E extends IEdge<V>, V extends IVertex> extends Vertex {

	protected TCType type = TCType.UNDEFINED;
	
	protected TCTreeSkeleton<E,V> skeleton;
	
	protected Collection<V> boundary = new ArrayList<V>();
	
	/**
	 * Constructor
	 */
	public TCTreeNode() {
		super();
	}
	
	/**
	 * Constructor
	 * @param name Node name
	 */
	public TCTreeNode(String name) {
		super(name);
	}

	public TCType getType() {
		return type;
	}

	protected void setType(TCType type) {
		this.type = type;
	}
	
	public TCTreeSkeleton<E,V> getSkeleton() {
		return this.skeleton;
	}

	protected void setSkeleton(TCTreeSkeleton<E,V> skeleton) {
		this.skeleton = skeleton;
	}
	
	public Collection<V> getBoundaryNodes() {
		return new ArrayList<V>(this.boundary);
	}

	protected void setBoundaryNodes(Collection<V> boundary) {
		if (boundary == null || boundary.size()!=2) return;
		
		this.boundary = new ArrayList<V>(boundary);
	}
	
	@Override
	public String toString() {
		return this.getName() + " " + this.getBoundaryNodes() + " - " + this.getSkeleton() + " - " + this.getSkeleton().getVirtualEdges();
	}

}
