package de.hpi.bpt.process.petri.util;

import java.util.HashSet;
import java.util.Set;

import de.hpi.bpt.hypergraph.abs.Vertex;
import de.hpi.bpt.process.Process;
import de.hpi.bpt.process.petri.Marking;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Transition;

/**
 * This class implements a check for Full Concurrent Bisimilarity.
 * It simulates one process and checks whether the second process 
 * can perform the same transitions and vice versa.
 * @author Christian Wiggert
 *
 */
public class BisimilarityChecker {

	/**
	 * Check whether the two Processes behave equally, thus the order of observable transitions is similar.
	 * @param process1
	 * @param process2
	 * @return
	 */
	public static boolean areBisimilar(Process process1, Process process2) {
		try {
			return areBisimilar(Process2PetriNet.convert(process1), Process2PetriNet.convert(process2));
		} catch (TransformationException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Check whether the two PetriNets behave equally, thus the order of observable transitions is similar.
	 * @param net1
	 * @param net2
	 * @return true if both nets behave equally
	 */
	public static boolean areBisimilar(PetriNet net1, PetriNet net2) {
		net1.setNaturalInitialMarking();
		net2.setNaturalInitialMarking();
		Marking marking1 = net1.getMarking();
		Marking marking2 = net2.getMarking();
		Set<Vertex> enabled1 = net1.getEnabledElements();
		Set<Vertex> enabled2 = net2.getEnabledElements();
		for (Vertex v:enabled1) {
			marking1.apply();
			marking2.apply();
			// check every path on its own
			if (!check(net1, net2, v))
				return false;
		}
		// test also the other way
		for (Vertex v:enabled2) {
			marking1.apply();
			marking2.apply();
			if (!check(net2, net1, v))
				return false;
		}
		return true;
	}
	
	/**
	 * Fires the {@link Transition} with the specified label if it exists.
	 * @param {@link PetriNet} net
	 * @param label
	 * @return true if Transition was fired
	 */
	private static boolean fire(PetriNet net, String label) {
		Set<Vertex> enabled = net.getEnabledElements();
		for (Vertex v:enabled) {
			if (!v.getName().equals("")) {
				// it is no unlabeled transition
				if (v.getName().equals(label)) {
					// we found the transition, we were looking for
					net.fire(v);
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Try to find and fire the transition with the given label.
	 * While searching the transition, fire as much unlabeled transitions as necessary.
	 * @param net
	 * @param label of the transition
	 * @return true if transition was found and fired
	 */
	private static boolean find(PetriNet net, String label) {
		// check if the transition is there
		if (fire(net, label))
			return true;
		// otherwise run some unlabeled transitions
		Set<Vertex> unlabeled = getUnlabeledEnabledTransitions(net);
		Marking marking = net.getMarking();
		for (Vertex v:unlabeled) {
			marking.apply();
			net.fire(v);
			if (find(net, label))
				return true;
		}
		return false;
	}
	
	/**
	 * Fires the given {@link Vertex} v of net1 and checks if net2 contains 
	 * a similar {@link Vertex} transition, which is enabled.
	 * @param net1 - a {@link PetriNet}
	 * @param net2 - a {@link PetriNet}
	 * @param v - the {@link Vertex} to be fired
	 * @return true if both nets behave similar
	 */
	private static boolean check(PetriNet net1, PetriNet net2, Vertex v) {
		net1.fire(v);
		Marking marking1 = net1.getMarking();
		Marking marking2 = net2.getMarking();
		if (!v.getName().equals("")) {
			// it's a labeled transition
			if (!fire(net2, v.getName()) && !find(net2, v.getName())) {
				// the transition wasn't enabled yet
				// find: trigger some unlabeled transitions and see if the required transition gets enabled
				return false;
			}
			marking2 = net2.getMarking();
		}
		// run next transition in line
		Set<Vertex> enabled = net1.getEnabledElements();
		for (Vertex next:enabled) {
			// reset the net for the next run
			marking1.apply();
			marking2.apply();
			if (!check(net1, net2, next))
				return false;
		}
		return true;
	}
	
	/**
	 * Returns a set of all unlabeled transitions of the given {@link PetriNet} 
	 * that are currently enabled.
	 * @param net - a {@link PetriNet}
	 * @return set of transitions
	 */
	private static Set<Vertex> getUnlabeledEnabledTransitions(PetriNet net) {
		Set<Vertex> trans = new HashSet<Vertex>();
		for (Vertex v:net.getEnabledElements()) {
			if (v.getName().equals(""))
				trans.add(v);
		}
		return trans;
	}
}
