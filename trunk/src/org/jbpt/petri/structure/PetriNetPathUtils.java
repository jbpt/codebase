package org.jbpt.petri.structure;

import java.util.Map;
import java.util.Set;

import org.jbpt.algo.graph.TransitiveClosure;
import org.jbpt.petri.IFlow;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPetriNet;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;
import org.jbpt.petri.PetriNet;

public class PetriNetPathUtils {

	/**
	 * Check if net has a path
	 * @param net Petri net
	 * @param n1 Node
	 * @param n2 Node
	 * @return  <code>true</code> if the net has a directed non-empty path from n1 to n2; <code>false</code> otherwise
	 */
	public static <N extends IPetriNet<IFlow<INode>,INode,IPlace,ITransition>> boolean hasDirectedNonEmptyPath(N net, INode n1, INode n2) {
		TransitiveClosure<IFlow<INode>,INode> tc = new TransitiveClosure<>(net);
		return tc.hasPath(n1,n2);
	}
	
	/**
	 * Check if net is cyclic
	 * @param net Petri net
	 * @return  <code>true</code> if the net is cyclic; <code>false</code> otherwise
	 */
	public static <N extends IPetriNet<IFlow<INode>,INode,IPlace,ITransition>> boolean isCyclic(N net) {
		TransitiveClosure<IFlow<INode>,INode> tc = new TransitiveClosure<>(net);
		for (INode n : net.getNodes())
			if (tc.isInLoop(n))
				return true;
			
		return false;
	}
	
	
	public static Map<INode, Set<INode>> getDominators(IPetriNet<IFlow<INode>, INode, IPlace, ITransition> net) {
		return PetriNet.DGA.getDominators(net, false);
	}
	
	public static Map<INode, Set<INode>> getPostDominators(IPetriNet<IFlow<INode>, INode, IPlace, ITransition> net) {
		return PetriNet.DGA.getDominators(net, true);
	}
}
