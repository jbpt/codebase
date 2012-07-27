package org.jbpt.petri.unfolding.order;

import java.util.Set;

import org.jbpt.petri.unfolding.Event;
import org.jbpt.petri.unfolding.LocalConfiguration;

/**
 * Interface to an adequate order on local configurations.
 *
 * @author Artem Polyvyanyy
 */
public interface IAdequateOrder {
	
	/**
	 * Get a minimal event from the set of events.
	 * 
	 * @param events Set of events.
	 * @return A minimal event from the set of events.
	 */	
	public Event getMinimal(Set<Event> events);
	
	/**
	 * Compare two local configurations.
	 * 
	 * @param lc1 A local configuration.
	 * @param lc2 A local configuration.
	 * @return <tt>true</tt> if 'lc1' is smaller than 'lc2'; otherwise <tt>false</tt>.
	 */
	public boolean isSmaller(LocalConfiguration lc1, LocalConfiguration lc2);
}
