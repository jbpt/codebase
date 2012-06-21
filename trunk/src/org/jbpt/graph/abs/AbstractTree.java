package org.jbpt.graph.abs;

import java.util.Collection;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jbpt.hypergraph.abs.IVertex;


/**
 * Abstract tree implementation.
 * 
 * Implemented as a directed graph.
 * For every edge of the graph source vertex represents parent vertex and target vertex represents child vertex.
 * 
 * @author Artem Polyvyanyy
 *
 * @param <V> Vertex template.
 */
public class AbstractTree<V extends IVertex> extends AbstractDirectedGraph<IDirectedEdge<V>,V> implements ITree<V> {
	
	protected V root = null;
	
	/**
	 * Empty constructor - for technical purposes.
	 */
	protected AbstractTree() {}
	
	/**
	 * Constructor of the abstract tree.
	 * @param root Vertex to use as root of this tree.
	 */
	public AbstractTree(V root) {
		this.root = root;
	}

	@Override
	public V getRoot() {
		return this.root;
	}

	@Override
	public V reRoot(V v) {
		if (v == null || !this.getVertices().contains(v)) return this.root;
		if (v.equals(this.root)) return this.root;		
		this.root = v;
		
		Queue<V> queue = new ConcurrentLinkedQueue<V>();
		queue.add(this.root);
		Set<V> visited = new HashSet<V>();
		visited.add(this.root);
		
		while (!queue.isEmpty()) {
			V c = queue.poll();
			Collection<V> adjVs = this.getAdjacent(c);
			adjVs.removeAll(visited);
			
			for (V a : adjVs) {
				super.removeEdges(super.getEdges(c,a));
				super.removeEdges(super.getEdges(a,c));
				
				super.addEdge(c,a);
				visited.add(a);
				queue.add(a);
			}
		}
		
		return this.root;
	}

	@Override
	public Collection<V> getChildren(V v) {
		return super.getDirectSuccessors(v);
	}

	@Override
	public V getParent(V v) {
		return super.getFirstDirectPredecessor(v);
	}

	@Override
	public V addChild(V p, V c) {
		if (!super.getVertices().contains(p)) return null;
		IDirectedEdge<V> e = super.addEdge(p,c);
		
		return (e==null) ? null : c;
	}

	@Override
	public boolean isRoot(V v) {
		if (this.root == null) return false;
		return this.root.equals(v);
	}
}
