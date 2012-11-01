package org.jbpt.petri.unfolding;

import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.petri.INode;

/**
 * Implementation of a node of a branching process.
 * 
 * @author Artem Polyvyanyy
 */
public abstract class AbstractBPNode<N extends INode> extends Vertex implements IBPNode<N> {
	protected int ID = 0;
	
	@Override
	public String getLabel() {
		return this.getName();
	}
}
