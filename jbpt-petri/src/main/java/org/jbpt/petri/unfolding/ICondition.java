package org.jbpt.petri.unfolding;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;

/**
 * Interface to a condition of a branching process.
 * 
 * @author Artem Polyvyanyy
 */
public interface ICondition<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> 
	extends IBPNode<N> {

	/**
	 * Get the place in the originative net system which this condition represents.
	 * 
	 * @return Corresponding place in the originative net system.
	 */
	public P getPlace();

	/**
	 * Get the event in the preset of this condition.
	 * 
	 * @return Event in the preset of this condition.
	 */
	public E getPreEvent();
	
	public void setPlace(P place);
	
	public void setPreEvent(E event);
}