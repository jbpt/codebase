package org.jbpt.graph.algo.bctree;

import org.jbpt.graph.Fragment;
import org.jbpt.graph.abs.IEdge;
import org.jbpt.graph.abs.IGraph;
import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.hypergraph.abs.Vertex;

/**
 * implementation of the node of the tree of the biconnected components.
 * 
 * @author Artem Polyvyanyy
 *
 * @param <E> Edge template.
 * @param <V> Vertex template.
 */
public class BCTreeNode<E extends IEdge<V>, V extends IVertex> extends Vertex {
	// Node type
	protected BCType nodeType = BCType.UNDEFINED;		
	// Biconnected component associated with this fragment 
	protected Fragment<E,V> fragment = null;
	// Cutvertex associated with this fragment
	protected V cutvertex = null;
	
	/**
	 * Constructor of 'B' type node.
	 * @param g Graph for which BCTree is computed.
	 */
	protected BCTreeNode(IGraph<E,V> g) {
		this.nodeType = BCType.B;
		this.fragment = new Fragment<E,V>(g);
	}
	
	/**
	 * Constructor of 'C' type node.
	 * @param g Graph for which BCTree is computed.
	 */
	protected BCTreeNode(V v) {
		this.nodeType = BCType.C;
		this.cutvertex = v;
	}
	
	/**
	 * Get type of this node.
	 * @return Type of this node.
	 */
	public BCType getNodeType() {
		return this.nodeType;
	}
	
	/**
	 * Get biconnected component associated with this node.
	 * @return Biconnected component associated with this node.
	 */
	public Fragment<E,V> getBiconnectedComponent() {
		return this.fragment;
	}
	
	/**
	 * Get articulation point associated with this node.
	 * @return Articulation point associated with this node.
	 */
	public V getArticulatioPoint() {
		return this.cutvertex;
	}
	
	@Override
	public String toString() {
		if (this.getNodeType()==BCType.C) return this.cutvertex.toString();
		return super.toString();
	}
	
	@Override
	public String getLabel() {
		if (this.getNodeType()==BCType.C) return this.cutvertex.getLabel();
		return super.toString();
	}
}
