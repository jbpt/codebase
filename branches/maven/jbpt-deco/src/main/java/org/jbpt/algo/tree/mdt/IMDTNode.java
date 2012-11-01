package org.jbpt.algo.tree.mdt;

import java.util.Collection;

import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.hypergraph.abs.IVertex;

public interface IMDTNode<E extends IDirectedEdge<V>, V extends IVertex> extends IVertex {
	int getColor();
	Collection<V> getClan();
	V getProxy();
	MDTType getType();
}
