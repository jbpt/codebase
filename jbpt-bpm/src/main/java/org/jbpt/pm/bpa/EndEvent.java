/**
 * 
 */
package org.jbpt.pm.bpa;




/**
 * @author rami.eidsabbagh
 *
 */
public class EndEvent extends SendingEvent implements IEndEvent {

	/**
	 * @param label
	 * @param mult
	 */
	public EndEvent(String label, BpaProcess enclosingProcess, int[] mult) {
		super(label, enclosingProcess, mult);
	}

	/**
	 * @param label
	 */
	public EndEvent(String label, BpaProcess enclosingProcess) {
		super(label, enclosingProcess);
	}
}
