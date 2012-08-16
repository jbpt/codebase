package org.jbpt.petri.unfolding.order;

import java.util.List;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;
import org.jbpt.petri.unfolding.IBPNode;
import org.jbpt.petri.unfolding.ICondition;
import org.jbpt.petri.unfolding.IEvent;
import org.jbpt.petri.unfolding.ILocalConfiguration;


/**
 * Esparza adequate order for arbitrary systems.<br/><br/>
 * 
 * This adequate order is described in:<br/>
 * Javier Esparza, Stefan Roemer, Walter Vogler: An Improvement of McMillan's Unfolding Algorithm. Formal Methods in System Design (FMSD) 20(3):285-310 (2002).
 * 
 * @author Artem Polyvyanyy
 */
public class EsparzaAdequateOrderForArbitrarySystems<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> 
	extends AdequateOrder<BPN,C,E,F,N,P,T,M> {

	@Override
	public boolean isSmaller(ILocalConfiguration<BPN,C,E,F,N,P,T,M> lc1, ILocalConfiguration<BPN,C,E,F,N,P,T,M> lc2) {
		if (lc1.size() < lc2.size()) return true;
		else if (lc1.size() == lc2.size()) {
			List<T> pvec1 = lc1.getQuasiParikhVector();
			List<T> pvec2 = lc2.getQuasiParikhVector();
			
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
