package org.jbpt.petri.untangling;

import org.jbpt.petri.Flow;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.Marking;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;

public class UntanglingRun extends AbstractUntanglingRun<Flow,Node,Place,Transition,Marking> {
	private static final long serialVersionUID = 2583533913166868367L;

	public UntanglingRun() {
		super();
	}

	public UntanglingRun(INetSystem<Flow,Node,Place,Transition,Marking> sys) {
		super(sys);
	}
}
