/**
 * 
 */
package org.jbpt.pm.bpmn;

import org.jbpt.pm.Activity;

/**
 * This the super class of all BPMN activities, such as Subprocess or Task.
 * 
 * @author Cindy Fähnrich
 *
 */
public abstract class BpmnActivity extends Activity implements IBpmnActivity {

	/**
	 * boolean for loop activity
	 */
	private boolean standardLoop = false;
	
	/**
	 * boolean for parallel execution for multiple times 
	 */
	private boolean parallelMultiple = false; 
	
	/**
	 * boolean for sequential execution for multiple times 
	 */
	private boolean sequentialMultiple = false; 
	
	/**
	 * boolean for sequential execution for multiple times compensation activity
	 */
	private boolean compensation = false;
	
	/**
	 * 
	 */
	public BpmnActivity() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name the label name of the activity
	 * @param desc the description of the activity
	 */
	public BpmnActivity(String name, String desc) {
		super(name, desc);
	}

	/**
	 * @param name
	 */
	public BpmnActivity(String name) {
		super(name);
	}

	@Override
	public boolean isStandardLoop() {
		return this.standardLoop;
	}

	@Override
	public boolean isParallelMultiple() {
		return this.parallelMultiple;
	}

	@Override
	public boolean isSequentialMultiple() {
		return this.sequentialMultiple;
	}

	@Override
	public boolean isCompensation() {
		return this.compensation;
	}

	@Override
	public void setStandardLoop(boolean loop) {
		this.standardLoop = loop;
	}

	@Override
	public void setParallelMultiple(boolean parallel) {
		this.parallelMultiple = parallel;
	}

	@Override
	public void setSequentialMultiple(boolean sequential) {
		this.sequentialMultiple = sequential;
	}

	@Override
	public void setCompensation(boolean compensation) {
		this.compensation = compensation;
	}

}
