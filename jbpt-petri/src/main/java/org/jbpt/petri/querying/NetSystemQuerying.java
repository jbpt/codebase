package org.jbpt.petri.querying;

import java.util.Collection;

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
 * This class can be used to check the presence of structural and/or behavioral information in a net system.  
 *
 * @author Artem Polyvyanyy
 */
public class NetSystemQuerying extends AbstractUntanglingBasedBehavioralQuerying<BPNode,Condition,Event,Flow,Node,Place,Transition,Marking> {

	public NetSystemQuerying(INetSystem<Flow,Node,Place,Transition,Marking> sys) {
		super(sys);
	}
	
	public NetSystemQuerying(INetSystem<Flow,Node,Place,Transition,Marking> sys,
			Collection<IProcess<BPNode,Condition,Event,Flow,Node,Place,Transition,Marking>> untangling) {
		super(sys, untangling);
	}

}
