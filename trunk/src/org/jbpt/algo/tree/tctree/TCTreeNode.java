package org.jbpt.algo.tree.tctree;

import java.util.ArrayList;
import java.util.Collection;

import org.jbpt.graph.abs.IEdge;
import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.hypergraph.abs.Vertex;

/**
 * Implementation of the node of the tree of the triconnected components.
 * 
 * @author Artem Polyvyanyy
 *
 * @param <E> Edge template.
 * @param <V> Vertex template.
 */
public class TCTreeNode<E extends IEdge<V>, V extends IVertex> extends Vertex {
	// Node type
	protected TCType type = TCType.UNDEFINED;
	// Fragment skeleton
	protected TCTreeSkeleton<E,V> skeleton;
	// boundary vertices of the fragment
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
	
	public TCTreeSkeleton<E,V> getSkeleton() {
		return this.skeleton;
	}
	
	public Collection<V> getBoundaryNodes() {
		// TODO: do we need to introduce new array?
		return new ArrayList<V>(this.boundary);
	}
	
	@Override
	public String toString() {
		return this.getName() + " " + this.getBoundaryNodes() + " - " + this.getSkeleton() + " - " + this.getSkeleton().getVirtualEdges();
	}
	
	protected void setType(TCType type) {
		this.type = type;
	}
	
	protected void setSkeleton(TCTreeSkeleton<E,V> skeleton) {
		this.skeleton = skeleton;
	}
	
	protected void setBoundaryNodes(Collection<V> boundary) {
		if (boundary == null || boundary.size()!=2) return;
		
		this.boundary = new ArrayList<V>(boundary);
	}

}
