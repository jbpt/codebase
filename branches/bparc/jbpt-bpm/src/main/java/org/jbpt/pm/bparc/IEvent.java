/**
 * 
 */
package org.jbpt.pm.bparc;


/**
 * @author Robert Breske and Marcin Hewelt
 *
 */
public interface IEvent extends org.jbpt.pm.IEvent {
	
	/**
	 * @param bparcProcess
	 */
	public void setEnclosingProcess(BparcProcess bparcProcess);
	
	public BparcProcess getEnclosingProcess();

}
