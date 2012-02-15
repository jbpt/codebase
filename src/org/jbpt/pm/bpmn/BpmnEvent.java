/**
 * 
 */
package org.jbpt.pm.bpmn;

import org.jbpt.pm.Event;

/**
 * Abstract class for BPMN events. It has a type attribute that contains the different event types.
 * It also has two more attributes: interrupting and attached. If both are true, it is an attached 
 * intermediate interrupting event. If only attached is true, it is an attached intermediate non-interrupting 
 * event. If only interrupted is true, it is an event-subprocess interrupting event. If both are false, it 
 * is an event-subprocess non-interrupting event. 
 * 
 * @author Cindy Fähnrich
 *
 */
public abstract class BpmnEvent extends Event implements IBpmnEvent {

	/**
	 * Determines the event type.
	 */
	private BpmnEventTypes.TYPES type = BpmnEventTypes.TYPES.BLANK;
	/**
	 * Determines whether this event is attached to an activity or not.
	 */
	private boolean attached = false;
	/**
	 * Determines whether this events interrupts the current activities or not.
	 */
	private boolean interrupting = false;
	
	/**
	 * Create a new Bpmn Event
	 */
	public BpmnEvent() {
		super();
	}

	/**
	 * Create a new Bpmn Event with the given name
	 * @param name of the event
	 */
	public BpmnEvent(String name) {
		super(name);
	}

	@Override
	public void setEventType(BpmnEventTypes.TYPES type) {
		this.type = type;
		
	}
	
	@Override
	public BpmnEventTypes.TYPES getEventType(){
		return this.type;
	}

	@Override
	public boolean isInterrupting() {
		return this.interrupting;
	}

	@Override
	public boolean isAttached() {
		return this.attached;
	}

	@Override
	public void setInterrupted(boolean interrupts) {
		this.interrupting = interrupts;		
	}

	@Override
	public void setAttached(boolean attached) {
		this.attached = attached;		
	} 

}
