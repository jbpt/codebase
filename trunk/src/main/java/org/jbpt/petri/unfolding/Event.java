package org.jbpt.petri.unfolding;

import org.jbpt.petri.Flow;
import org.jbpt.petri.Marking;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;

public class Event extends AbstractEvent<BPNode,Condition,Event,Flow,Node,Place,Transition,Marking> {

	protected Event() {}
	
	public Event(
			ICompletePrefixUnfolding<BPNode, Condition, Event, Flow, Node, Place, Transition, Marking> cpu,
			Transition transition,
			ICoSet<BPNode, Condition, Event, Flow, Node, Place, Transition, Marking> preset) {
		super(cpu, transition, preset);
	}

}
