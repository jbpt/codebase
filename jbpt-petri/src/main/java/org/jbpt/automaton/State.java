package org.jbpt.automaton;

import org.jbpt.petri.Flow;
import org.jbpt.petri.Marking;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;

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
