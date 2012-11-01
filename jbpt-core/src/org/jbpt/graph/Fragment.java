package org.jbpt.graph;

import java.util.Collection;
import java.util.HashSet;

import org.jbpt.graph.abs.IEdge;
import org.jbpt.graph.abs.IFragment;
import org.jbpt.graph.abs.IGraph;
import org.jbpt.hypergraph.abs.IVertex;

/**
 * Fragment of a graph implementation.
 * A fragment of a graph is defined by a set of graph's edges.
 * 
 * @author Artem Polyvyanyy
 *
 * @param <E> Edge template.
 * @param <V> Vertex template.
 */
public class Fragment<E extends IEdge<V>, V extends IVertex> extends HashSet<E> implements IFragment<E,V> {
	
	private static final long serialVersionUID = -2316099603574934264L;
	
	private IGraph<E,V> g = null;
	
	public Fragment(IGraph<E,V> g) {
		super();
		
		this.g = g;
	}

	@Override
	public IGraph<E,V> getGraph() {
		return this.g;
	}
	
	@Override
	public boolean add(E e) {
		if (!this.g.getEdges().contains(e)) return false;
		return super.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean result = false;
		
		for (E e : c)
			result |= this.add(e);
		
		return result;
	}
}
