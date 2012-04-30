package org.jbpt.petri.structure;

import org.jbpt.graph.algo.ReflexiveTransitiveClosure;
import org.jbpt.petri.Flow;
import org.jbpt.petri.Node;
import org.jbpt.petri.PetriNet;

public class PetriNetPathChecks {
	
	public static boolean hasPath(PetriNet net, Node n1, Node n2) {
		ReflexiveTransitiveClosure<Flow,Node> tc = new ReflexiveTransitiveClosure<Flow,Node>(net);
		return tc.hasPath(n1,n2);
	}

}
