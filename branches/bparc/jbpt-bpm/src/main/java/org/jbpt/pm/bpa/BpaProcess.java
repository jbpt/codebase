package org.jbpt.pm.bpa;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jbpt.pm.NonFlowNode;

public class BpaProcess extends NonFlowNode implements IBpaProcess {
	
	List<IEvent> events = new ArrayList<IEvent>();
	private String shapeId;
	
	/**
	 * @param name
	 * @param description
	 */
	public BpaProcess(String name, String resourceId) {
		super(name);
		this.shapeId = resourceId;
	}

	public List<IEvent> getEvents() {
		return events;
	}
	
	public void addEvent(IEvent e) {
		events.add(e);
	}
	
	public void addEvents(List<IEvent> events) {
		events.addAll(events);
	}
	
	public UUID getUid() {
		//TODO: probably unnecessary
		return UUID.fromString(super.getId());
	}
	
	public String getShapeId() {
		return shapeId;
	}
	
}
