package org.jbpt.petri.unfolding.order;

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
 * Interface to an adequate order on local configurations.
 *
 * @author Artem Polyvyanyy
 */
public interface IAdequateOrder<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> {
	
	/**
	 * Get a minimal event from the set of events.
	 * 
	 * @param events Set of events.
	 * @return A minimal event from the set of events.
	 */	
	public E getMinimal(Set<E> events);
	
	/**
	 * Compare two local configurations.
	 * 
	 * @param lc1 A local configuration.
	 * @param lc2 A local configuration.
	 * @return <tt>true</tt> if 'lc1' is smaller than 'lc2'; otherwise <tt>false</tt>.
	 */
	public boolean isSmaller(ILocalConfiguration<BPN,C,E,F,N,P,T,M> lc1, ILocalConfiguration<BPN,C,E,F,N,P,T,M> lc2);
}
