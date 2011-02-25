package de.hpi.bpt.process.petri;

import de.hpi.bpt.graph.abs.AbstractDirectedEdge;
import de.hpi.bpt.graph.abs.AbstractDirectedGraph;

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
