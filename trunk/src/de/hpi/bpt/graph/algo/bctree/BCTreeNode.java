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
package de.hpi.bpt.graph.algo.bctree;

import java.util.Collection;
import java.util.Vector;

import de.hpi.bpt.graph.abs.IEdge;
import de.hpi.bpt.hypergraph.abs.IVertex;

public class BCTreeNode<E extends IEdge<V>, V extends IVertex> {
	private BCType nodeType;
	
	private BCTreeNode<E,V> parentNode;
	private Vector<BCTreeNode<E,V>> childNodes;
	
	private BCTComponent<E,V> graph;
	private V point;
	
	
	public BCTreeNode(BCTComponent<E,V> g) {
		this.parentNode = null;
		this.childNodes = new Vector<BCTreeNode<E,V>>();

		this.graph = g;
		this.point = null;
		
		this.nodeType = BCType.B;
	}
	
	public BCTreeNode(V p) {
		parentNode = null;
		childNodes = new Vector<BCTreeNode<E,V>>();

		this.graph = null;
		this.point = p;
		
		this.nodeType = BCType.C;
	}
	
	public BCType getNodeType() {
		return nodeType;
	}
	
	public BCTreeNode<E,V> getParentNode() {
		return this.parentNode;
	}
	
	public BCTComponent<E,V> getGraph() {
		return this.graph;
	}
	
	public V getPoint() {
		return this.point;
	}
	
	public Collection<BCTreeNode<E,V>> getChildren() {
		return this.childNodes;
	}
	
	public void addChild(BCTreeNode<E,V> node) {
		this.childNodes.add(node);
	}
	
	public void removeChild(BCTreeNode<E,V> node) {
		this.childNodes.remove(node);
	}
	
	public void setParent(BCTreeNode<E,V> node) {
		this.parentNode = node;
	}
}
