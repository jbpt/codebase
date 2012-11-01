/**
 * 
 */
package org.jbpt.pm.bpmn;

/**
 * Class for BPMN Throwing Event.
 * @author Cindy Fähnrich
 *
 */
public class ThrowingEvent extends BpmnEvent implements IThrowingEvent {

	/**
	 * Constructor
	 */
	public ThrowingEvent() {
		super();
	}

	/**
	 * @param name the name/title of this event
	 */
	public ThrowingEvent(String name) {
		super(name);
	}

}
