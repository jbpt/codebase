/**
 * 
 */
package org.jbpt.pm.bpa;




/**
 * @author rami.eidsabbagh
 *
 */
public class StartEvent extends ReceivingEvent implements IStartEvent {

	
	/**
	 * @param label
	 * @param mult
	 */
	public StartEvent(String label, BpaProcess enclosingProcess, int[] mult) {
		super(label, enclosingProcess, mult);
	}

	/**
	 * @param label
	 */
	public StartEvent(String label, BpaProcess enclosingProcess) {
		super(label, enclosingProcess);
	}

	/**
	 * Checks whether this StartEvent is an initial place, ie. whether
	 * is has no incoming triggers. In this case its place in the 
	 * PetriNet representation is marked.
	 * @return
	 */
	public boolean isInitialPlace() {
		return (getPreset() == null || getPreset().isEmpty());
	}
}
