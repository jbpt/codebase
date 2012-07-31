package org.jbpt.petri.unfolding.order;

import java.util.Iterator;
import java.util.Set;

import org.jbpt.petri.unfolding.Event;
import org.jbpt.petri.unfolding.LocalConfiguration;


/**
 * Adequate order (abstract class).
 * 
 * @author Artem Polyvyanyy
 */
public abstract class AdequateOrder implements IAdequateOrder {
	
	@Override
	public Event getMinimal(Set<Event> events) {
		Iterator<Event> i = events.iterator();
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
}
