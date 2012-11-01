package org.jbpt.petri.unfolding;

import org.jbpt.petri.INode;

public interface IOrderingRelationsDescriptor<BPN extends IBPNode<N>, N extends INode> {
	/**
	 * Get ordering relation between two nodes of this branching process.
	 * 
	 * @param n1 Node of this branching process.
	 * @param n2 Node of this branching process.
	 * @return Ordering relation between 'n1' and 'n2', see {@link OrderingRelationType}.
	 */
	public OrderingRelationType getOrderingRelation(BPN n1, BPN n2);

	/**
	 * Check if two nodes of this branching process are in the causal relation.
	 * 
	 * @param n1 Node of this branching process.
	 * @param n2 Node of this branching process.
	 * @return <tt>true</tt> if 'n1' and 'n2' are in the causal relation; otherwise <tt>false</tt>.
	 */
	public boolean areCausal(BPN n1, BPN n2);

	/**
	 * Check if two nodes of this branching process are in the inverse causal relation.
	 * 
	 * @param n1 Node of this branching process.
	 * @param n2 Node of this branching process.
	 * @return <tt>true</tt> if 'n1' and 'n2' are in the inverse causal relation; otherwise <tt>false</tt>.
	 */
	public boolean areInverseCausal(BPN n1, BPN n2);

	/**
	 * Check if two nodes of this branching process are concurrent.
	 * 
	 * @param n1 Node of this branching process.
	 * @param n2 Node of this branching process.
	 * @return <tt>true</tt> if 'n1' and 'n2' are concurrent; otherwise <tt>false</tt>.
	 */
	public boolean areConcurrent(BPN n1, BPN n2);

	/**
	 * Check if two nodes of this branching process are in conflict.
	 * 
	 * @param n1 Node of this branching process.
	 * @param n2 Node of this branching process.
	 * @return <tt>true</tt> if 'n1' and 'n2' are in conflict; otherwise <tt>false</tt>.
	 */
	public boolean areInConflict(BPN n1, BPN n2);
}
