package org.jbpt.algo.tree.tctree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jbpt.graph.abs.AbstractMultiGraph;
import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.graph.abs.IEdge;
import org.jbpt.graph.abs.IGraph;
import org.jbpt.hypergraph.abs.IVertex;


/**
 * Implementation of the skeleton of the triconnected component.
 *  
 * @author Artem Polyvyanyy
 * 
 * @param <E> Edge template.
 * @param <V> Vertex template.
 */
public class TCSkeleton<E extends IEdge<V>, V extends IVertex> extends AbstractMultiGraph<E,V> {
	// set of virtual edges
	protected Set<E> virtualEdges = new HashSet<E>();
	
	protected Map<E,E> e2o = new HashMap<E,E>();
	protected Map<E,E> o2e = new HashMap<E,E>();
	
	/**
	 * Empty constructor.
	 */
	protected TCSkeleton() {
		super();
	}

	/**
	 * Constructor.
	 * @param g Graph to copy skeleton from.
	 */
	protected TCSkeleton(IGraph<E,V> g, Map<E,E> e2o) {
		super();
		for (E o : g.getEdges()) {
			E e = this.addEdge(o.getV1(),o.getV2());
			e2o.put(e,o);
		}
	}
	
	protected E addVirtualEdge(V v1, V v2, Object id) {
		E e = super.addEdge(v1,v2);
		if (e != null) {
			e.setTag(id);
			this.virtualEdges.add(e);
		}

		return e;
	}
	
	protected E addVirtualEdge(V v1, V v2) {
		E e = super.addEdge(v1,v2);
		if (e != null) {
			this.virtualEdges.add(e);
		}

		return e;
	}

	/**
	 * Get virtual edges of this skeleton.
	 * @return Set of all virtual edges of this skeleton.
	 */
	public Set<E> getVirtualEdges() {		
		return this.virtualEdges;
	}

	/**
	 * Checks if a given edge is a virtual edge of this skeleton.
	 * @param e Edge.
	 * @return <tt>true</tt> if the edge is virtual; otherwise <tt>false</tt>. 
	 */
	public boolean isVirtual(E e) {
		return this.virtualEdges.contains(e);
	}

	protected E addEdge(V v1, V v2, E o) {
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
		StringBuffer buff = new StringBuffer(this.getEdges().size() + 2);
		
		buff.append(String.format("digraph \"%s\" {\n",this.getName()));
		buff.append("rankdir=TD\n");
		
		
		for (V v : this.getVertices()) {
			buff.append(String.format("    \"%s\" [label=\"%s\"];\n", v.getId().replace("-", ""), v.getLabel()));
		}
		
		for (E e : this.getEdges()) {
			if (this.isVirtual(e)) {
				buff.append(String.format("    \"%s\" %s \"%s\" [label=\"%s\" style=dotted dir=none]\n", e.getV1().getId().replace("-", ""), "->", e.getV2().getId().replace("-", ""), e.getLabel()));
			}				
			else {
				E edge = this.getOriginalEdge(e);
				if (edge instanceof IDirectedEdge<?>) {
					IDirectedEdge<?> de = (IDirectedEdge<?>) edge;
					buff.append(String.format("    \"%s\" %s \"%s\" [label=\"%s\"]\n", de.getSource().getId().replace("-", ""), "->", de.getTarget().getId().replace("-", ""), e.getLabel()));
				}
				else
					buff.append(String.format("    \"%s\" %s \"%s\" [label=\"%s\" dir=none]\n", e.getV1().getId().replace("-", ""), "->", e.getV2().getId().replace("-", ""), e.getLabel()));
			}
		}
		buff.append("}\n");
		
		return buff.toString();
	}
	
	/**
	 * Get original edge associated with a given edge from this skeleton. 
	 * @param e Edge.
	 * @return Original edge associated with this edge, if there is no original edge associated with this edge.
	 */
	public E getOriginalEdge(E e) {
		return this.e2o.get(e);
	}
	
	/**
	 * Get original edges associated with this skeleton. 
	 * @return
	 */
	public Set<E> getOriginalEdges() {
		return this.o2e.keySet();
	}
}
