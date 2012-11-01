package org.jbpt.graph;

import org.jbpt.graph.abs.AbstractEdge;
import org.jbpt.graph.abs.AbstractMultiGraph;
import org.jbpt.hypergraph.abs.Vertex;

/**
 * Graph edge implementation
 * 
 * @author Artem Polyvyanyy
 */
public class Edge extends AbstractEdge<Vertex>
{
	protected Edge(AbstractMultiGraph<?, Vertex> g, Vertex v1, Vertex v2) {
		super(g, v1, v2);
	}	
}
