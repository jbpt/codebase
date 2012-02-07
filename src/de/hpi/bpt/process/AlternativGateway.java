/**
 * 
 */
package de.hpi.bpt.process;

/**
 * Class for all {@link IGateway}s that do not match any of the other behaviors.
 * 
 * @author Tobias Hoppe
 *
 */
public class AlternativGateway extends Gateway implements IGateway {

	/**
	 * Create a new gateway with self defined behavior.
	 */
	public AlternativGateway() {
		super();
	}

	/**
	 * Create a new gateway with self defined behavior and the given name.
	 * @param name of this {@link AlternativGateway}
	 */
	public AlternativGateway(String name) {
		super(name);
	}

}
