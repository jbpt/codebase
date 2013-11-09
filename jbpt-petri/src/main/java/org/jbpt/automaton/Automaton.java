package org.jbpt.automaton;

import org.jbpt.petri.Flow;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.Marking;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;

public class Automaton extends AbstractAutomaton<StateTransition,State,Flow,Node,Place,Transition,Marking> {

	public Automaton() {
		super();
	}

	public Automaton(INetSystem<Flow, Node, Place, Transition, Marking> sys,int maxSize) {
		super(sys, maxSize);
	}

	public Automaton(INetSystem<Flow,Node,Place,Transition,Marking> sys) {
		super(sys);
	}

}
