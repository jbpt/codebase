package org.jbpt.petri;

import org.jbpt.graph.abs.AbstractDirectedEdge;

/**
 * State space of a net system.
 * 
 * @author Artem Polyvyanyy
 */
public class StateSpace extends AbstractStateSpace<AbstractDirectedEdge<State>,State,Flow,Node,Place,Transition,Marking> {

	public StateSpace(INetSystem<Flow,Node,Place,Transition,Marking> sys, int maxSize) {
		super(sys, maxSize);
	}

	public StateSpace(INetSystem<Flow,Node,Place,Transition,Marking> sys) {
		super(sys);
	}

}
