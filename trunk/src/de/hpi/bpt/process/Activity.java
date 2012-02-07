package de.hpi.bpt.process;


/**
 * Base class for all nodes of a {@link IProcessModel} that represent something that can be executed
 * (e.g. task, activity, ...).
 * 
 * @author Tobias Hoppe
 *
 */
public class Activity extends FlowNode {
	
	/**
	 * Create a new {@link Activity} with an empty name.
	 */
	public Activity() {
		super();
	}

	/**
	 * Create a new {@link Activity} with the given name.
	 * @param name of this {@link Activity}
	 */
	public Activity(String name) {
		super(name);
	}

	/**
	 * Create a new {@link Activity} with the given name and description.
	 * @param name of this {@link Activity}
	 * @param description of this {@link Activity}
	 */
	public Activity(String name, String description) {
		super(name, description);
	}

	
}
