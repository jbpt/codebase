/**
 * 
 */
package org.jbpt.pm.bparc;

import java.util.List;

import org.jbpt.pm.INonFlowNode;

/**
 * @author Robert Breske and Marcin Hewelt
 *
 */
public interface IBparcProcess extends INonFlowNode {
	
	/**
	 * Returns all events inside of this BParc Process
	 * @return Collection of events
	 */
	public List<IEvent> getEvents();

}
