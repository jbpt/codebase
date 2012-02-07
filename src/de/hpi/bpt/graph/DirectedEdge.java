package de.hpi.bpt.graph;

import de.hpi.bpt.graph.abs.AbstractDirectedEdge;
import de.hpi.bpt.graph.abs.AbstractMultiDirectedGraph;
import de.hpi.bpt.hypergraph.abs.Vertex;

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
