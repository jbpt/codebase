package org.jbpt.petri;

import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.graph.abs.AbstractDirectedGraph;

/**
 * Implementation of a Petri net flow relation.
 * 
 * @author Artem Polyvyanyy
 */
public abstract class AbstractFlow<N extends INode> extends AbstractDirectedEdge<N> implements IFlow<N> {
	
	/**
	 * Constructor of a flow relation.
	 * 
	 * @param g A directed graph
	 * @param source Source node.
	 * @param target Target node.
	 */
	protected AbstractFlow(AbstractDirectedGraph<?,N> g, N source, N target) {
		super(g, source, target);
	}
}
