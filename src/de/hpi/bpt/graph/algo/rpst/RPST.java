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

import java.util.ArrayList;
import java.util.Collection;

import de.hpi.bpt.graph.abs.AbstractDirectedGraph;
import de.hpi.bpt.graph.abs.IDirectedEdge;
import de.hpi.bpt.graph.abs.IDirectedGraph;
import de.hpi.bpt.graph.algo.DirectedGraphAlgorithms;
import de.hpi.bpt.graph.algo.tctree.TCTree;
import de.hpi.bpt.graph.algo.tctree.TCTreeNode;
import de.hpi.bpt.graph.algo.tctree.TCType;
import de.hpi.bpt.hypergraph.abs.IVertex;
import de.hpi.bpt.hypergraph.abs.Vertex;

/**
 * The Refined Process Structure Tree
 * 
 * @author Artem Polyvyanyy
 * 
 * Artem Polyvyanyy, Jussi Vanhatalo, and Hagen Voelzer. 
 * Simplified Computation and Generalization of the Refined Process Structure Tree. 
 * Proceedings of the 7th International Workshop on Web Services and Formal Methods (WS-FM).
 * Hoboken, NJ, US, September 2010;
 */
public class RPST <E extends IDirectedEdge<V>, V extends IVertex>
				extends AbstractDirectedGraph<IDirectedEdge<RPSTNode<E,V>>, RPSTNode<E,V>> {

	private IDirectedGraph<E,V> graph = null;
	
	private IDirectedEdge<V> backEdge = null;
	
	private Collection<E> extraEdges = null;
	
	private TCTree<E,V> tct = null;
	
	private DirectedGraphAlgorithms<E,V> dga = new DirectedGraphAlgorithms<E, V>();
	
	private RPSTNode<E,V> root = null;
	
	@Override
	public IDirectedEdge<RPSTNode<E, V>> addEdge(RPSTNode<E, V> s, RPSTNode<E, V> t) {
		return super.addEdge(s, t);
	}
	
	@SuppressWarnings("unchecked")
	public RPST(IDirectedGraph<E,V> g) {
		if (g==null) return;
		this.graph = g;
		
		Collection<V> sources = dga.getInputVertices(this.graph);
		Collection<V> sinks = dga.getOutputVertices(this.graph);
		if (sources.size()!=1 || sinks.size()!=1) return;
		
		V src = sources.iterator().next();
		V snk = sinks.iterator().next();
		
		this.backEdge = this.graph.addEdge(snk, src);
		
		// expand mixed vertices
		this.extraEdges = new ArrayList<E>();
		for (V v : this.graph.getVertices()) {
			if (this.graph.getIncomingEdges(v).size()>1 &&
					this.graph.getOutgoingEdges(v).size()>1) {
				V newV = (V) (new Vertex());
				newV.setName(v.getName()+"*");
				this.graph.addVertex(newV);
				
				for (E e : this.graph.getOutgoingEdges(v)) {
					this.graph.addEdge(newV,e.getTarget());
					this.graph.removeEdge(e);
				}
				
				E newE = this.graph.addEdge(v, newV);
				this.extraEdges.add(newE);
			}
		}
		
		// compute TCTree
		this.tct = new TCTree(this.graph,this.backEdge);
		
		System.out.println(this.tct);
		
		// remove extra edges
		for (TCTreeNode trivial : this.tct.getVertices(TCType.T)) {
			
			if (this.isExtraEdge(trivial.getBoundaryNodes())) {
				this.tct.removeEdges(this.tct.getIncomingEdges(trivial));
				this.tct.removeVertex(trivial);
			}
		}
		
		System.out.println(this.tct);
		
		// CONSTRUCT RPST
		this.root = new RPSTNode<E, V>(this.tct.getRoot());
		
		System.out.println(this);
		
	}
	
	private boolean isExtraEdge(Collection<V> vs) {
		for (E e : this.extraEdges) {
			if (vs.contains(e.getSource()) && vs.contains(e.getTarget()))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Get original graph
	 * @return original graph
	 */
	public IDirectedGraph<E,V> getGraph() {
		return this.graph;
	}
	
	/**
	 * Get root node
	 * @return root node
	 */
	public RPSTNode<E,V> getRoot() {
		return this.root;
	}
	
	/**
	 * Get children of the RPST node
	 * @param node RPST node
	 * @return children of the node
	 */
	public Collection<RPSTNode<E,V>> getChildren(RPSTNode<E,V> node) {
		return this.getSuccessors(node);
	}
	
	@Override
	public String toString() {
		return toStringHelper(this.getRoot(), 0);
	}
	
	private String toStringHelper(RPSTNode<E,V> tn, int depth) {
		String result = "";
		for (int i = 0; i < depth; i++){
			result += "   ";
		}
		result += tn.toString();
		result += "\n";
		for (RPSTNode<E,V> c: this.getChildren(tn)){
			result += toStringHelper(c, depth+1);
		}
		return result;
	}
}
