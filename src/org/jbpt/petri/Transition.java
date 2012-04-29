package org.jbpt.petri;

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
	
	/**
	 * Check if transition is silent
	 * @return <code>true</code> if label is the empty string; <code>false</code> otherwise
	 */
	public boolean isSilent() {
		return this.getLabel().isEmpty();
	}

	/**
	 * Check if transition is observable
	 * @return <code>true</code> if label is not the empty string; <code>false</code> otherwise
	 */
	public boolean isObservable() {
		return !this.getLabel().isEmpty();
	}
}
