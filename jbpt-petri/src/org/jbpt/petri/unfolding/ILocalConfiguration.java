package org.jbpt.petri.unfolding;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;

public interface ILocalConfiguration<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> 
	extends Set<E> {

	public ICut<BPN,C,E,F,N,P,T,M> getCut();

	public M getMarking();

	public List<T> getQuasiParikhVector();

	// TODO cache this
	public List<T> getQuasiParikhVector(Collection<E> events);

	public List<Set<E>> getFoataNormalForm();

	public Integer compareTransitions(T transition1, T transition2);
	
	public void setEvent(E e);
	
	public void setCompletePrefixUnfolding(ICompletePrefixUnfolding<BPN,C,E,F,N,P,T,M> cpf);
	
	public void construct();
}