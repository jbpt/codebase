package de.hpi.bpt.process.petri.util;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hpi.bpt.hypergraph.abs.Vertex;
import de.hpi.bpt.process.ProcessModel;
import de.hpi.bpt.process.petri.Marking;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Transition;

/**
 * This class implements a check for Full Concurrent Bisimilarity.
 * It compares the state transition relations of both state spaces.
 * @author Christian Wiggert
 *
 */
public class BisimilarityChecker2 {

	private PetriNet net1, net2;
	private boolean areBisimilar = false;
	private Map<String, BitSet> nameMap;
	private int n;
	
	public BisimilarityChecker2(ProcessModel process1, ProcessModel process2) throws TransformationException {
		this(Process2PetriNet.convert(process1), Process2PetriNet.convert(process2));
	}
	
	public BisimilarityChecker2(PetriNet net1, PetriNet net2) {
		this.net1 = net1;
		this.net2 = net2;
		this.compare();
	}
	
	private List<String> getNames(PetriNet net) {
		ArrayList<String> names = new ArrayList<String>();
		for (Transition t:net.getTransitions()) {
			if (!t.getName().equals(""))
				names.add(t.getName());
		}
		return names;
	}
	
	/**
	 * Creates the lookup map for all the labels, to receive
	 * the according BitSet.
	 * @param names - list of all labels
	 */
	private void createNameMap(List<String> names) {
		n = names.size();
		nameMap = new HashMap<String, BitSet>();
		for (int i = 0; i < n; i++) {
			BitSet bits = new BitSet(n);
			bits.set(i);
			nameMap.put(names.get(i), bits);
		}
	}
	
	/**
	 * Runs a DFS to create the state transition relation for a given {@link PetriNet}.
	 * @param net - the petri net
	 * @param trans - all yet found transitions
	 * @param currentState
	 */
	private void dfs(PetriNet net, Map<BitSet, Set<BitSet>> trans, BitSet currentState) {
		Marking marking = net.getMarking();
		Set<Vertex> enabled = net.getEnabledElements();
		for (Vertex v:enabled) {
			marking.apply();
			BitSet nextState = null;
			if (!v.getName().equals("")) {
				// we are just interested in labeled transitions...
				nextState = (BitSet) currentState.clone();
				nextState.or(nameMap.get(v.getName()));
				if (trans.containsKey(currentState))
					trans.get(currentState).add(nextState);
				else {
					HashSet<BitSet> sets = new HashSet<BitSet>();
					sets.add(nextState);
					trans.put(currentState, sets);
				}
			} else {
				// ...therefore we don't change the state if the current 
				// transition is unlabeled
				nextState = currentState;
			}
			net.fire(v);
			dfs(net, trans, nextState);
		}
	}
	
	/**
	 * Prepare to run the DFS.
	 * @param net
	 * @return the state transition relation
	 */
	private Map<BitSet, Set<BitSet>> createStateTransitions(PetriNet net) {
		Map<BitSet, Set<BitSet>> transitions = new HashMap<BitSet, Set<BitSet>>();
		net.setNaturalInitialMarking();
		dfs(net, transitions, new BitSet(n));
		return transitions;
	}
	
	/**
	 * Compares both given relations for equality.
	 * @param trans1
	 * @param trans2
	 * @return
	 */
	private boolean compareStateTransitions(Map<BitSet, Set<BitSet>> trans1, Map<BitSet, Set<BitSet>> trans2) {
		// there might be different sets of reachable states
		if (!trans1.keySet().containsAll(trans2.keySet()) || 
			!trans2.keySet().containsAll(trans1.keySet()))
			return false;
		// compare every single relation 
		for (BitSet bits:trans1.keySet()) {
			if (!trans1.get(bits).containsAll(trans2.get(bits)) ||
				!trans2.get(bits).containsAll(trans1.get(bits)))
				return false;
		}
		return true;
	}
	
	/**
	 * Runs the whole process of comparison.
	 */
	private void compare() {
		List<String> names1 = getNames(net1);
		List<String> names2 = getNames(net2);
		if (!names1.containsAll(names2) || !names2.containsAll(names1))
			areBisimilar = false;
		else {
			createNameMap(names1);
			Map<BitSet, Set<BitSet>> trans1 = createStateTransitions(net1);
			Map<BitSet, Set<BitSet>> trans2 = createStateTransitions(net2);
			areBisimilar = compareStateTransitions(trans1, trans2);
		}
	}
	
	public boolean areBisimilar() {
		return areBisimilar;
	}
}
