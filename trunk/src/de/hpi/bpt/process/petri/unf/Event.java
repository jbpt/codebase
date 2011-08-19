package de.hpi.bpt.process.petri.unf;

import java.util.Set;

import com.google.gwt.dev.util.collect.HashSet;

import de.hpi.bpt.process.petri.Transition;

/**
 * Event of a branching process
 * 
 * @author Artem Polyvyanyy
 */
public class Event extends BPNode {
	private Transition t = null;
	private Set<Condition> B = null;
	protected Set<Condition> post = new HashSet<Condition>();
	
	public Event() {}
	
	public Event(Transition transition, Set<Condition> conditions) {
		this.B = conditions;
		this.t = transition;
	}
	
	public Set<Condition> getConditions() {
		return this.B;
	}
	
	public Transition getTransition() {
		return this.t;
	}
	
	public void setConditions(Set<Condition> conditions) {
		this.B = conditions;
	}
	
	public void setTransition(Transition transition) {
		this.t= transition;
	}
	
	@Override
	public String toString() {
		return "["+this.getTransition().getName()+","+this.getConditions()+"]";
	}
	
	@Override
	public String getName() {
		return this.t.getName();
	}
}
