package org.jbpt.petri;

/**
 * Step of a Petri net.
 * 
 * @author Artem Polyvyanyy
 */
public class Step extends AbstractStep<Flow,Node,Place,Transition,Marking> {

	protected Step(IPetriNet<Flow, Node, Place, Transition> net, Marking inputMarking, Transition transition, Marking outputMarking) {
		super(net, inputMarking, transition, outputMarking);
	}

	protected Step(IPetriNet<Flow, Node, Place, Transition> net, Marking inputMarking, Transition transition) {
		super(net, inputMarking, transition);
	}

}
