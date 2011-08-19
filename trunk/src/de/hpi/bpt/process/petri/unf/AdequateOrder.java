package de.hpi.bpt.process.petri.unf;

import java.util.Collection;

public abstract class AdequateOrder {
	
	public Event getMininmal(Unfolding unf, Collection<Event> es) {
		return es.iterator().next();
	}
	
	public boolean isSmaller(LocalConfiguration lc1, LocalConfiguration lc2) {
		return false;
	}
}
