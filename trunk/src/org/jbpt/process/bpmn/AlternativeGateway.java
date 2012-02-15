/**
 * 
 */
package de.hpi.bpt.process.bpmn;

import de.hpi.bpt.process.AlternativGateway;
import de.hpi.bpt.process.IAlternativGateway;

/**
 * Class for alternative gateways. Alternative Gateways are ExclusiveEvent-basedGateway and 
 * ParallelEvent-basedGateway.
 * @author Tobias Hoppe, Cindy Fähnrich
 *
 */
public class AlternativeGateway extends AlternativGateway implements
		IAlternativGateway {

	/**
	 * 
	 */
	public AlternativeGateway() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 */
	public AlternativeGateway(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

}
