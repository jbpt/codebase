package org.jbpt.algo.tree.tctree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
	protected Set<E> virtualEdges = new HashSet<E>();
	
	protected Map<E,E> e2o = new HashMap<E,E>();
	protected Map<E,E> o2e = new HashMap<E,E>();

	/**
	 * Constructor.
	 */
	public TCSkeleton() {
		super();
	}

	/**
	 * Constructor.
	 * @param g Graph to copy skeleton from.
	 */
	public TCSkeleton(IGraph<E,V> g, Map<E,E> e2o) {
		super();
		for (E o : g.getEdges()) {
			E e = this.addEdge(o.getV1(),o.getV2());
			e2o.put(e,o);
		}
	}
	
	/**
	 * Add a virtual edge to this skeleton.
	 * @param v1 Vertex.
	 * @param v2 Vertex.
	 * @return Edge added to this skeleton or <tt>null</tt> if no edge was added.
	 */
	public E addVirtualEdge(V v1, V v2) {
		E e = super.addEdge(v1,v2);
		if (e != null) {
			this.virtualEdges.add(e);
		}

		return e;
	}

	/**
	 * Get virtual edges in this skeleton.
	 * @return Set of all virtual edges of this skeleton.
	 */
	public Set<E> getVirtualEdges() {		
		return this.virtualEdges;
	}

	/**
	 * Check if a given edge is a virtual edge in this skeleton.
	 * @param e Edge.
	 * @return <tt>true</tt> if the edge is virtual; otherwise <tt>false</tt>. 
	 */
	public boolean isVirtual(E e) {
		return this.virtualEdges.contains(e);
	}

	/**
	 * Add edge to this skeleton.
	 * @param v1
	 * @param v2
	 * @param o
	 * @return
	 */
	public E addEdge(V v1, V v2, E o) {
		E e = super.addEdge(v1,v2);
		if (e!=null) {
			this.e2o.put(e,o);
			this.o2e.put(o,e);
		}
		return e;
	}

	@Override
	public E removeEdge(E e) {
		this.virtualEdges.remove(e);
		this.o2e.remove(this.e2o.get(e));
		this.e2o.remove(e);
		return super.removeEdge(e);
	}

	@Override
	public String toDOT() {
		return new DotSerializer().serialize(this,this.virtualEdges);
	}
	
	/**
	 * Get original edge associated with a given edge from this skeleton. 
	 * @param e Edge.
	 * @return Original edge associated with this edge, if there is no original edge associated with this edge.
	 */
	public E getOriginalEdge(E e) {
		return this.e2o.get(e);
	}
	
	public Set<E> getOriginalEdges() {
		return this.o2e.keySet();
	}
}
