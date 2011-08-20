package de.hpi.bpt.process.petri.unf;

import de.hpi.bpt.process.petri.unf.order.AdequateOrder;
import de.hpi.bpt.process.petri.unf.order.McMillanAdequateOrder;

/**
 * Configure unfolding
 * 
 * @author Artem Polyvyanyy
 */
public class Setup {
	public int MAX_BOUND = 1;
	public int MAX_EVENTS = Integer.MAX_VALUE;
	public AdequateOrder ADEQUATE_ORDER = new McMillanAdequateOrder();
}
