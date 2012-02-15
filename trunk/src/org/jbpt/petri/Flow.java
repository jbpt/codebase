package org.jbpt.petri;

import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.graph.abs.AbstractDirectedGraph;

/**
 * Petri net flow relation
 * @author artem.polyvyanyy
 *
 */
public class Flow extends AbstractDirectedEdge<Node> {
	@SuppressWarnings("rawtypes")
	protected Flow(AbstractDirectedGraph g, Node source, Node target) {
		super(g, source, target);
	}
}
