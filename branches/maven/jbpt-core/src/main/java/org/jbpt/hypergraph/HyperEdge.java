package org.jbpt.hypergraph;

import org.jbpt.hypergraph.abs.AbstractHyperEdge;
import org.jbpt.hypergraph.abs.AbstractMultiHyperGraph;
import org.jbpt.hypergraph.abs.Vertex;

/**
 * Hyper edge implementation
 * Hyper edge is a collection of vertices of any size
 * Same vertices are allowed within edge to define self-loops
 * 
 * @author Artem Polyvyanyy
 */
public class HyperEdge extends AbstractHyperEdge<Vertex>
{
	protected HyperEdge(AbstractMultiHyperGraph<?, ?> g) {
		super(g);
	}
}
