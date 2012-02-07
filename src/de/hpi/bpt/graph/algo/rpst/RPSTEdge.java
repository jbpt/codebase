package de.hpi.bpt.graph.algo.rpst;

import de.hpi.bpt.graph.abs.AbstractDirectedEdge;
import de.hpi.bpt.graph.abs.AbstractMultiDirectedGraph;
import de.hpi.bpt.graph.abs.IDirectedEdge;
import de.hpi.bpt.hypergraph.abs.IVertex;

public class RPSTEdge<E extends IDirectedEdge<V>, V extends IVertex> extends AbstractDirectedEdge<RPSTNode<E,V>> {

	protected RPSTEdge(AbstractMultiDirectedGraph<?, ?> g, RPSTNode<E, V> source, RPSTNode<E, V> target) {
		super(g, source, target);
	}
}
