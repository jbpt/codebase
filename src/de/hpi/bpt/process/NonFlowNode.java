package de.hpi.bpt.process;

import de.hpi.bpt.hypergraph.abs.Vertex;

/**
 * Base class for nodes that does not take part of the control flow.
 * @author Tobias Hoppe
 *
 */
public class NonFlowNode extends Vertex implements INonFlowNode{

	/**
	 * Create a new node that does not take part of the control flow.
	 */
	public NonFlowNode() {
		super();
	}

	/**
	 * Create a new node with the given name that does not take part of the control flow.
	 * @param name of the node
	 */
	public NonFlowNode(String name) {
		super(name);
	}

	/**
	 * Create a new node with the given name and description, that does not take part of the control flow.
	 * @param name of the node
	 * @param description of the node
	 */
	public NonFlowNode(String name, String description) {
		super(name, description);
	}

}
