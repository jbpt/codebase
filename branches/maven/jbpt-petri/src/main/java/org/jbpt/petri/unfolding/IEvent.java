package org.jbpt.petri.unfolding;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;

/**
 * Interface to an event of a branching process.
 * 
 * @author Artem Polyvyanyy
 */
public interface IEvent<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> 
	extends IBPNode<N> {
	/**
	 * Get post conditions of this event.
	 * 
	 * @return Post conditions.
	 */
	public ICoSet<BPN,C,E,F,N,P,T,M> getPostConditions();

	/**
	 * Get the transition that corresponds to this event.
	 * 
	 * @return Corresponding transition.
	 */
	public T getTransition();

	/**
	 * Get preconditions of this event.
	 * 
	 * @return Preconditions.
	 */
	public ICoSet<BPN,C,E,F,N,P,T,M> getPreConditions();
	
	public void setTransition(T transition);
	
	public void setPreConditions(ICoSet<BPN,C,E,F,N,P,T,M> preConditions);
	
	public void setPostConditions(ICoSet<BPN,C,E,F,N,P,T,M> postConditions);
	
	public void setCompletePrefixUnfolding(ICompletePrefixUnfolding<BPN,C,E,F,N,P,T,M> cpf);
	
	public ILocalConfiguration<BPN,C,E,F,N,P,T,M> getLocalConfiguration();
}