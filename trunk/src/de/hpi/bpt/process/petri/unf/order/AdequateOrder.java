package de.hpi.bpt.process.petri.unf.order;

import java.util.Collection;

import de.hpi.bpt.process.petri.unf.Event;
import de.hpi.bpt.process.petri.unf.LocalConfiguration;
import de.hpi.bpt.process.petri.unf.Unfolding;

public abstract class AdequateOrder {
	
	public Event getMininmal(Unfolding unf, Collection<Event> es) {
		return es.iterator().next();
	}
	
	public boolean isSmaller(LocalConfiguration lc1, LocalConfiguration lc2) {
		return false;
	}
}
