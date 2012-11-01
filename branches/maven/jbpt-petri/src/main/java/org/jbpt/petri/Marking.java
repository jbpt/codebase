package org.jbpt.petri;

public class Marking extends AbstractMarking<Flow,Node,Place,Transition> {

	private static final long serialVersionUID = 4936816593533149286L;
	
	public Marking() {
	}
	
	public Marking(IPetriNet<Flow,Node,Place,Transition> net) {
		super(net);
	}

}
