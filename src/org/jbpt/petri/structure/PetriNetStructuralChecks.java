package org.jbpt.petri.structure;

import java.util.HashSet;
import java.util.Set;

import org.jbpt.graph.algo.TransitiveClosure;
import org.jbpt.petri.Flow;
import org.jbpt.petri.Node;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;

/**
 * Petri net structural checks
 * 
 * @author Artem Polyvyanyy
 */
public class PetriNetStructuralChecks {
	
	/**
	 * Check if Petri net is free-choice.
	 * 
	 * @param net Petri net
	 * @return <code>true</code> if net is free-choice; <code>false</code> otherwise
	 */
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
	 * Checks if Petri net is extended free-choice. 
	 * A Petri net is extended free-choice if all transitions that share a place in their presets have to coincide w.r.t. their presets.
	 * 
	 * @param net Petri net
	 * @return <code>true</code> if net is extended free-choice; <code>false</code> otherwise
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
	 * Checks if Petri net is S-net.
	 * S-net has no conflict places (neither forward, nor backward).
	 * 
	 * @param net Petri net
	 * @return <code>true</code> if net is S-net; <code>false</code> otherwise
	 */
	public static boolean isSNet(PetriNet net) {
		boolean result = true;
		for (Place p : net.getPlaces())
			result &= (net.getPreset(p).size()<=1) && (net.getPostset(p).size()<=1);
		return result;	
	}
	
	/**
	 * Checks if Petri net is T-net.
	 * S-net has no branching transitions (neither forward, nor backward).
	 * 
	 * @param net Petri net
	 * @return <code>true</code> if net is T-net; <code>false</code> otherwise
	 */
	public static boolean isTNet(PetriNet net) {
		boolean result = true;
		for (Transition t : net.getTransitions())
			result &= (net.getPreset(t).size()<=1) && (net.getPostset(t).size()<=1);
		return result;	
	}
	
	/**
	 * Checks if Petri net is workflow net. 
	 * 
	 * Workflow net has exactly one source and exactly one sink place.
	 * Moreover, every node is on a path from the source to the sink.
	 * 
	 * @param net Petri net
	 * @return <code>true</code> if net is workflow net; <code>false</code> otherwise
	 */
	public static boolean isWorkflowNet(PetriNet net) {
		TransitiveClosure<Flow,Node> tc = new TransitiveClosure<Flow,Node>(net);
		
		if (net.getSourcePlaces().size() != 1) return false;
		if (net.getSinkPlaces().size() != 1) return false;
		
		Node source = net.getSourcePlaces().iterator().next();
		Node sink = net.getSinkPlaces().iterator().next();
		
		for (Node n : net.getNodes()) {
			if (n.equals(source) || n.equals(sink)) continue;
			if (!tc.hasPath(source,n)) return false;
			if (!tc.hasPath(n,sink)) return false;
		}
		
		return true;
	}

}
