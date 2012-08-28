package org.jbpt.petri.behavior;

import java.util.ArrayList;
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

public class StateSpace<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F, N, P, T>> {
	
	public final static int MARKING_ARRAY_INCREMENT = 10000;
	
	protected INetSystem<F,N,P,T,M> netSystem = null;
	
	protected Map<P,Integer> placeMapping = null;
	
	protected int[][] markings = null;
	
	protected int lastMarking = 0;
	
	protected Map<String,Integer> labelMapping = null;
	protected boolean[][][] stateTransitionLabels = null;
	
	public StateSpace(INetSystem<F, N, P, T, M> netSystem) {
		super();
		this.netSystem = netSystem;
		this.placeMapping = new HashMap<>();
		
		/*
		 * Fill mappings to get id in constant time
		 */
		List<P> tmpList = new ArrayList<>(netSystem.getPlaces());
		for (int i = 0; i < tmpList.size(); i++) 
			this.placeMapping.put(tmpList.get(i), i); 
		
		List<String> tmpList2 = new ArrayList<>();
		for (T t : this.netSystem.getTransitions())
			if (t.isObservable())
				tmpList2.add(t.getLabel());

		for (int i = 0; i < tmpList2.size(); i++) 
			this.labelMapping.put(tmpList2.get(i), i); 
		
		this.markings = new int[MARKING_ARRAY_INCREMENT][this.placeMapping.keySet().size()];
		this.stateTransitionLabels = new boolean[MARKING_ARRAY_INCREMENT][MARKING_ARRAY_INCREMENT][this.labelMapping.keySet().size()];
		
		for (P p : this.netSystem.getMarking().toMultiSet())
			this.markings[0][this.placeMapping.get(p)] += 1;
		
	}
	
	public void create() {
		this.createUpToNumberOfMarkings(Integer.MAX_VALUE);
	}

	public void createUpToNumberOfMarkings(int numberOfMarkings) {
		Set<T> enabled = this.netSystem.getEnabledTransitions();

		int currentM = 0;
		
		while (!enabled.isEmpty() && this.lastMarking < numberOfMarkings) {
			T t = enabled.iterator().next();
			currentM = fireTransition(currentM, t);
			enabled = getEnabledTransitions(enabled, t, currentM);
		}
	}

	protected int fireTransition(int currentM, T t) {

		/*
		 * Derive new marking
		 */
		int[] newM = this.markings[currentM];
		
		for (P p : this.netSystem.getPreset(t))
			newM[this.placeMapping.get(p)] -= 1;
		for (P p : this.netSystem.getPostset(t))
			newM[this.placeMapping.get(p)] += 1;

		/*
		 * Store new marking
		 */
		int newMId = -1;
		for (int i = 0; i < this.markings.length; i++) 
			newMId = (this.markings[i] == newM)? i : -1;
		
		if (newMId == -1) {
			if (this.markings.length == lastMarking) {

				int[][] oldMarkings = this.markings;
				this.markings = new int[oldMarkings.length + MARKING_ARRAY_INCREMENT][this.placeMapping.keySet().size()];
				System.arraycopy(oldMarkings, 0, this.markings, 0, oldMarkings.length);

				
				boolean[][][] oldstateTransitionLabels = this.stateTransitionLabels;
				this.stateTransitionLabels = new boolean[oldstateTransitionLabels.length + MARKING_ARRAY_INCREMENT][oldstateTransitionLabels.length + MARKING_ARRAY_INCREMENT][this.labelMapping.keySet().size()];
				System.arraycopy(oldstateTransitionLabels, 0, this.stateTransitionLabels, 0, oldstateTransitionLabels.length);
			}
			this.lastMarking++;
			newMId = this.lastMarking;
			this.markings[newMId] = newM;
		}
		
		/*
		 * Store transition
		 */
		this.stateTransitionLabels[currentM][newMId][this.labelMapping.get(t.getLabel())] = true;
		
		return newMId;
	}

	protected Set<T> getEnabledTransitions(Set<T> lastEnabled, T lastFired, int mId) {
		Set<T> enabled = new HashSet<>(lastEnabled);
		/*
		 * Old disabled?
		 */
		transition:
		for (T t : lastEnabled){
			for (P p : this.netSystem.getPreset(t)) {
				if (this.markings[mId][this.placeMapping.get(p)] < 1) {
					enabled.remove(t);
					continue transition;
				}
			}
		}
		/*
		 * New enabled?
		 */
		for (P p : this.netSystem.getPostset(lastFired)) {
			transition:
			for (T t : this.netSystem.getPostset(p)) {
				for (P p2 : this.netSystem.getPreset(t)) {
					if (this.markings[mId][this.placeMapping.get(p2)] < 1)
						continue transition;
				}
				enabled.add(t);
			}
		}
		return enabled;
	}
	
	public String toDOT() {
		String result = "digraph G {\n";
		result += "graph [fontname=\"Helvetica\" fontsize=10 nodesep=0.35 ranksep=\"0.25 equally\"];\n";
		result += "node [fontname=\"Helvetica\" fontsize=10 fixedsize style=filled fillcolor=white penwidth=\"2\"];\n";
		result += "edge [fontname=\"Helvetica\" fontsize=10 arrowhead=normal color=black];\n";
		result += "\n";
		result += "node [shape=circle];\n";
		
		for (int i = 0; i < this.lastMarking; i++) {
			String label = " ";
			for (int j = 0; j < this.markings[i].length; j++) 
				if (this.markings[i][j]> 0)
					label += "j(" + this.markings[i][j] + ") ";
			label += "";
			result += String.format("\tn%s[label=\"%s\" width=\".3\" height=\".3\"];\n", i, label);
		}
		
		result += "\n";
		
		result += "\n";
		for (int i = 0; i < this.lastMarking; i++) {
			for (int j = 0; j < this.lastMarking; j++) {
				for (String l : this.labelMapping.keySet()) {
					if (this.stateTransitionLabels[i][j][this.labelMapping.get(l)])
						result += String.format("\tn%s->n%s [label=\"%s\"];\n", i, j, l);
				}
			}
		}
		result += "}\n";
		
		return result;
	}

}
