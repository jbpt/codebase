package de.hpi.bpt.process.petri.unf.order.copy;

import java.util.List;

import de.hpi.bpt.process.petri.Transition;
import de.hpi.bpt.process.petri.unf.copy.LocalConfiguration;

/**
 * Esparza adequate order for arbitrary systems
 * 
 * Javier Esparza, Stefan Roemer, Walter Vogler: An Improvement of McMillan's Unfolding Algorithm. Formal Methods in System Design (FMSD) 20(3):285-310 (2002)
 * 
 * @author Artem Polyvyanyy
 */
public class EsparzaAdequateOrderForArbitrarySystems extends AdequateOrder {

	@Override
	public boolean isSmaller(LocalConfiguration lc1, LocalConfiguration lc2) {
		if (lc1.size() < lc2.size()) return true;
		else if (lc1.size() == lc2.size()) {
			List<Transition> pvec1 = lc1.getQuasiParikhVector();
			List<Transition> pvec2 = lc2.getQuasiParikhVector();
			
			for (int i = 0; i<pvec1.size(); i++) {
				Integer comp = lc1.compareTransitions(pvec1.get(i), pvec2.get(i));
				if (comp<0) return true;
				if (comp!=0) return false;
			}
			return false;
		}
		
		return false;
	}
}
