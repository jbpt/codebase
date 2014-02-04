/**
 * 
 */
package org.jbpt.pm.bparc;


/**
 * @author rami.eidsabbagh
 *
 */
public class IntermediateThrowingEvent extends SendingEvent implements IIntermediateThrowingEvent {

	/**
	 * @param label
	 * @param mult
	 */
	public IntermediateThrowingEvent(String label, BparcProcess enclosingProcess, int[] mult) {
		super(label, enclosingProcess, mult);
	}

	/**
	 * @param label
	 */
	public IntermediateThrowingEvent(String label, BparcProcess enclosingProcess) {
		super(label, enclosingProcess);
	}	
}
