package org.jbpt.automaton;

import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;

/**
 * @author Artem Polyvyanyy
 */
public class AbstractState<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>>
	extends Vertex 
	implements IState<F,N,P,T,M> {

	private M marking = null;
	
	protected AbstractState() {}
	
	public AbstractState(M marking) {
		this.marking = marking;
	}

	@Override
	public M getMarking() {
		return this.marking;
	}

	@Override
	public void setMarking(M marking) {
		this.marking = marking;
	}
	
	@Override
	public String toString() {
		return this.marking.toString();
	}
	
	@Override
	public String getLabel() {
		return this.toString();
	}

}
