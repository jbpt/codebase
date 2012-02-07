package de.hpi.bpt.process;

/**
 * Base class for all model elements in a {@link IProcessModel} representing an event.
 * 
 * @author Tobias Hoppe
 *
 */
public class Event extends FlowNode {
	
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

}
