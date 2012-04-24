package org.jbpt.petri2;

/**
 * Petri net transition implementation
 * 
 * @author Artem Polyvyanyy
 */
public class Transition extends Node {
	/**
	 * Empty constructor
	 */
	public Transition() {
		super();
	}
	
	/**
	 * Constructor with transition name parameter
	 * @param name Transition name
	 */
	public Transition(String name) {
		super(name);
	}
	
	/**
	 * Constructor with transition name and description parameters
	 * @param name Transition name
	 * @param desc Transition description
	 */
	public Transition(String name, String desc) {
		super(name,desc);
	}
	
	@Override
	public Transition clone() {
		return (Transition) super.clone();
	}
	
	public boolean isSilent() {
		return this.getLabel().isEmpty();
	}
	
	public boolean isObservable() {
		return !this.getLabel().isEmpty();
	}
}
