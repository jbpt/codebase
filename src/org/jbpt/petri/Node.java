package org.jbpt.petri;

import org.jbpt.hypergraph.abs.Vertex;

/**
 * A Petri net abstract node (vertex), might be place or transition 
 * @author artem.polyvyanyy
 *
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
	
	@Override
	public String toString() {
		return (getName()==null) ? "" : getName();
	}
}
