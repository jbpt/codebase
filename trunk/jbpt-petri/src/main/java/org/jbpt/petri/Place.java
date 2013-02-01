package org.jbpt.petri;

/**
 * Implementation of a Petri net place.
 * 
 * @author Artem Polyvyanyy
 */
public class Place extends Node implements IPlace {
	
	/**
	 * Empty constructor.
	 */
	public Place() {
		super();
	}
	
	/**
	 * Constructor with label of the place parameter.
	 *  
	 * @param label String to use as a label of this place. 
	 */
	public Place(String label) {
		super(label);
	}
	
	/**
	 * Constructor with label and description of the place parameters.
	 * 
	 * @param label String to use as a label of this place. 
	 * @param desc String to use as a description of this place. 
	 */
	public Place(String label, String desc) {
		super(label,desc);
	}
	
	@Override
	public IPlace clone() {
		return (IPlace) super.clone();
	}
}
