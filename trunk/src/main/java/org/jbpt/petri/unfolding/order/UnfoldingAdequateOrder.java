package org.jbpt.petri.unfolding.order;

import org.jbpt.petri.unfolding.LocalConfiguration;

/**
 * An adequate order which allows to simulate unfolding (maximal branching process).
 *  
 * @author Artem Polyvyanyy
 */
public class UnfoldingAdequateOrder extends AdequateOrder {

	@Override
	public boolean isSmaller(LocalConfiguration lc1, LocalConfiguration lc2) {
		return false;
	}
}
