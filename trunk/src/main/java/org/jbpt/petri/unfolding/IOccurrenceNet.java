package org.jbpt.petri.unfolding;

import java.util.Set;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPetriNet;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;

public interface IOccurrenceNet<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> 
	extends IPetriNet<F,N,P,T>
{

	public ICompletePrefixUnfolding<BPN,C,E,F,N,P,T,M> getCompletePrefixUnfolding();

	public E getEvent(T t);

	public C getCondition(P p);

	public P getPlace(C c);

	public T getTransition(E e);

	public BPN getUnfoldingNode(N n);

	public OrderingRelationType getOrderingRelation(N n1, N n2);

	public Set<T> getCutoffEvents();

	public T getCorrespondingEvent(T t);

	public boolean isCutoffEvent(T t);
	
	public Set<P> getCutInducedByLocalConfiguration(T t);
	
	public void setCompletePrefixUnfolding(ICompletePrefixUnfolding<BPN,C,E,F,N,P,T,M> cpu);
	
	public void setBranchingProcess(IBranchingProcess<BPN,C,E,F,N,P,T,M> bp);

}