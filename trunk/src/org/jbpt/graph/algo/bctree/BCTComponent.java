package org.jbpt.graph.algo.bctree;

import org.jbpt.graph.abs.AbstractMultiGraphFragment;
import org.jbpt.graph.abs.IEdge;
import org.jbpt.graph.abs.IGraph;
import org.jbpt.hypergraph.abs.IVertex;

public class BCTComponent<E extends IEdge<V>, V extends IVertex> extends AbstractMultiGraphFragment<E, V> {

	public BCTComponent(IGraph<E, V> g) {
		super(g);
	}

}
