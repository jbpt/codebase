package org.jbpt.petri2;

import java.util.HashSet;
import java.util.Set;

public class PNAnalyzer {
	
	public static boolean isFreeChoice(PetriNet net) {
		for (Place p : net.getPlaces()) {
			if (net.getPostset(p).size()>1) {
				Set<Place> z = new HashSet<Place>();
				for (Transition t : net.getPostset(p)) {
					z.addAll(net.getPreset(t));
				}
				if (z.size()>1) return false;
			}
		}
		
		return true;
	}
		
	/**
	 * Checks whether the given Petri net is extended free-choice. That is,
	 * all transitions that share a place in their presets have to coincide
	 * w.r.t. their presets
	 * 
	 * @param a Petri net
	 * @return true, if the given Petri net is extended free-choice
	 */
	public static boolean isExtendedFreeChoice(PetriNet net) {
		boolean isFC = true;
		
		outer:
		for (Transition t1 : net.getTransitions()) {
			for (Transition t2 : net.getTransitions()) {
				for (Place p : net.getPlaces()) {
					if (net.getDirectPredecessors(t1).contains(p) && net.getDirectPredecessors(t2).contains(p))
						isFC &= net.getPreset(t1).equals(net.getPreset(t2));
					if (!isFC) 
						break outer;
				}
			}
		}
		return isFC;
	}
	
	/**
	 * Checks whether the given Petri net is a workflow net. Such a net has
	 * exactly one initial and one final place and every place and 
	 * transition is one a path from i to o.
	 * 
	 * @return true, if the net is a workflow net
	 */
	// TODO
	/*public static boolean isWorkflowNet(PetriNet net) {
		boolean isWF = (net.getSourcePlaces().size() == 1) && (net.getSinkPlaces().size() == 1);
		// maybe we already know that the net is not a workflow net
		if (!isWF)
			return isWF;
		
		Node in = net.getSourcePlaces().iterator().next();
		Node out = net.getSinkPlaces().iterator().next();
		for (Node n : net.getNodes()) {
			if (n.equals(in) || n.equals(out))
				continue;
			isWF &= net.hasPath(in, n);
			isWF &= net.hasPath(n, out);
		}
		return isWF;
	}*/

	/**
	 * Checks whether the given Petri net is an S-net.
	 * 
	 * @return true, if net is an S-net.
	 */
	public static boolean isSNet(PetriNet net) {
		boolean result = true;
		for (Transition t : net.getTransitions())
			result &= (net.getIncomingEdges(t).size() == 1) && ((net.getOutgoingEdges(t).size() == 1));
		return result;	
	}

	/**
	 * Checks whether the given Petri net is a T-net.
	 * 
	 * @return true, if net is a T-net.
	 */
	public static boolean isTNet(PetriNet net) {
		boolean result = true;
		for (Place p : net.getPlaces())
			result &= (net.getIncomingEdges(p).size() == 1) && ((net.getOutgoingEdges(p).size() == 1));
		return result;	
	}

}
