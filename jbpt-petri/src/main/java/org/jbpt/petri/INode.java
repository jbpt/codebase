package org.jbpt.petri;

import org.jbpt.hypergraph.abs.IVertex;

/**
 * Interface to a Petri net node.
 *
 * @author Artem Polyvyanyy
 */
public interface INode extends IVertex {
	
	/**
	 * Get label of this node.
	 * 
	 * @return Label of this node.
	 */
	public String getLabel();
	
	/**
	 * Set label of this node.
	 * 
	 * @param label String to use as label of this node. 
	 */
	public void setLabel(String label);
}