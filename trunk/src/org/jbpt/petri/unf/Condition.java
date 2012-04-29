package org.jbpt.petri.unf;

import org.jbpt.petri.Node;
import org.jbpt.petri.Place;

/**
 * Unfolding condition
 * 
 * @author Artem Polyvyanyy
 */
public class Condition extends BPNode {
	
	static private int count = 0; 
	private int ID = 0;
	
	Place s = null;
	Event e = null;
	
	/**
	 * Constructor
	 * @param place corresponding place in the originative net
	 * @param event the only event in the preset of the condition
	 */
	public Condition(Place place, Event event) {
		this.ID = ++Condition.count;
		
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
		return "["+this.getName()+","+( this.getPreEvent()==null ? "null" : this.getPreEvent().getTransition().getName())+"]";
	}
	
	@Override
	public String getName() {
		return this.s.getName()+"-"+this.ID;
	}
	
	@Override
	public boolean equals(Object that) {
		if (that == null || !(that instanceof Condition)) return false;
		if (this == that) return true;
		
		Condition thatC = (Condition) that;
		if (this.getPlace().equals(thatC.getPlace())) {
			if (this.getPreEvent()==null) {
				if (thatC.getPreEvent()==null) return true;
				return false;
			}
			else {
				if (this.getPreEvent().equals(thatC.getPreEvent())) return true;
				return false;
			}
		}
		
		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = 0;
		hashCode += this.getPlace()==null ? 0 : 7 * this.getPlace().hashCode();
		hashCode += this.getPreEvent()==null ? 0 : 11 * this.getPreEvent().getTransition().hashCode();
		
		return hashCode;
	}

	@Override
	public Node getNode() {
		return this.getPlace();
	}
}
