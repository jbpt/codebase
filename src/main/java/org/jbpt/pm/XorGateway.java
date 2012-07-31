package org.jbpt.pm;

/**
 * Base class for a XOR-Gateway in a process model.
 * 
 * @author Tobias Hoppe
 *
 */
public class XorGateway extends Gateway implements IXorGateway{

	/**
	 * Create a new XOR-Gateway
	 */
	public XorGateway(){
		super();
	}

	/**
	 * Create a new XOR-Gateway
	 * @param name
	 */
	public XorGateway(String name){
		super(name);
	}
}
