package de.hpi.bpt.process.petri.unf;

import java.util.Collection;

/**
 * McMillan adequate order
 * 
 * @author Artem Polyvyanyy
 */
public class McMillanAdequateOrder extends AdequateOrder {

	@Override
	public Event getMininmal(Unfolding unf, Collection<Event> es) {
		Event min = es.iterator().next();
		LocalConfiguration lcMin = new LocalConfiguration(unf,min);
		
		for (Event e : es) {
			LocalConfiguration lce = new LocalConfiguration(unf,e);
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
