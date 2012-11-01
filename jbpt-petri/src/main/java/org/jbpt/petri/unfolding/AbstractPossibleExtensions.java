package org.jbpt.petri.unfolding;

import java.util.Iterator;
import java.util.TreeSet;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;
import org.jbpt.petri.unfolding.order.AbstractEventComparator;
import org.jbpt.petri.unfolding.order.IAdequateOrder;

public class AbstractPossibleExtensions<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>>
	extends TreeSet<E>
	implements IPossibleExtensions<BPN,C,E,F,N,P,T,M>
{
	private static final long serialVersionUID = -2255908039950562882L;
	
	private IAdequateOrder<BPN,C,E,F,N,P,T,M> order = null;
	
	public AbstractPossibleExtensions(IAdequateOrder<BPN,C,E,F,N,P,T,M> order) {
		super(new AbstractEventComparator<BPN,C,E,F,N,P,T,M>(order));
		
		this.order = order;
	}

	@Override
	public E getMinimal() {
		if (order.isTotal()) 
			return this.first();
				
		Iterator<E> i = this.iterator();
		E min = i.next();
		if (!i.hasNext()) return min;
		
		ILocalConfiguration<BPN,C,E,F,N,P,T,M> lcMin = min.getLocalConfiguration();
		while (i.hasNext()) {
			E e = i.next();
			ILocalConfiguration<BPN,C,E,F,N,P,T,M> lce = e.getLocalConfiguration();
			if (this.order.isSmaller(lce,lcMin)) {
				min = e;
				lcMin = lce;
			}
		}
		
		return min;
	}
}
