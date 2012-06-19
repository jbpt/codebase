package org.jbpt.petri.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Transition;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.structure.ProcessModel2NetSystem;
import org.jbpt.throwable.TransformationException;


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
	public static boolean areBisimilar(ProcessModel process1, ProcessModel process2) {
		try {
			return areBisimilar(ProcessModel2NetSystem.transform(process1), ProcessModel2NetSystem.transform(process2));
		} catch (TransformationException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Check whether the two NetSystems behave equally, thus the order of observable transitions is similar.
	 * @param net1
	 * @param net2
	 * @return true if both nets behave equally
	 */
	public static boolean areBisimilar(NetSystem net1, NetSystem net2) {
		net1.loadNaturalMarking();
		net2.loadNaturalMarking();
		//Marking marking1 = net1.getMarking();
		//Marking marking2 = net2.getMarking();
		Collection<Transition> enabled1 = net1.getEnabledTransitions();
		Collection<Transition> enabled2 = net2.getEnabledTransitions();
		for (Transition v:enabled1) {
			// check every path on its own
			if (!check(net1, net2, v))
				return false;
		}
		// test also the other way
		for (Transition v:enabled2) {
			if (!check(net2, net1, v))
				return false;
		}
		return true;
	}
	
	/**
	 * Fires the {@link Transition} with the specified label if it exists.
	 * @param {@link NetSystem} net
	 * @param label
	 * @return true if Transition was fired
	 */
	private static boolean fire(NetSystem net, String label) {
		Collection<Transition> enabled = net.getEnabledTransitions();
		for (Transition v:enabled) {
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
	private static boolean find(NetSystem net, String label) {
		// check if the transition is there
		if (fire(net, label))
			return true;
		// otherwise run some unlabeled transitions
		Collection<Transition> unlabeled = getUnlabeledEnabledTransitions(net);
		//Marking marking = net.getMarking();
		for (Transition v:unlabeled) {
			net.fire(v);
			if (find(net, label))
				return true;
		}
		return false;
	}
	
	/**
	 * Fires the given {@link Vertex} v of net1 and checks if net2 contains 
	 * a similar {@link Vertex} transition, which is enabled.
	 * @param net1 - a {@link NetSystem}
	 * @param net2 - a {@link NetSystem}
	 * @param v - the {@link Vertex} to be fired
	 * @return true if both nets behave similar
	 */
	private static boolean check(NetSystem net1, NetSystem net2, Transition v) {
		net1.fire(v);
		//Marking marking1 = net1.getMarking();
		//Marking marking2 = net2.getMarking();
		if (!v.getName().equals("")) {
			// it's a labeled transition
			if (!fire(net2, v.getName()) && !find(net2, v.getName())) {
				// the transition wasn't enabled yet
				// find: trigger some unlabeled transitions and see if the required transition gets enabled
				return false;
			}
			//marking2 = net2.getMarking();
		}
		// run next transition in line
		Collection<Transition> enabled = net1.getEnabledTransitions();
		for (Transition next:enabled) {
			// reset the net for the next run
			//marking1.apply();
			//marking2.apply();
			if (!check(net1, net2, next))
				return false;
		}
		return true;
	}
	
	/**
	 * Returns a set of all unlabeled transitions of the given {@link NetSystem} 
	 * that are currently enabled.
	 * @param net - a {@link NetSystem}
	 * @return set of transitions
	 */
	private static Set<Transition> getUnlabeledEnabledTransitions(NetSystem net) {
		Set<Transition> trans = new HashSet<Transition>();
		for (Transition v:net.getEnabledTransitions()) {
			if (v.getName().equals(""))
				trans.add(v);
		}
		return trans;
	}
}
