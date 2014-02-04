package org.jbpt.pm.bparc;

import java.util.Collection;
import java.util.LinkedList;

import org.jbpt.pm.FlowNode;

/**
 * @author rami.eidsabbagh
 *
 */
public abstract class SendingEvent extends Event implements ISendingEvent {
	
	Collection<IReceivingEvent> postset;
	
	/**
	 * @param label
	 * @param mult
	 */
	public SendingEvent(String label, BparcProcess enclosingProcess, int[] mult) {
		super(label, enclosingProcess, mult);
	}

	/**
	 * @param label
	 */
	public SendingEvent(String label, BparcProcess enclosingProcess) {
		super(label, enclosingProcess);
	}
	
	public Collection<IReceivingEvent> getPostset() {
		if (this.postset == null) {
			this.postset = calculatePostset(new LinkedList<IReceivingEvent>(), this);
		}
		return this.postset;
	}
	
	private Collection<IReceivingEvent> calculatePostset(Collection<IReceivingEvent> postset, FlowNode node) {
		if (node instanceof IReceivingEvent) {
			if (!this.getEnclosingProcess().getEvents().contains((IEvent) node))
				postset.add((IReceivingEvent)node);
			return postset;
		} else {
			for (FlowNode successorNode : this.getModel().getDirectSuccessors(node)) {
				calculatePostset(postset, successorNode);
			}
			return postset;
		}
	}

	@Override
	public Collection<IReceivingEvent> getConflictSet() {
		// TODO Auto-generated method stub
		return null;
	}
}
