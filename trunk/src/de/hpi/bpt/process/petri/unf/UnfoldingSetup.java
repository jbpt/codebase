package de.hpi.bpt.process.petri.unf;

import de.hpi.bpt.process.petri.unf.order.AdequateOrder;
import de.hpi.bpt.process.petri.unf.order.McMillanAdequateOrder;

/**
 * Unfolding setup
 * 
 * Uses McMillan adequate order by default
 * 
 * @author Artem Polyvyanyy
 */
public class UnfoldingSetup {
	public int MAX_BOUND = 1;											// stop unfolding when identified co-set which contains MAX_BOUND conditions that correspond to the same place
	public int MAX_EVENTS = Integer.MAX_VALUE;							// do not append more than MAX_EVENTS events
	public AdequateOrder ADEQUATE_ORDER = new McMillanAdequateOrder();	// use this adequate order
}
