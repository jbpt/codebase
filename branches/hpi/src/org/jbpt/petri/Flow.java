package org.jbpt.petri;

import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.graph.abs.AbstractDirectedGraph;

/**
 * Implementation of a Petri net flow relation.
 * 
 * @author Artem Polyvyanyy
 */
public class Flow extends AbstractDirectedEdge<Node> implements IFlow<Node> {
	
	protected Flow(AbstractDirectedGraph<?,Node> g, Node source, Node target) {
		super(g, source, target);
	}
}
