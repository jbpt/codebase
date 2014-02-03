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
	public IntermediateThrowingEvent(String label, int[] mult) {
		super(label, mult);
	}

	/**
	 * @param label
	 */
	public IntermediateThrowingEvent(String label) {
		super(label);
	}	
}
