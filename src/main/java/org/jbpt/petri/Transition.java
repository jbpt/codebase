package org.jbpt.petri;

/**
 * Implementation of a Petri net transition. 
 * 
 * @author Artem Polyvyanyy
 */
public class Transition extends Node implements ITransition {
	
	/**
	 * Empty constructor.
	 */
	public Transition() {
		super();
	}
	
	/**
	 * Constructor with label of the transition parameter.
	 *  
	 * @param label String to use as a label of this transition. 
	 */
	public Transition(String label) {
		super(label);
	}
	
	/**
	 * Constructor with label and description of the transition parameters.
	 * 
	 * @param label String to use as a label of this transition. 
	 * @param desc String to use as a description of this transition. 
	 */
	public Transition(String label, String desc) {
		super(label,desc);
	}
	
	@Override
	public boolean isSilent() {
		return this.getLabel().isEmpty();
	}

	@Override
	public boolean isObservable() {
		return !this.isSilent();
	}
}
