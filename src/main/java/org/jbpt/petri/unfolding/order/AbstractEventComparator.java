package org.jbpt.petri.unfolding.order;

import java.util.Comparator;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;
import org.jbpt.petri.unfolding.IBPNode;
import org.jbpt.petri.unfolding.ICondition;
import org.jbpt.petri.unfolding.IEvent;

public class AbstractEventComparator<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>>
		implements Comparator<E> 
{	
	private IAdequateOrder<BPN,C,E,F,N,P,T,M> order = null;

	public AbstractEventComparator(IAdequateOrder<BPN,C,E,F,N,P,T,M> order) {
		this.order = order;
	}
	
	@Override
	public int compare(E e1, E e2) {
		if (e1==e2) return 0;
		if (e1.equals(e2)) return 0;
		
		if (order.isSmaller(e1.getLocalConfiguration(), e2.getLocalConfiguration()))
			return -1;
		
		if (order.isSmaller(e2.getLocalConfiguration(), e1.getLocalConfiguration()))
			return 1;
		
		return 0;
	}

}
