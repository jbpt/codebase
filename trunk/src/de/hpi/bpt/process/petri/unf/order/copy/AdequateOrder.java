package de.hpi.bpt.process.petri.unf.order.copy;

import java.util.Collection;

import de.hpi.bpt.process.petri.unf.copy.Event;
import de.hpi.bpt.process.petri.unf.copy.LocalConfiguration;

/**
 * Adequate order (abstract class)
 * 
 * @author Artem Polyvyanyy
 */
public abstract class AdequateOrder {
	
	/**
	 * Get minimal event
	 * @param es events
	 * @return minimal event (according to the adequate order) from the collection of events
	 */
	public Event getMininmal(Collection<Event> es) {
		Event min = es.iterator().next();
		LocalConfiguration lcMin = min.getLocalConfiguration();
		
		for (Event e : es) {
			LocalConfiguration lce = e.getLocalConfiguration();
			if (this.isSmaller(lce,lcMin)) {
				min = e;
				lcMin = lce;
			}
		}
		
		return min;
	}
	
	/**
	 * Compare local configurations
	 * @param lc1 local configuration
	 * @param lc2 local configurations
	 * @return true if lc1 is smaller than lc2; otherwise false
	 */
	public boolean isSmaller(LocalConfiguration lc1, LocalConfiguration lc2) {
		return false;
	}
}
