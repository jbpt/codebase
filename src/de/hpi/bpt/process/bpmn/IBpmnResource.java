/**
 * 
 */
package de.hpi.bpt.process.bpmn;

import de.hpi.bpt.process.IResource;

/**
 * Interface class for {@link BpmnResource}
 * @author Tobias Hoppe
 *
 */
public interface IBpmnResource extends IResource {

	/**
	 * Sets the Resource type ("Lane" or "Pool").
	 * @param type
	 */
	public void setType(String type);
	
	/**
	 * Returns the Resource type ("Lane" or "Pool").
	 * @return type
	 */
	public String getType();
}
