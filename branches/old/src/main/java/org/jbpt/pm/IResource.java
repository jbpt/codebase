/**
 * 
 */
package org.jbpt.pm;

/**
 * Interface for all model elements that should not be considered in any graph algorithms.
 * 
 * @author Tobias Hoppe
 *
 */
public interface IResource extends INonFlowNode {
	
	/**
	 * @return the parent {@link Resource} of this one.
	 */
	IResource getParent();

	/**
	 * Set the parent of this {@Resource}.
	 * @param parent of this {@Resource}
	 */
	void setParent(IResource parent);
}
