package org.jbpt.petri.structure;

import java.util.Map;
import java.util.Set;

import org.jbpt.graph.algo.ReflexiveTransitiveClosure;
import org.jbpt.graph.algo.TransitiveClosure;
import org.jbpt.petri.Flow;
import org.jbpt.petri.Node;
import org.jbpt.petri.PetriNet;

public class PetriNetPathUtils {

	/**
	 * Check if net has a path
	 * @param net Petri net
	 * @param n1 Node
	 * @param n2 Node
	 * @return  <code>true</code> if the net has a directed path from n1 to n2; <code>false</code> otherwise
	 */
	public static boolean hasDirectedPath(PetriNet net, Node n1, Node n2) {
		ReflexiveTransitiveClosure<Flow,Node> tc = new ReflexiveTransitiveClosure<Flow,Node>(net);
		return tc.hasPath(n1,n2);
	}
	
	/**
	 * Check if net is cyclic
	 * @param net Petri net
	 * @return  <code>true</code> if the net is cyclic; <code>false</code> otherwise
	 */
	public static boolean isCyclic(PetriNet net) {
		TransitiveClosure<Flow,Node> tc = new TransitiveClosure<Flow,Node>(net);
		for (Node n : net.getNodes())
			if (tc.isInLoop(n))
				return true;
			
		return false;
	}
	
	
	public static Map<Node, Set<Node>> getDominators(PetriNet net) {
		return PetriNet.DGA.getDominators(net, false);
	}
	
	public static Map<Node, Set<Node>> getPostDominators(PetriNet net) {
		return PetriNet.DGA.getDominators(net, true);
	}
}
