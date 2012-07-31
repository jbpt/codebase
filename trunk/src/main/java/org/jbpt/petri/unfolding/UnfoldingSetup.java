package org.jbpt.petri.unfolding;

import org.jbpt.petri.unfolding.order.AdequateOrder;
import org.jbpt.petri.unfolding.order.EsparzaAdequateTotalOrderForSafeSystems;

/**
 * (Prefix) unfolding setup. 
 * 
 * This class should be used to parameterize the unfolding algorithm, see {@link Unfolding}.
 * 
 * @author Artem Polyvyanyy
 */
public class UnfoldingSetup {
	
	/**
	 * Do not append more than MAX_EVENTS events to the unfolding.
	 */
	public int MAX_EVENTS = Integer.MAX_VALUE;
	
	/**
	 * Stop unfolding when identified a co-set which contains MAX_BOUND conditions that correspond to the same place.   
	 * Only works if SAFE_OPTIMIZATION is set to <tt>false</tt>.
	 */
	public int MAX_BOUND = 1;
	
	/**
	 * Use this adequate order when constructing the unfolding.	
	 */
	public AdequateOrder ADEQUATE_ORDER = new EsparzaAdequateTotalOrderForSafeSystems();
	
	/**
	 * Use techniques to optimize unfodling construction.
	 * 
	 * @assumption The originative system is safe.
	 */
	public boolean SAFE_OPTIMIZATION = true;
}
