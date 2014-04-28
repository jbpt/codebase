/**
 * 
 */
package org.jbpt.pm.bpa;


/**
 * @author Robert Breske and Marcin Hewelt
 *
 */
public interface IEvent extends org.jbpt.pm.IEvent {
	
	public void setEnclosingProcess(BpaProcess enclosingProcess);
	
	public void setMultiplicity(int[] mult);
	
	public int[] getMultiplicity();
	
	public boolean hasTrivialMultiplicity();
	
	public BpaProcess getEnclosingProcess();

}
