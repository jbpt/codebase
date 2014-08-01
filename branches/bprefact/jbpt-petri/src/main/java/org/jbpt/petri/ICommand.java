package org.jbpt.petri;

/**
 * Interface to a state change.
 * 
 * @author Artem Polyvyanyy
 */
public interface ICommand<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> {

	public M getInputMarking();
	
	public M getOutputMarking();
	
	public IPetriNet<F,N,P,T> getPetriNet();
	
	public Object getCommand();
}
