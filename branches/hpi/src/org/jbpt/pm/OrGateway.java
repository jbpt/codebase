package org.jbpt.pm;

/**
 * Base class for a OR-Gateway in a process model.
 * @author Tobias Hoppe
 *
 */
public class OrGateway extends Gateway implements IOrGateway{

	/**
	 * Create a new OR-Gateway.
	 */
	public OrGateway(){
		super();
	}

	/**
	 * Create a new OR-Gateway with the given name.
	 * @param name
	 */
	public OrGateway(String name){
		super(name);
	}
}
