/**
 * 
 */
package org.jbpt.pm.bpmn;

/**
 * This is a simple BPMN Task.
 * 
 * @author Cindy Fähnrich
 *
 */
public class Task extends BpmnActivity implements ITask{

	/**
	 * Constructor
	 */
	public Task() {
		super();
	}

	/**
	 * @param name the name/title of this task
	 * @param desc the description to this task
	 */
	public Task(String name, String desc) {
		super(name, desc);
	}

	/**
	 * @param name the name/title of this task
	 */
	public Task(String name) {
		super(name);
	}

}
