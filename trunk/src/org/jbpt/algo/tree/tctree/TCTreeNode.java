package org.jbpt.algo.tree.tctree;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
	// node type
	protected TCType type = TCType.UNDEFINED;
	// skeleton
	protected TCSkeleton<E,V> skeleton = new TCSkeleton<E,V>();
	// boundary vertices of the fragment
	// TODO: populate split pair
	protected Set<V> splitPair = new HashSet<V>();

	public TCType getType() {
		return type;
	}
	
	public TCSkeleton<E,V> getSkeleton() {
		return this.skeleton;
	}
	
	public Collection<V> getSplitPair() {
		return this.splitPair;
	}
	
	@Override
	public String toString() {
		return this.getName() + " [" + this.splitPair + "] skeleton: " + this.skeleton + " virtual: " + this.skeleton.virtualEdges;
	}
}
