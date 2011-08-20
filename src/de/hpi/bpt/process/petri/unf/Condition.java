package de.hpi.bpt.process.petri.unf;

import de.hpi.bpt.process.petri.Place;

/**
 * Unfolding condition
 * 
 * @author Artem Polyvyanyy
 */
public class Condition extends BPNode {
	Place s = null;
	Event e = null;
	
	protected Condition(Place place, Event event) {
		this.s = place;
		this.e = event;
	}
	
	public Place getPlace() {
		return this.s;
	}
	
	public Event getPreEvent() {
		return this.e;
	}
	
	public void setPlace(Place place) {
		this.s = place;
	}
	
	@Override
	public String toString() {
		return "["+this.getPlace().getName()+","+( this.getPreEvent()==null ? "null" : this.getPreEvent().getTransition().getName())+"]";
	}
	
	@Override
	public String getName() {
		return this.s.getName();
	}
}
