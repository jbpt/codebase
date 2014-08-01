package org.jbpt.graph;

import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.graph.abs.AbstractMultiDirectedGraph;
import org.jbpt.hypergraph.abs.Vertex;

/**
 * Directed edge implementation
 * 
 * @author Artem Polyvyanyy
 */
public class DirectedEdge extends AbstractDirectedEdge<Vertex>
{
	protected DirectedEdge(AbstractMultiDirectedGraph<?, Vertex> g, Vertex source, Vertex target) {
		super(g, source, target);
	}
}
