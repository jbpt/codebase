package org.jbpt.pm.bpa;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.ProcessModel;

public class Bpa extends ProcessModel implements IBpa {

	private String name;
	private String organisation;
	private String shapeId;

	public Bpa() {

	}
	
	@Override
	public Bpa clone() {
		Bpa clone = (Bpa) super.clone();
		clone.setName(this.name);
		clone.setOrganisation(this.organisation);
		clone.setShapeId(this.shapeId);
		
		return clone;
	}

	private void setShapeId(String shapeId) {
		this.shapeId = shapeId;
	}

	@Override
	public ControlFlow<FlowNode> addControlFlow(FlowNode from, FlowNode to,
			float probability) {
		ControlFlow<FlowNode> flow;
		if (from instanceof IEvent &&
				to instanceof IEvent &&
				((IEvent)from).getEnclosingProcess().equals(((IEvent) to).getEnclosingProcess())) {
			// both events are enclosed by the same BPA process -> internal flow
			flow = new InternalFlow<>(this, from, to, probability);	
		} else {
			// all other cases -> external flow
			flow = new ExternalFlow<FlowNode>(this, from, to, probability);
		}

		// link flow nodes to model
		from.setModel(this);
		to.setModel(this);
		Set<FlowNode> set = new HashSet<FlowNode>();
		set.add(flow.getSource());
		set.add(flow.getTarget());
		this.edges.put(flow, set);

		return flow;
	}

	@SuppressWarnings("unchecked")
	public Collection<BpaProcess> getAllProcesses() {
		return (Collection<BpaProcess>) this.filter(BpaProcess.class);
	}

	public String getCanvasId() {
		return shapeId;
	}

	public void setCanvasId(String id) {
		this.shapeId = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

	/* (non-Javadoc)
	 * @see org.jbpt.pm.bpa.IBpa#getAllEvents()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection<Event> getAllEvents() {
		return (Collection<Event>) this.filter(Event.class);
	}


}

