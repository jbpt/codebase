package org.jbpt.petri;

import org.jbpt.graph.abs.AbstractDirectedGraph;

public class Flow extends AbstractFlow<Node> {

	public Flow(AbstractDirectedGraph<?, Node> g, Node source, Node target) {
		super(g, source, target);
	}

}
