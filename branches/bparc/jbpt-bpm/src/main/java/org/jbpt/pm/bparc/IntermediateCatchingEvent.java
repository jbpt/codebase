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
	public IntermediateCatchingEvent(String label, int[] mult) {
		super(label, mult);
	}

	/**
	 * @param label
	 */
	public IntermediateCatchingEvent(String label) {
		super(label);
	}

}
