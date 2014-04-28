/**
 * 
 */
package org.jbpt.pm.bpa;


/**
 * @author rami.eidsabbagh
 *
 */
public class IntermediateThrowingEvent extends SendingEvent implements IIntermediateThrowingEvent {

	/**
	 * @param label
	 * @param mult
	 */
	public IntermediateThrowingEvent(String label, BpaProcess enclosingProcess, int[] mult) {
		super(label, enclosingProcess, mult);
	}

	/**
	 * @param label
	 */
	public IntermediateThrowingEvent(String label, BpaProcess enclosingProcess) {
		super(label, enclosingProcess);
	}	
}
