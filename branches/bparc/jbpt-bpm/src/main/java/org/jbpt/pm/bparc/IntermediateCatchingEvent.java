/**
 * 
 */
package org.jbpt.pm.bparc;


/**
 * @author rami.eidsabbagh
 *
 */
public class IntermediateCatchingEvent extends ReceivingEvent implements IReceivingEvent {

	/**
	 * @param label
	 * @param mult
	 */
	public IntermediateCatchingEvent(String label, BparcProcess enclosingProcess, int[] mult) {
		super(label, enclosingProcess, mult);
	}

	/**
	 * @param label
	 */
	public IntermediateCatchingEvent(String label, BparcProcess enclosingProcess) {
		super(label, enclosingProcess);
	}

}
