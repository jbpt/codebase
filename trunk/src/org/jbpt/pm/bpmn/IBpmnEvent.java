/**
 * 
 */
package org.jbpt.pm.bpmn;

import org.jbpt.pm.IEvent;

/**
 * Interface for BPMN event class.
 * 
 * @author Cindy Fähnrich, Tobias Hoppe
 *
 */
public interface IBpmnEvent extends IEvent {

	/**
	 * Sets the event type.
	 * @param type
	 */
	public void setEventType(BpmnEventTypes.TYPES type);
	
	/**
	 * @return the event type
	 */
	public BpmnEventTypes.TYPES getEventType();
	
	/**
	 * Checks whether this is an interrupting event.
	 * @return
	 */
	public boolean isInterrupting();
	
	/**
	 * Checks whether this is an attached event.
	 * @return
	 */
	public boolean isAttached();
	
	/**
	 * Sets this event as interrupted (or not).
	 * @param interrupts
	 */
	public void setInterrupted(boolean interrupts);
	
	/**
	 * Sets this event as attached (or not).
	 * @param attached
	 */
	public void setAttached(boolean attached);
}
