package org.jbpt.petri.unfolding.order;

import org.jbpt.petri.unfolding.LocalConfiguration;

/**
 * McMillan adequate order
 * 
 * @author Artem Polyvyanyy
 */
public class McMillanAdequateOrder extends AdequateOrder {

	@Override
	public boolean isSmaller(LocalConfiguration lc1, LocalConfiguration lc2) {
		return lc1.size() < lc2.size();
	}
}
