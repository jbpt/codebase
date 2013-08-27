package org.jbpt.petri;

/**
 * Implementation of a Petri net place.
 * 
 * @author Artem Polyvyanyy
 */
public class Place extends Node implements IPlace {

	public Place() {
		super();
	}
	
	public Place(String name) {
		super(name);
	}
	
	public Place(String name, String label) {
		super(label,label);
	}
	
	public Place(String name, String label, String desc) {
		super(label,label,desc);
	}
	
	@Override
	public IPlace clone() {
		return (IPlace) super.clone();
	}
}
