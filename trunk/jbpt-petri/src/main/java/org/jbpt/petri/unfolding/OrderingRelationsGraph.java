package org.jbpt.petri.unfolding;

import org.jbpt.petri.Flow;
import org.jbpt.petri.Marking;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;

/**
 * Implementation of the ordering relations graph (ORGRAPH).
 *
 * @author Artem Polyvyanyy
 *
 */
public class OrderingRelationsGraph extends
		AbstractOrderingRelationsGraph<BPNode,Condition,Event,Flow,Node,Place,Transition,Marking> {

	protected OrderingRelationsGraph() {
		super();
	}

	public OrderingRelationsGraph(IBranchingProcess<BPNode,Condition,Event,Flow,Node,Place,Transition,Marking> bp) {
		super(bp);
	}

}
