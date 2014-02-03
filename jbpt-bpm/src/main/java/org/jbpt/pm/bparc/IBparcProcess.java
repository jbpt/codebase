/**
 * 
 */
package org.jbpt.pm.bparc;

import java.util.Collection;

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
	public Collection<Event> getEvents();

}
