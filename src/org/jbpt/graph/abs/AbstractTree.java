package org.jbpt.graph.abs;

import java.util.Collection;

import org.jbpt.hypergraph.abs.IVertex;


/**
 * Abstract tree implementation
 * 
 * @author Artem Polyvyanyy
 *
 * @param <E> template for edge (extends IEdge)
 * @param <V> template for vertex (extends IVertex)
 */
public class AbstractTree<E extends IEdge<V>,V extends IVertex> extends AbstractGraph<E,V> implements ITree<E,V> {
	
	private V root = null;

	@Override
	public V getRoot() {
		return this.root;
	}

	@Override
	public V reRoot(V v) {
		if (v == null || !this.getVertices().contains(v)) return this.root;

		this.root = v;
		
		return this.root;
	}

	@Override
	public Collection<V> getChildren(V v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V getParent(V v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public E addChild(V p, V c) {
		
		return null;
	}
}
