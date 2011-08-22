package de.hpi.bpt.process.petri.unf.order;

import java.util.Collection;

import de.hpi.bpt.process.petri.unf.Event;
import de.hpi.bpt.process.petri.unf.LocalConfiguration;

/**
 * Adequate order (abstract class)
 * 
 * @author Artem Polyvyanyy
 */
public abstract class AdequateOrder {
	
	/**
	 * Get minimal event
	 * @param es events
	 * @return minimal event from the collection of events
	 */
	public Event getMininmal(Collection<Event> es) {
		return es.iterator().next();
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
