package org.jbpt.hypergraph;

import org.jbpt.hypergraph.abs.AbstractDirectedHyperEdge;
import org.jbpt.hypergraph.abs.AbstractMultiDirectedHyperGraph;
import org.jbpt.hypergraph.abs.Vertex;

/**
 * Directed hyper edge implementation
 * Directed hyper edge is two typed sets of vertices: source and target vertices
 * 
 * @author Artem Polyvyanyy
 */
public class DirectedHyperEdge extends AbstractDirectedHyperEdge<Vertex>
{
	protected DirectedHyperEdge(AbstractMultiDirectedHyperGraph<?, ?> g) {
		super(g);
	}
}
