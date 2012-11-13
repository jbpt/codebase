package org.jbpt.petri;

/**
 * State of a net system.
 * 
 * @author Artem Polyvyanyy
 */
public class State extends AbstractState<Flow,Node,Place,Transition,Marking> {
	
	public State() {}
	
	public State(Marking marking) {
		super(marking);
	}
}
