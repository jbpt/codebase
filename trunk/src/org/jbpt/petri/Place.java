package org.jbpt.petri;

/**
 * Petri net place implementation
 * 
 * @author Artem Polyvyanyy
 */
public class Place extends Node {
	/**
	 * Empty constructor
	 */
	public Place() {
		super();
	}
	
	/**
	 * Constructor with place name parameter
	 * @param name Place name
	 */
	public Place(String name) {
		super(name);
	}
	
	/**
	 * Constructor with place name and description parameters
	 * @param name Place name
	 * @param desc Place description
	 */
	public Place(String name, String desc) {
		super(name,desc);
	}
	
	@Override
	public Place clone() {
		return (Place) super.clone();
	}
}
