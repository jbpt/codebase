package org.jbpt.automaton;

import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;

/**
 * @author Artem Polyvyanyy
 */
public interface IStateTransition<S extends IState<F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> extends IDirectedEdge<S> {
	
	public T getTransition();
	
	public void setTransition(T t);
	
	public String getSymbol();
	
	public void setSymbol(String t);
	
	public boolean isSilent();
	
	public boolean isObservable();

}
