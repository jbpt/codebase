/**
 * 
 */
package org.jbpt.pm.bpa;


/**
 * @author rami.eidsabbagh
 *
 */
public class IntermediateCatchingEvent extends ReceivingEvent implements IReceivingEvent {

	/**
	 * @param label
	 * @param mult
	 */
	public IntermediateCatchingEvent(String label, BpaProcess enclosingProcess, int[] mult) {
		super(label, enclosingProcess, mult);
	}

	/**
	 * @param label
	 */
	public IntermediateCatchingEvent(String label, BpaProcess enclosingProcess) {
		super(label, enclosingProcess);
	}

}
