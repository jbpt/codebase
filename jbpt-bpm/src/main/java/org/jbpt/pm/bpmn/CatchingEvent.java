/**
 * 
 */
package org.jbpt.pm.bpmn;

/**
 * Class for BPMN Catching Event.
 * @author Cindy Fähnrich
 *
 */
public class CatchingEvent extends BpmnEvent implements ICatchingEvent{

	/**
	 * Constructor
	 */
	public CatchingEvent() {
		super();
		setType(EventType.RECEIVING);
	}

	/**
	 * @param name the name/title of this event
	 */
	public CatchingEvent(String name) {
		super(name);
		setType(EventType.RECEIVING);
	}

}
