/**
 * 
 */
package de.hpi.bpt.process.epc;

import de.hpi.bpt.process.OrGateway;

/**
 * Class for EPC Or-Connector.
 * @author Tobias Hoppe
 *
 */
public class OrConnector extends OrGateway implements IOrConnector {

	/**
	 * Create a new connector with the <code>OR</code> behavior.
	 */
	public OrConnector() {
	
	}

	/**
	 * Create a new connector with the <code>OR</code> behavior and the given name.
	 * @param name of this connector
	 */
	public OrConnector(String name) {
		super(name);
	}

}
