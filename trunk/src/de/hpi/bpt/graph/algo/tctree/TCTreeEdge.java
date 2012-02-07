package de.hpi.bpt.graph.algo.tctree;

import de.hpi.bpt.graph.abs.AbstractDirectedEdge;
import de.hpi.bpt.graph.abs.AbstractMultiDirectedGraph;
import de.hpi.bpt.graph.abs.IEdge;
import de.hpi.bpt.hypergraph.abs.IVertex;

/**
 * 
 * @author Artem Polyvyanyy
 *
 */
public class TCTreeEdge<E extends IEdge<V>, V extends IVertex> extends AbstractDirectedEdge<TCTreeNode<E,V>> {

	protected TCTreeEdge(AbstractMultiDirectedGraph<?, ?> g, TCTreeNode<E, V> source, TCTreeNode<E, V> target) {
		super(g, source, target);
	}
}
