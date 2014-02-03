/**
 * 
 */
package org.jbpt.pm.bparc;

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
	public ReceivingEvent(String label, int[] mult) {
		super(label, mult);
	}

	/**
	 * @param label
	 */
	public ReceivingEvent(String label) {
		super(label);
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
