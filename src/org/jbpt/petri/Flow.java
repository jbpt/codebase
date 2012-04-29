package org.jbpt.petri;

import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.graph.abs.AbstractDirectedGraph;

/**
 * Petri net flow relation element
 * 
 * @author Artem Polyvyanyy
 */
public class Flow extends AbstractDirectedEdge<Node> {
	protected Flow(AbstractDirectedGraph<Flow,Node> g, Node source, Node target) {
		super(g, source, target);
	}
}
