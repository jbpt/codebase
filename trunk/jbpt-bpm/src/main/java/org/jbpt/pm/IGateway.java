/**
 * 
 */
package org.jbpt.pm;

/**
 * Basic interface for all gateway implementations.
 * 
 * @author Cindy Fähnrich, TObias Hoppe
 *
 */
public interface IGateway extends IFlowNode {
	
	/**
	 * Check if {@link IGateway} is split, has one incoming and multiple outgoing control flow edges
	 * @return <code>true</code> if {@link IGateway} is a split gateway, <code>false</code> otherwise
	 */
	boolean isSplit();
	
	/**
	 * Check if {@link IGateway} is join, has one outgoing and multiple incoming control flow edges
	 * @return <code>true</code> if {@link IGateway} is a join gateway, <code>false</code> otherwise
	 */
	boolean isJoin();
}
