/**
 * 
 */
package org.jbpt.pm.bparc;




/**
 * @author rami.eidsabbagh
 *
 */
public class EndEvent extends SendingEvent implements IEndEvent {

	/**
	 * @param label
	 * @param mult
	 */
	public EndEvent(String label, int[] mult) {
		super(label, mult);
	}

	/**
	 * @param label
	 */
	public EndEvent(String label) {
		super(label);
	}
}
