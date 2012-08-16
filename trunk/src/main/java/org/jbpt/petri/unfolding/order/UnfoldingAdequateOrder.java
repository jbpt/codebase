package org.jbpt.petri.unfolding.order;

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
 * An adequate order which allows to simulate unfolding (maximal branching process).
 *  
 * @author Artem Polyvyanyy
 */
public class UnfoldingAdequateOrder<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>>
	extends AdequateOrder<BPN,C,E,F,N,P,T,M> {

	@Override
	public boolean isSmaller(ILocalConfiguration<BPN,C,E,F,N,P,T,M> lc1, ILocalConfiguration<BPN,C,E,F,N,P,T,M> lc2) {
		return false;
	}
}
