package de.hpi.bpt.process.petri;

/**
 * Petri net transition implementation
 * 
 * @author artem.polyvyanyy
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

}
