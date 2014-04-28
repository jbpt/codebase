/**
 * 
 */
package org.jbpt.pm.bpa;

import java.util.List;

import org.jbpt.pm.INonFlowNode;

/**
 * @author Robert Breske and Marcin Hewelt
 *
 */
public interface IBpaProcess extends INonFlowNode {
	
	/**
	 * Returns all events inside of this BPA Process
	 * @return Collection of events
	 */
	public List<IEvent> getEvents();

}
