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
	public EndEvent(String label, BparcProcess enclosingProcess, int[] mult) {
		super(label, enclosingProcess, mult);
	}

	/**
	 * @param label
	 */
	public EndEvent(String label, BparcProcess enclosingProcess) {
		super(label, enclosingProcess);
	}
}
