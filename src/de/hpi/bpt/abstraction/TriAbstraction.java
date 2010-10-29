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
package de.hpi.bpt.abstraction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import de.hpi.bpt.graph.abs.AbstractMultiGraphFragment;
import de.hpi.bpt.graph.abs.IDirectedEdge;
import de.hpi.bpt.graph.abs.IDirectedGraph;
import de.hpi.bpt.graph.algo.tctree.TCTree;
import de.hpi.bpt.graph.algo.tctree.TCTreeNode;
import de.hpi.bpt.graph.algo.tctree.TCType;
import de.hpi.bpt.hypergraph.abs.IGObject;
import de.hpi.bpt.hypergraph.abs.IVertex;

/**
 * 
 * @author Artem Polyvyanyy
 *
 */
public class TriAbstraction<E extends IDirectedEdge<V>, V extends IVertex> {
	private TCTree<E,V> tc;
	
	/**
	 * Constructor
	 * @param g Graph to abstract
	 */
	public TriAbstraction(IDirectedGraph<E,V> g) {
		this.tc = new TCTree<E,V>(g);
	}
	
	/**
	 * Get abstraction info
	 * @param id Task id to abstract
	 * @return Abstraction info, <code>null</code> if nothing to abstract
	 */
	public TriAbstractionStepInfo getAbstractionInfo(String id) {
		TriAbstractionStepInfo result = null;
		
		V absVertex = this.getVertex(id);
		TCTreeNode<E,V> absTreeNode = tc.getVertex(absVertex);
		
		if (this.isSimple(absTreeNode)) { // P or R
			if (tc.isRoot(absTreeNode)) return null;
			
			// get parent of node
			TCTreeNode<E,V> absTreeNodeParent = tc.getParent(absTreeNode);
			
			if (absTreeNodeParent.getType() == TCType.R) { result = this.getRAbstractionInfo(absTreeNodeParent); }
			if (absTreeNodeParent.getType() == TCType.B) { result = this.getPAbstractionInfo(absTreeNodeParent, absTreeNode); }
		}
		else { // is not simple - Q or S
			result = getQSAbstractionInfo(absTreeNode, absVertex);
		}
		
		return result;
	}
	
	private Collection<IGObject> getAllObjects(AbstractMultiGraphFragment<E,V> frag)
	{
		Collection<IGObject> result = new ArrayList<IGObject>();
		
		result.addAll(frag.getVertices());
		result.addAll(frag.getOriginalEdges());
		
		return result;
	}
	
	private TriAbstractionStepInfo getRAbstractionInfo(TCTreeNode<E,V> node)
	{
		TriAbstractionStepInfo result = new TriAbstractionStepInfo();
		result.type = TCType.R;
		result.fragment = this.getAllObjects(tc.getFragment(node));
		result.entry = node.getEntry();
		result.exit = node.getExit(); 
		
		return result;
	}
	
	private TriAbstractionStepInfo getPAbstractionInfo(TCTreeNode<E,V> parent, TCTreeNode<E,V> node)
	{
		TriAbstractionStepInfo result = new TriAbstractionStepInfo();
		result.type = TCType.B;
		result.fragment = this.getAllObjects(tc.getFragment(parent, node));
		result.entry = parent.getEntry();
		result.exit = parent.getExit(); 
		
		return result;
	}
	
	private TriAbstractionStepInfo getQSAbstractionInfo(TCTreeNode<E,V> node, V v)
	{
		TriAbstractionStepInfo result = tc.getFragment(node, v); 
		
		return result;
	}
	
	private V getVertex(String id)
	{
		V v = null;
		Collection<V> vs = tc.getGraph().getVertices();
		Iterator<V> i = vs.iterator();
		while (i.hasNext()) {
			v = i.next();
			if (v.getId().equals(id)) break;
		}
		
		return v;
	}
	
	private boolean isSimple(TCTreeNode<E,V> node)
	{
		return (node.getType() == TCType.P && node.getSkeleton().getVertices().size() == 3);
	}
}
