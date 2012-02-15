/**
 * 
 */
package de.hpi.bpt.process.bpmn;

import de.hpi.bpt.process.IActivity;

/**
 * This is the interface for a BPMN Activity, which is the super class of all other activity types in BPMN notation. It has different marks
 * such as for loop execution or for multiple executions in parallel or sequential order (relevant for subprocesses).
 * 
 * @author Cindy Fähnrich
 *
 */
public interface IBpmnActivity extends IActivity {
	
	/**
	 * @return whether this activity is a simple loop
	 */
	public boolean isStandardLoop();
	/**
	 * @return whether this activity has a parallel execution of
	 * multiple instances.
	 */
	public boolean isParallelMultiple();
	/**
	 * @return whether this activity has a sequential execution of
	 * multiple instances.
	 */
	public boolean isSequentialMultiple();
	/**
	 * @return whether this is a compensation acitivity
	 */
	public boolean isCompensation();
	/**
	 * Sets this activity to be a simple loop (or not).
	 * @param loop
	 */
	public void setStandardLoop(boolean loop);
	/**
	 * Sets this activity to have multiple instances with parallel execution (or not).
	 * @param loop
	 */
	public void setParallelMultiple(boolean parallel);
	/**
	 * Sets this activity to have multiple instances with sequential execution (or not).
	 * @param loop
	 */
	public void setSequentialMultiple(boolean sequential);
	/**
	 * Sets this activity to be a compensation activity (or not).
	 * @param loop
	 */
	public void setCompensation(boolean compensation);
	
}
