package org.jbpt.algo.tree.tctree;

import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.graph.abs.AbstractMultiDirectedGraph;
import org.jbpt.graph.abs.IEdge;
import org.jbpt.hypergraph.abs.IVertex;

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
