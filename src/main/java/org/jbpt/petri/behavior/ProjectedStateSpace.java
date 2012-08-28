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

public class ProjectedStateSpace<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F, N, P, T>>
extends StateSpace<F, N, P, T, M> {
	
	protected boolean[][] stepRelation = null;
	
	protected Map<Integer,Set<T>> mToEnabled = new HashMap<>();

	protected Map<T,Integer> projectedTransitionMapping = null;

	public ProjectedStateSpace(INetSystem<F, N, P, T, M> netSystem, Set<T> projectionSet) {
		super(netSystem);
		
		List<T> tmpList = new ArrayList<>(projectionSet);
		for (int i = 0; i < tmpList.size(); i++) 
			this.projectedTransitionMapping.put(tmpList.get(i), i); 
		
		this.stepRelation = new boolean[projectedTransitionMapping.keySet().size()][projectedTransitionMapping.keySet().size()];
		for (int i = 0; i < projectedTransitionMapping.keySet().size(); i++) {
			this.stepRelation[i][i] = false;
			for (int j = i + 1; j < projectedTransitionMapping.keySet().size(); j++) {
				this.stepRelation[i][j] = false;
				this.stepRelation[j][i] = false;
			}
		}
	}
	
	@Override
	public void createUpToNumberOfMarkings(int numberOfMarkings) {
		
		Map<T , Set<Integer>> txM = new HashMap<>();
		Map<Integer, Map<T,Integer>> prevMforT = new HashMap<>();
		
		Map<T , Set<Integer>> vtxM = new HashMap<>();
		
		for (T t : this.netSystem.getTransitions())
			vtxM.put(t, new HashSet<Integer>());
		
		this.mToEnabled.put(0,new HashSet<T>());
		
		for (T t : this.netSystem.getEnabledTransitions()) {
			int newM = fireTransition(0, t);
			Set<Integer> m = new HashSet<>();
			m.add(newM);
			txM.put(t, m);
			this.mToEnabled.get(0).add(t);
			prevMforT.put(newM, new HashMap<T,Integer>());
			prevMforT.get(newM).put(t, 0);
		}

		while (!txM.keySet().isEmpty() && this.lastMarking < numberOfMarkings) {
			T t = txM.keySet().iterator().next();
			int mToCheck = txM.get(t).iterator().next();			

			vtxM.get(t).add(mToCheck);
			
			Set<T> enabled = getEnabledTransitions(this.mToEnabled.get(prevMforT.get(mToCheck).get(t)), t, mToCheck);

			this.mToEnabled.put(mToCheck,new HashSet<T>());
			this.mToEnabled.get(mToCheck).addAll(enabled);
			
			for (T te : enabled) {
				if (this.projectedTransitionMapping.containsKey(te)) {
					if (this.projectedTransitionMapping.containsKey(t))
						this.stepRelation[this.projectedTransitionMapping.get(t)][this.projectedTransitionMapping.get(te)] = true;
					
					int nextM = fireTransition(mToCheck, te);
					
					if (!prevMforT.containsKey(nextM))
						prevMforT.put(nextM, new HashMap<T,Integer>());
					
					prevMforT.get(nextM).put(t, mToCheck);
					
					if (!vtxM.get(te).contains(nextM)) {
						if (!txM.containsKey(te))
							txM.put(te, new HashSet<Integer>());
						txM.get(te).add(nextM);
						
					}
				}
				else {
					int nextM = fireTransition(mToCheck, te);
					
					if (!prevMforT.containsKey(nextM))
						prevMforT.put(nextM, new HashMap<T,Integer>());
					
					prevMforT.get(nextM).put(t, mToCheck);
					
					if (!vtxM.get(t).contains(nextM)) {
						if (!txM.containsKey(t))
							txM.put(t, new HashSet<Integer>());
						txM.get(t).add(nextM);
					}
				}
			}
		}
	}
	
	public boolean isStep(T t1, T t2) {
		if (!this.projectedTransitionMapping.keySet().contains(t1) || !this.projectedTransitionMapping.keySet().contains(t2))
			throw new IllegalArgumentException("Transitions have not been in projection set.");
		
		return this.stepRelation[this.projectedTransitionMapping.get(t1)][this.projectedTransitionMapping.get(t2)];
	}
}
