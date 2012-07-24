package org.jbpt.petri.structure;

import java.util.HashSet;
import java.util.Set;

import org.jbpt.algo.graph.DirectedGraphAlgorithms;
import org.jbpt.petri.IFlow;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPetriNet;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;

/**
 * Petri net structural class checks.
 * 
 * @author Artem Polyvyanyy
 * @author Matthias Weidlich
 */
public class PetriNetStructuralClassChecks {
	
	/**
	 * Check if Petri net is free-choice.
	 * 
	 * @param net Petri net
	 * @return <code>true</code> if net is free-choice; <code>false</code> otherwise
	 */
	public static <N extends IPetriNet<IFlow<INode>,INode,IPlace,ITransition>> boolean isFreeChoice(N net) {
		for (IPlace p : net.getPlaces()) {
			if (net.getPostset(p).size()>1) {
				Set<IPlace> z = new HashSet<>();
				for (ITransition t : net.getPostset(p)) {
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
	public static <N extends IPetriNet<IFlow<INode>,INode,IPlace,ITransition>> boolean isExtendedFreeChoice(N net) {
		boolean isFC = true;
		
		outer:
		for (ITransition t1 : net.getTransitions()) {
			for (ITransition t2 : net.getTransitions()) {
				for (IPlace p : net.getPlaces()) {
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
	public static <N extends IPetriNet<IFlow<INode>,INode,IPlace,ITransition>> boolean isSNet(N net) {
		boolean result = true;
		for (IPlace p : net.getPlaces())
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
	public static <N extends IPetriNet<IFlow<INode>,INode,IPlace,ITransition>> boolean isTNet(N net) {
		boolean result = true;
		for (ITransition t : net.getTransitions())
			result &= (net.getPreset(t).size()<=1) && (net.getPostset(t).size()<=1);
		return result;	
	}

	/**
	 * Test if Petri net is a workflow net. 
	 * 
	 * Workflow net has exactly one source and exactly one sink place. 
	 * Moreover, every node is on a path from the source to the sink. 
	 * 
	 * @param net Petri net
	 * @return <code>true</code> if the net is a workflow net; <code>false</code> otherwise. 
	 */
	public static <N extends IPetriNet<IFlow<INode>,INode,IPlace,ITransition>> boolean isWorkflowNet(N net) {
		if (net==null) return false;
		DirectedGraphAlgorithms<IFlow<INode>,INode> dga = new DirectedGraphAlgorithms<>();
		return dga.isTwoTerminal(net);
	}

}
