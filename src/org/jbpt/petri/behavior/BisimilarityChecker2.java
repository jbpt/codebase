package org.jbpt.petri.behavior;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;
import org.jbpt.petri.NetSystem;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.structure.ProcessModel2NetSystem;
import org.jbpt.throwable.TransformationException;


/**
 * This class implements a check for Full Concurrent Bisimilarity.
 * It compares the state transition relations of both state spaces.
 * @author Christian Wiggert
 *
 */
public class BisimilarityChecker2 {

	private INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> net1, net2;
	private boolean areBisimilar = false;
	private Map<String, BitSet> nameMap;
	private int n;
	
	public BisimilarityChecker2(ProcessModel process1, ProcessModel process2) throws TransformationException {
		this(ProcessModel2NetSystem.transform(process1), ProcessModel2NetSystem.transform(process2));
	}
	
	public BisimilarityChecker2(INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> net1, INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> net2) {
		this.net1 = net1;
		this.net2 = net2;
		this.compare();
	}
	
	private List<String> getNames(INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> net) {
		ArrayList<String> names = new ArrayList<String>();
		for (ITransition t:net.getTransitions()) {
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
	 * Runs a DFS to create the state transition relation for a given {@link NetSystem}.
	 * @param net - the petri net
	 * @param trans - all yet found transitions
	 * @param currentState
	 */
	private void dfs(INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> net, Map<BitSet, Set<BitSet>> trans, BitSet currentState) {
		Collection<ITransition> enabled = net.getEnabledTransitions();
		for (ITransition v:enabled) {
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
	private Map<BitSet, Set<BitSet>> createStateTransitions(INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> net) {
		Map<BitSet, Set<BitSet>> transitions = new HashMap<BitSet, Set<BitSet>>();
		net.loadNaturalMarking();
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
