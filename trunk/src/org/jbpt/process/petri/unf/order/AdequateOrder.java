package de.hpi.bpt.process.petri.unf.order;

import java.util.Collection;
import java.util.Iterator;

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
	 * @return minimal event (according to the adequate order) from the collection of events
	 */	
	public Event getMinimal(Collection<Event> es) {
		Iterator<Event> i = es.iterator();
		Event min = i.next();
		if (!i.hasNext()) return min;
		
		LocalConfiguration lcMin = min.getLocalConfiguration();
		while (i.hasNext()) {
			Event e = i.next();
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
