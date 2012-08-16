package org.jbpt.petri.unfolding;

import org.jbpt.petri.Flow;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.Marking;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;

public class CompletePrefixUnfolding extends
		AbstractCompletePrefixUnfolding<BPNode, Condition, Event, Flow, Node, Place, Transition, Marking> {

	public CompletePrefixUnfolding(INetSystem<Flow, Node, Place, Transition, Marking> sys, CompletePrefixUnfoldingSetup setup) {
		super(sys, setup);
	}

	public CompletePrefixUnfolding(INetSystem<Flow, Node, Place, Transition, Marking> sys) {
		super(sys);
	}
	
}
