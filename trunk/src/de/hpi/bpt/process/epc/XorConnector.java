/**
 * 
 */
package de.hpi.bpt.process.epc;

import de.hpi.bpt.process.XorGateway;

/**
 * Class for EPC Xor-Connector
 * @author Tobias Hoppe
 *
 */
public class XorConnector extends XorGateway implements IXorConnector {

	/**
	 * Create a new connector with the <code>XOR</code> behavior and the given name.
	 * @param name of this connector
	 */
	public XorConnector(String name) {
		super(name);
	}

	/**
	 * Create a new connector with the <code>XOR</code> behavior.
	 */
	public XorConnector() {
		
	}

}
