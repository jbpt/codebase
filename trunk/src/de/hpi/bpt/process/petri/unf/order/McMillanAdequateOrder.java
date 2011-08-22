package de.hpi.bpt.process.petri.unf.order;

import java.util.Collection;

import de.hpi.bpt.process.petri.unf.Event;
import de.hpi.bpt.process.petri.unf.LocalConfiguration;

/**
 * McMillan adequate order
 * 
 * @author Artem Polyvyanyy
 */
public class McMillanAdequateOrder extends AdequateOrder {

	@Override
	public Event getMininmal(Collection<Event> es) {
		Event min = es.iterator().next();
		LocalConfiguration lcMin = min.getLocalConfiguration();
		
		for (Event e : es) {
			LocalConfiguration lce = e.getLocalConfiguration();
			if (lce.size() < lcMin.size()) {
				min = e;
				lcMin = lce;
			}
		}
		
		return min; 
	}

	@Override
	public boolean isSmaller(LocalConfiguration lc1, LocalConfiguration lc2) {
		return lc1.size() < lc2.size();
	}
}
