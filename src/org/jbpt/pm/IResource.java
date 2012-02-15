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
public interface IResource{
	
	/**
	 * @return the description of this {@link IResource}.
	 */
	String getDescription();

	/**
	 * @return the label of this {@link IResource}.
	 */
	String getLabel();
	
	/**
	 * @return the nam of this {@link IResource}.
	 */
	String getName();
	
	/**
	 * @return the parent {@link Resource} of this one.
	 */
	IResource getParent();

	/**
	 * Set the description of this {@link IResource}.
	 * @param description the description to set
	 */
	void setDescription(String description);

	/**
	 * Set the label of this {@link IResource}.
	 * @param label of this {@link IResource}
	 */
	void setLabel(String label);

	/**
	 * Set the name of this {@link IResource}.
	 * @param name of this {@link IResource}
	 */
	void setName(String name);

	/**
	 * Set the parent of this {@Resource}.
	 * @param parent of this {@Resource}
	 */
	void setResource(IResource parent);
}
