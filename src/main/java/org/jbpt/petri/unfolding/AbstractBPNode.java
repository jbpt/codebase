package org.jbpt.petri.unfolding;

import org.jbpt.hypergraph.abs.GObject;
import org.jbpt.petri.INode;

/**
 * Implementation of a node of a branching process.
 * 
 * @author Artem Polyvyanyy
 */
public abstract class AbstractBPNode<N extends INode> extends GObject implements IBPNode<N> {
	protected int ID = 0;
}
