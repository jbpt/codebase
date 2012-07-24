package org.jbpt.petri;

import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.graph.abs.AbstractDirectedGraph;

/**
 * Implementation of a Petri net flow relation.
 * 
 * @author Artem Polyvyanyy
 */
public class Flow extends AbstractDirectedEdge<INode> implements IFlow<INode> {
	
	protected Flow(AbstractDirectedGraph<?,INode> g, INode source, INode target) {
		super(g, source, target);
	}
}
