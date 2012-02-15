package org.jbpt.graph.algo.rpst;

import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.graph.abs.AbstractMultiDirectedGraph;
import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.hypergraph.abs.IVertex;

public class RPSTEdge<E extends IDirectedEdge<V>, V extends IVertex> extends AbstractDirectedEdge<RPSTNode<E,V>> {

	protected RPSTEdge(AbstractMultiDirectedGraph<?, ?> g, RPSTNode<E, V> source, RPSTNode<E, V> target) {
		super(g, source, target);
	}
}
