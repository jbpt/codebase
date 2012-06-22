package org.jbpt.algo.tree.tctree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jbpt.graph.abs.AbstractMultiGraph;
import org.jbpt.graph.abs.IEdge;
import org.jbpt.graph.abs.IGraph;
import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.utils.DotSerializer;


/**
 * Implementation of the skeleton of the triconnected component.
 *  
 * @author Artem Polyvyanyy
 * 
 * @param <E> template for edge (extends IEdge)
 * @param <V> template for vertex (extends IVertex)
 */
public class TCSkeleton<E extends IEdge<V>, V extends IVertex> extends AbstractMultiGraph<E,V> {
	Set<E> virtualEdges = new HashSet<E>();

	public TCSkeleton() {
		super();
	}

	public TCSkeleton(IGraph<E,V> g) {
		super();
		for (E e : g.getEdges())
			this.addEdge(e.getV1(),e.getV2());
	}

	public E addVirtualEdge(V v1, V v2) {
		E e = super.addEdge(v1,v2);
		if (e != null)
			this.virtualEdges.add(e);

		return e;
	}

	public Collection<E> getVirtualEdges() {		
		return new ArrayList<E>(this.virtualEdges);
	}

	public boolean isVirtual(E e) {
		return this.virtualEdges.contains(e);
	}

	@Override
	public E removeEdge(E e) {
		this.virtualEdges.remove(e);
		return super.removeEdge(e);
	}

	@Override
	public String toDOT() {
		return new DotSerializer().serialize(this,this.virtualEdges);
	}
}
