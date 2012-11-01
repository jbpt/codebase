/**
 * 
 */
package org.jbpt.pm.epc;

import org.jbpt.pm.AndGateway;

/**
 * Class for Epc And-Connector.
 * @author Tobias Hoppe
 *
 */
public class AndConnector extends AndGateway implements IAndConnector {

	/**
	 * Create a new connector with the <code>AND</code> behavior.
	 */
	public AndConnector() {

	}

	/**
	 * Create a new connector with the <code>AND</code> behavior and the given name.
	 * @param name of this connector
	 */
	public AndConnector(String name) {
		super(name);
	}

}
