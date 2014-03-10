package org.jbpt.pm;

/**
 * Base class for all model elements in a {@link IProcessModel} representing an event.
 * 
 * @author Tobias Hoppe
 *
 */
public class Event extends FlowNode {
	
	public enum EventType {
		REVEIVECING,SENDING
	}
	
	private EventType type;
	
	/**
	 * Creates a new {@link Event} with an empty name.
	 */
	public Event (){
		super();
	}
	
	/**
	 * Creates a new {@link Event} with the given name.
	 * @param name of this {@link Event}
	 */
	public Event (String name){
		super(name);
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

}
