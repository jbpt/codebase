package org.jbpt.petri2;

import org.jbpt.hypergraph.abs.Vertex;

/**
 * A Petri net abstract node (vertex), might be place or transition 
 * 
 * @author Artem Polyvyanyy
 */
public abstract class Node extends Vertex {
	/**
	 * Empty constructor
	 */
	public Node() {
		super();
	}
	
	/**
	 * Constructor with node name parameter
	 * @param name Node name
	 */
	public Node(String name) {
		super(name);
	}
	
	/**
	 * Constructor with node name and description parameters
	 * @param name Node name
	 * @param desc Node description
	 */
	public Node(String name, String desc) {
		super(name,desc);
	}

	/**
	 * Get label
	 * @return Label string (alias to name string)
	 */
	public String getLabel() {
		return this.getName();
	}

	/**
	 * Set label
	 * @param label Label to set
	 */
	public void setLabel(String label) {
		this.setName(label);
	}
	
	@Override
	public String toString() {
		return (this.getName()==null) ? "" : getName();
	}
}
