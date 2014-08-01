package org.jbpt.graph;

import java.util.Collection;
import java.util.Iterator;

import org.jbpt.graph.abs.AbstractMultiDirectedGraphFragment;
import org.jbpt.hypergraph.abs.Vertex;


/**
 * 
 * @author Artem Polyvyanyy
 *
 */
public class DirectedGraphFragment extends AbstractMultiDirectedGraphFragment<DirectedEdge,Vertex> {

	/**
	 * Constructor
	 * @param parent Parent graph of the fragment
	 */
	public DirectedGraphFragment(DirectedGraph parent) {
		super(parent);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.hpi.bpt.graph.abs.AbstractMultiDirectedGraphFragment#addEdge(de.hpi.bpt.hypergraph.abs.IVertex, de.hpi.bpt.hypergraph.abs.IVertex)
	 */
	@Override
	public DirectedEdge addEdge(Vertex s, Vertex t) {
		if (this.graph!=null && this.graph.areAdjacent(s, t)) {
			if (s == null || t == null) return null;
			Collection<DirectedEdge> es = this.getEdgesWithSourceAndTarget(s, t);
			if (es.size()>0) {
				Iterator<DirectedEdge> i = es.iterator();
				while (i.hasNext()) {
					DirectedEdge e = i.next();
					if (e.getVertices().size()==2)
						return null;
				}
			}
			
			DirectedEdge e = new DirectedEdge(this, s, t);
			return e;
		}	
		
		return null;
	}

}
