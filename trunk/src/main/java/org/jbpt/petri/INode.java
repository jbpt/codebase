package org.jbpt.petri;

import org.jbpt.hypergraph.abs.IVertex;

/**
 * Petri net node interface.
 *
 * @author Artem Polyvyanyy
 */
public interface INode extends IVertex {
	/**
	 * Get a label of this node.
	 * 
	 * @return Label of this node.
	 */
	public String getLabel();
	
	/**
	 * Set label of this node.
	 * 
	 * @param label String to use as a label of this node. 
	 */
	public void setLabel(String label);
}