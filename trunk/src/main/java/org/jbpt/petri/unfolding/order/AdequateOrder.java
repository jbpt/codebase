package org.jbpt.petri.unfolding.order;

import java.util.Iterator;
import java.util.Set;

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
 * Adequate order (abstract class).
 * 
 * @author Artem Polyvyanyy
 */
public abstract class AdequateOrder<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> 
	implements IAdequateOrder<BPN,C,E,F,N,P,T,M> {
	
	@Override
	// TODO set must be ordered
	public E getMinimal(Set<E> events) {
		Iterator<E> i = events.iterator();
		E min = i.next();
		if (!i.hasNext()) return min;
		
		ILocalConfiguration<BPN,C,E,F,N,P,T,M> lcMin = min.getLocalConfiguration();
		while (i.hasNext()) {
			E e = i.next();
			ILocalConfiguration<BPN,C,E,F,N,P,T,M> lce = e.getLocalConfiguration();
			if (this.isSmaller(lce,lcMin)) {
				min = e;
				lcMin = lce;
			}
		}
		
		return min;
	}
}
