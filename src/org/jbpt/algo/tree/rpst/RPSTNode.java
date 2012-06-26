package org.jbpt.algo.tree.rpst;

import org.jbpt.algo.tree.tctree.TCTreeNode;
import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.hypergraph.abs.IVertex;


public class RPSTNode<E extends IDirectedEdge<V>, V extends IVertex> extends TCTreeNode<E,V> {

	protected boolean isQuasi = false;

}
