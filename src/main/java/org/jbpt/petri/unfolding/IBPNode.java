package org.jbpt.petri.unfolding;

import org.jbpt.hypergraph.abs.IGObject;
import org.jbpt.petri.INode;

/**
 * Interface to a node of a branching process.
 * 
 * @author Artem Polyvyanyy
 */
public interface IBPNode<N extends INode> extends IGObject {
	
	/**
	 * Get a Petri node associated with this node of a branching process. 
	 * 
	 * @return A Petri net node of the originative net system associated with this node in the branching process.
	 */
	public N getPetriNetNode();
	
	public boolean isEvent();
	
	public boolean isCondition();
}