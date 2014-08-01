package org.jbpt.automaton;

import org.jbpt.graph.abs.AbstractMultiDirectedGraph;
import org.jbpt.petri.Flow;
import org.jbpt.petri.Marking;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;

/**
 * @author Artem Polyvyanyy
 */
public class StateTransition extends AbstractStateTransition<State,Flow,Node,Place,Transition,Marking> {

	@SuppressWarnings("rawtypes")
	public StateTransition(AbstractMultiDirectedGraph g, State source, State target) {
		super(g, source, target);
	}

}
