package de.hpi.bpt.process.petri.unf;

import de.hpi.bpt.process.petri.Place;

/**
 * Condition of a branching process
 * 
 * @author Artem Polyvyanyy
 */
public class Condition extends BPNode {
	Place s = null;
	Event e = null;
	
	public Condition() {}
	
	public Condition(Place place, Event event) {
		this.s = place;
		this.e = event;
	}
	
	public Place getPlace() {
		return this.s;
	}
	
	public Event getEvent() {
		return this.e;
	}
	
	public void setPlace(Place place) {
		this.s = place;
	}
	
	public void setEvent(Event event) {
		this.e= event;
	}
	
	@Override
	public String toString() {
		return "["+this.getPlace().getName()+","+( this.getEvent()==null ? "null" : this.getEvent().getTransition().getName())+"]";
	}
	
	@Override
	public String getName() {
		return this.s.getName();
	}
}
