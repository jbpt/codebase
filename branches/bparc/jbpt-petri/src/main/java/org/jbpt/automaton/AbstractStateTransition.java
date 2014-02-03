package org.jbpt.automaton;

import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.graph.abs.AbstractMultiDirectedGraph;
import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;

/**
 * @author Artem Polyvyanyy
 */
public class AbstractStateTransition<S extends IState<F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> extends AbstractDirectedEdge<S> implements
		IStateTransition<S,F,N,P,T,M> {
	private T transition = null;
	private String symbol = null;
	
	@SuppressWarnings("rawtypes")
	public AbstractStateTransition(AbstractMultiDirectedGraph g, S source, S target) {
		super(g, source, target);
	}

	@Override
	public String getSymbol() {
		return this.symbol;
	}

	@Override
	public void setSymbol(String s) {
		this.symbol = s;
	}

	@Override
	public T getTransition() {
		return this.transition;
	}

	@Override
	public void setTransition(T t) {
		if (t==null) return;
			
		this.transition = t;
		this.symbol = t.getLabel();
	}

	@Override
	public boolean isSilent() {
		if (this.getSymbol()==null) return true;
		if (this.getSymbol().isEmpty()) return true;
		
		return false;
	}

	@Override
	public boolean isObservable() {
		return !this.isSilent();
	}
	
	@Override
	public int hashCode() {
		if (this.getSource()==null || this.getTarget()==null)
			return super.hashCode();
			
		int result = 0;
		
		result -= this.getSource().hashCode();
		result += this.getTarget().hashCode();
		
		result -= this.transition==null ? 0 : 7*this.transition.hashCode();
		result += this.symbol==null ? 0 : 11*this.symbol.hashCode();
		
		return result;
	}

}
