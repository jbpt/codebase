package org.jbpt.petri.unfolding;

import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.petri.Node;

/**
 * A node in an unfolding, either an event or condition.
 * 
 * @author Artem Polyvyanyy
 */
public abstract class BPNode extends Vertex {
	
	protected int ID = 0;
	
	public abstract Node getNode();
}
