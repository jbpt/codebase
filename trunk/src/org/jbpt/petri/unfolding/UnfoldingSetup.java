package org.jbpt.petri.unfolding;

import org.jbpt.petri.unfolding.order.AdequateOrder;
import org.jbpt.petri.unfolding.order.EsparzaAdequateTotalOrderForSafeSystems;

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
	public AdequateOrder ADEQUATE_ORDER = new EsparzaAdequateTotalOrderForSafeSystems();	// use this adequate order	
	public boolean SAFE_OPTIMIZATION = false; 							// !!! will be changed to true !!!
}
