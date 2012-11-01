/**
 * 
 */
package org.jbpt.pm.bpmn;

import org.jbpt.pm.XorGateway;

/**
 * Class for BPMN event-based Gateway. Since this Gateway has Xor semantics, but is only for events,
 * it inherits from XorGateway.
 * 
 * @author Cindy Fähnrich
 *
 */
public class EventBasedXorGateway extends XorGateway implements IEventBasedXorGateway{

	/**
	 * @param name the name/title of this gateway
	 */
	public EventBasedXorGateway(String name) {
		super(name);
	}

	/**
	 * Constructor
	 */
	public EventBasedXorGateway() {
		super();
	}

}
