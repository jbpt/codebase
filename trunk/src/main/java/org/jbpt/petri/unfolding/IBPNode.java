package org.jbpt.petri.unfolding;

import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.petri.INode;

/**
 * Interface to a node of a branching process.
 * 
 * @author Artem Polyvyanyy
 */
public interface IBPNode<N extends INode> extends IVertex {
	
	/**
	 * Get a Petri net node associated with this node of a branching process. 
	 * 
	 * @return A Petri net node of the originative net system associated with this node in the given branching process.
	 */
	public N getPetriNetNode();
	
	/**
	 * Check if this node is event.
	 * 
	 * @return <tt>true</tt> if this node is event; otherwise <tt>false</tt>.
	 */
	public boolean isEvent();
	
	/**
	 * Check if this node is condition.
	 * 
	 * @return <tt>true</tt> if this node is condition; otherwise <tt>false</tt>.
	 */
	public boolean isCondition();
}