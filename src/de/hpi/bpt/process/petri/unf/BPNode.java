package de.hpi.bpt.process.petri.unf;

import de.hpi.bpt.hypergraph.abs.Vertex;
import de.hpi.bpt.process.petri.Node;

/**
 * Unfolding node - event or condition
 * 
 * @author Artem Polyvyanyy
 */
public abstract class BPNode extends Vertex {

	@Override
	public String getName() {
		return super.getName();
	}
	
	public abstract Node getNode();
}
