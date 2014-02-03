package org.jbpt.petri.wftree;

import org.jbpt.petri.Flow;
import org.jbpt.petri.IPetriNet;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;

/**
 * This class takes a net and computes its WF-tree.<br/><br/>
 *
 * WF-tree was proposed in:
 * Matthias Weidlich, Artem Polyvyanyy, Jan Mendling, and Mathias Weske.
 * Causal Behavioural Profiles - Efficient Computation, Applications, and Evaluation. 
 * Fundamenta Informaticae (FUIN) 113(3-4): 399-435 (2011)
 * 
 * @author Artem Polyvyanyy
 * @author Matthias Weidlich
 */
public class WFTree extends AbstractWFTree<Flow,Node,Place,Transition> {

	public WFTree(IPetriNet<Flow,Node,Place,Transition> net) {
		super(net);
	}

}
