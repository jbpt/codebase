package org.jbpt.pm.bparc;

import java.util.Collection;
import java.util.LinkedList;

import org.jbpt.pm.FlowNode;

/**
 * @author rami.eidsabbagh
 *
 */
public abstract class SendingEvent extends Event implements ISendingEvent {
	
//	List<IReceivingEvent> postset = new ArrayList<IReceivingEvent>();
	
	
	

	/**
	 * @param label
	 * @param mult
	 */
	public SendingEvent(String label, int[] mult) {
		super(label, mult);
	}

	/**
	 * @param label
	 */
	public SendingEvent(String label) {
		super(label);
	}
	
	public Collection<IReceivingEvent> getPostset(){
		Collection<FlowNode> successors = this.getModel().getDirectSuccessors(this);
		Collection<IEvent> processEvents = this.getEnclosingProcess().getEvents();
		Collection<IReceivingEvent> postset = new LinkedList<IReceivingEvent>();
		
		// filter events that are not inside of my process
		for (FlowNode successor : successors) {
			if (successor instanceof IReceivingEvent) {
				if (!processEvents.contains((IEvent)successor)) {
					postset.add((IReceivingEvent)successor);
				}
			}
		}
		
		return postset;
	}
}
