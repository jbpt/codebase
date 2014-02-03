package org.jbpt.petri.untangling.pss;

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
 * An implementation of the {@link IProcessSetSystem} interface.
 * 
 * @author Artem Polyvyanyy
 */
public class ProcessSetSystem extends AbstractProcessSetSystem<BPNode,Condition,Event,Flow,Node,Place,Transition,Marking> {

	public ProcessSetSystem(INetSystem<Flow, Node, Place, Transition, Marking> sys, Collection<IProcess<BPNode, Condition, Event, Flow, Node, Place, Transition, Marking>> pis) {
		super(sys,pis);
	}

}
