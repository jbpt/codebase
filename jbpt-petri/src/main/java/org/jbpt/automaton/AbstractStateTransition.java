package org.jbpt.automaton;

import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.graph.abs.AbstractMultiDirectedGraph;
import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;

public class AbstractStateTransition<S extends IState<F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> extends AbstractDirectedEdge<S> implements
		IStateTransition<S,F,N,P,T,M> {

	T symbol = null;
	
	@SuppressWarnings("rawtypes")
	public AbstractStateTransition(AbstractMultiDirectedGraph g, S source, S target) {
		super(g, source, target);
	}

	@Override
	public T getSymbol() {
		return this.symbol;
	}

	@Override
	public void setSymbol(T t) {
		this.symbol = t;
	}

}
