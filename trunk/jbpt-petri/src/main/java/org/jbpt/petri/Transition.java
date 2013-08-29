package org.jbpt.petri;

/**
 * Implementation of a Petri net transition. 
 * 
 * @author Artem Polyvyanyy
 */
public class Transition extends Node implements ITransition {
	
	public Transition() {
		super();
	}
	
	public Transition(String name) {
		super(name);
	}
	
	public Transition(String name, String label) {
		super(name,label);
	}
	
	public Transition(String name, String label, String desc) {
		super(name,label,desc);
	}
	
	@Override
	public boolean isSilent() {
		return this.getLabel().isEmpty();
	}

	@Override
	public boolean isObservable() {
		return !this.isSilent();
	}
	
	@Override
	public ITransition clone() {
		return (ITransition) super.clone();
	}
}
