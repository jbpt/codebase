package org.jbpt.petri;

import org.jbpt.hypergraph.abs.Vertex;

/**
 * Implementation of a Petri net node. 
 * A node of a Petri net is either a {@link Place} or {@link Transition}. 
 * 
 * @author Artem Polyvyanyy
 */
public class Node extends Vertex implements INode {
	
	/**
	 * Empty constructor.
	 */
	public Node() {
		super();
	}
	
	/**
	 * Constructor with label of the node parameter.
	 *  
	 * @param label String to use as a label of this node. 
	 */
	public Node(String label) {
		super();
		
		this.setLabel(label);
	}
	
	/**
	 * Constructor with label and description of the node parameters.
	 * 
	 * @param label String to use as a label of this node. 
	 * @param desc String to use as a description of this node. 
	 */
	public Node(String label, String desc) {
		super();
		
		this.setLabel(label);
		this.setDescription(desc);
	}

	@Override
	public String getLabel() {
		return this.getName();
	}

	@Override
	public void setLabel(String label) {
		this.setName(label);
	}
	
	@Override
	public INode clone() {
		return (INode)super.clone();
	}
}
