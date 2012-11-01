package org.jbpt.petri.unfolding;

import org.jbpt.petri.Flow;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.Marking;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;

public class ProperCompletePrefixUnfolding 
		extends AbstractProperCompletePrefixUnfolding<BPNode,Condition,Event,Flow,Node,Place,Transition,Marking> {

	public ProperCompletePrefixUnfolding() {
		super();
	}

	public ProperCompletePrefixUnfolding(
			INetSystem<Flow, Node, Place, Transition, Marking> sys,
			CompletePrefixUnfoldingSetup setup) {
		super(sys, setup);
	}

	public ProperCompletePrefixUnfolding(
			INetSystem<Flow, Node, Place, Transition, Marking> sys) {
		super(sys);
	}

}
