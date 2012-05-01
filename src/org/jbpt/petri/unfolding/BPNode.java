package org.jbpt.petri.unfolding;

import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.petri.Node;

/**
 * Unfolding node - event or condition
 * 
 * @author Artem Polyvyanyy
 */
public abstract class BPNode extends Vertex {
	
	public abstract Node getNode();
	
}
