package org.jbpt.petri.unfolding;

import org.jbpt.petri.unfolding.order.AdequateOrderType;

/**
 * Setup for construction of a branching process of a net system.
 * 
 * @author Artem Polyvyanyy
 */
public class CompletePrefixUnfoldingSetup {
	
	/**
	 * Do not append more than MAX_EVENTS events to the branching process.
	 */
	public int MAX_EVENTS = Integer.MAX_VALUE;
	
	/**
	 * Stop construction when identified a co-set which contains MAX_BOUND conditions that correspond to the same place of the originative system.
	 *    
	 * Only works if SAFE_OPTIMIZATION is set to <tt>false</tt>.
	 */
	public int MAX_BOUND = 1;
	
	/**
	 * Use this adequate order when constructing the unfolding.	
	 */
	public AdequateOrderType ADEQUATE_ORDER = AdequateOrderType.ESPARZA_FOR_SAFE_SYSTEMS;
	
	/**
	 * Use techniques for safe systems to optimize construction of the branching process.
	 * 
	 * @assumption The originative system is safe.
	 */
	public boolean SAFE_OPTIMIZATION = true;
}
