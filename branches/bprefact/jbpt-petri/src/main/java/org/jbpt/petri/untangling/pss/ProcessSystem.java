package org.jbpt.petri.untangling.pss;

import org.jbpt.petri.Flow;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.Marking;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.petri.unfolding.BPNode;
import org.jbpt.petri.unfolding.Condition;
import org.jbpt.petri.unfolding.Event;
import org.jbpt.petri.untangling.IProcess;

/**
 * An implementation of the {@link IProcessSystem} interface.
 * 
 * @author Artem Polyvyanyy
 */
public class ProcessSystem 
		extends AbstractProcessSystem<BPNode,Condition,Event,Flow,Node,Place,Transition,Marking> {

	protected ProcessSystem() {
		super();
	}
			
	public ProcessSystem(INetSystem<Flow,Node,Place,Transition,Marking> sys, IProcess<BPNode,Condition,Event,Flow,Node,Place,Transition,Marking> pi) {
		super(sys,pi);
	}

}
