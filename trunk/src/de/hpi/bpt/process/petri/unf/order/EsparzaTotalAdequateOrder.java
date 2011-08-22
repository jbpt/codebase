package de.hpi.bpt.process.petri.unf.order;

import java.util.Collection;

import de.hpi.bpt.process.petri.unf.Event;
import de.hpi.bpt.process.petri.unf.LocalConfiguration;

/**
 * TODO: Esparza adequate order for safe systems
 * 
 * @author Artem Polyvyanyy
 */
public class EsparzaTotalAdequateOrder extends AdequateOrder {

	@Override
	public Event getMininmal(Collection<Event> es) {
		// TODO Auto-generated method stub
		return super.getMininmal(es);
	}

	@Override
	public boolean isSmaller(LocalConfiguration lc1, LocalConfiguration lc2) {
		// TODO Auto-generated method stub
		return super.isSmaller(lc1, lc2);
	}

}
