package org.jbpt.petri.unfolding;

import org.jbpt.petri.Flow;
import org.jbpt.petri.Marking;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;

public class Condition extends AbstractCondition<BPNode,Condition,Event,Flow,Node,Place,Transition,Marking> {

	protected Condition() {}
	
	public Condition(Place place, Event event) {
		super(place, event);
	}

}
