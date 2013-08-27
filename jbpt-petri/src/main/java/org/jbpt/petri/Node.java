package org.jbpt.petri;

import org.jbpt.hypergraph.abs.Vertex;

/**
 * Implementation of a Petri net node.
 *  
 * A node of a Petri net is either a {@link Place} or {@link Transition}. 
 * 
 * @author Artem Polyvyanyy
 */
public class Node extends Vertex implements INode {
	
	private String label = "";
	
	/**
	 * Empty constructor.
	 */
	public Node() {
		super();
	}
	
	/**
	 * Constructor with name of this node as the only parameter.
	 *  
	 * @param name Name of this node. 
	 */
	public Node(String name) {
		super(name);
		
		this.setLabel(name);
	}
	
	/**
	 * Constructor with name and label as parameters.
	 * 
	 * @param name Name of this node.
	 * @param label Label of this node.
	 */
	public Node(String name, String label) {
		super(name);
		
		this.setLabel(label);
	}
	
	/**
	 * Constructor with name, label and description as parameters.
	 * 
	 * @param label String to use as a label of this node. 
	 * @param desc String to use as a description of this node. 
	 */
	public Node(String name, String label, String desc) {
		super(name);
		
		this.setLabel(label);
		this.setDescription(desc);
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}
	
	@Override
	public INode clone() {
		INode node = (INode)super.clone();
		node.setLabel(this.getLabel());
		
		return node;
	}
}
