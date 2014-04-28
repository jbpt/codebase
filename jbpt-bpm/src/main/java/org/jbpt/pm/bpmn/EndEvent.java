package org.jbpt.pm.bpmn;

/**
 * Class for BPMN End Event.
 * @author Cindy Fähnrich
 *
 */
public class EndEvent extends BpmnEvent implements IEndEvent{
	
	/**
	 * Constructor
	 */
	public EndEvent() {
		super();
		setType(EventType.SENDING);
	}

	/**
	 * Constructor setting the name/title of this event.
	 * @param name
	 */
	public EndEvent(String name) {
		super(name);
		setType(EventType.SENDING);
	}
}
