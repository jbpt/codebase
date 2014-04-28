/**
 * 
 */
package org.jbpt.pm.bpa;

import java.util.Collection;
import java.util.LinkedList;

import org.jbpt.pm.FlowNode;

/**
 * @author rami.eidsabbagh
 *
 */
public abstract class ReceivingEvent extends Event implements IReceivingEvent {

	/**
	 * @param label
	 * @param mult
	 */
	public ReceivingEvent(String label, BpaProcess enclosingProcess, int[] mult) {
		super(label, enclosingProcess, mult);
	}

	/**
	 * @param label
	 */
	public ReceivingEvent(String label, BpaProcess enclosingProcess) {
		super(label, enclosingProcess);
	}

	public Collection<ISendingEvent> getPreset(){
		Collection<FlowNode> predecessors = this.getModel().getDirectPredecessors(this);
		Collection<IEvent> processEvents = this.getEnclosingProcess().getEvents();
		Collection<ISendingEvent> preset = new LinkedList<ISendingEvent>();
		
		// filter events that are not inside of my process
		for (FlowNode predecessor : predecessors) {
			if (predecessor instanceof ISendingEvent) {
				if (!processEvents.contains((IEvent)predecessor)) {
					preset.add((ISendingEvent)predecessor);
				}
			}
		}
		
		return preset;
	}
}
