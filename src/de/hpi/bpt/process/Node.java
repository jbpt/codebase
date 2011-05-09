package de.hpi.bpt.process;

import de.hpi.bpt.oryx.erdf.ERDFNode;

/**
 * 
 * @author Artem Polyvyanyy
 *
 */
public abstract class Node extends ERDFNode {

	public Node() {
		super();
	}

	public Node(String name, String desc) {
		super(name, desc);
	}

	public Node(String name) {
		super(name);
	}
	
	public String toString() {
		return this.getId();
	}
}
