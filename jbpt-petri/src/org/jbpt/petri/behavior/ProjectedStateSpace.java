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

public class ProjectedStateSpace<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F, N, P, T>> {
	
	protected INetSystem<F,N,P,T,M> netSystem = null;

	protected Map<M,Set<T>> enabled = null;
	protected Map<T,Set<M>> txM = null;
	protected Map<T,Set<M>> vTxM = null;

	protected Map<M, Map<T, M>> stateTransitions = null;

	protected boolean[][] stepMatrix = null;
	
	protected Map<T,Integer> projectionSetForStepMatrix = null;

	public ProjectedStateSpace(INetSystem<F, N, P, T, M> netSystem, Set<T> projectionSet) {
		super();
		this.netSystem = netSystem;
		this.enabled = new HashMap<>();
		this.txM = new HashMap<>();
		this.vTxM = new HashMap<>();
		this.stateTransitions = new HashMap<>();

		this.projectionSetForStepMatrix = new HashMap<>();
		
		/*
		 * All transitions in the projection set get an id
		 * for the step matrix
		 */
		List<T> tmpList = new ArrayList<>(projectionSet);
		for (int i = 0; i < tmpList.size(); i++) 
			this.projectionSetForStepMatrix.put(tmpList.get(i), i); 

		/*
		 * Init the step matrix
		 */
		this.stepMatrix = new boolean[projectionSetForStepMatrix.keySet().size()][projectionSetForStepMatrix.keySet().size()];
		for (int i = 0; i < projectionSetForStepMatrix.keySet().size(); i++) {
			this.stepMatrix[i][i] = false;
			for (int j = i + 1; j < projectionSetForStepMatrix.keySet().size(); j++) {
				this.stepMatrix[i][j] = false;
				this.stepMatrix[j][i] = false;
			}
		}
	}
	
	public void create() {
		this.createUpToNumberOfMarkings(Integer.MAX_VALUE);
	}

	public void createUpToNumberOfMarkings(int numberOfMarkings) {
		
		/*
		 * Clone initial marking for storing it as part of the SimpleStateSpace and for 
		 * being able to reset the net system at the end
		 */
		@SuppressWarnings("unchecked")
		M iM = (M) this.netSystem.getMarking().clone();
		
		Set<T> iEnabled = new HashSet<>(this.netSystem.getEnabledTransitions());
		
		this.enabled.put(iM, iEnabled);
		
		for (T t : this.netSystem.getTransitions()) 
			this.vTxM.put(t, new HashSet<M>());

		for (T t : iEnabled) {
			M nM = fireTransition(iM, iEnabled, t);
			addToVisit(t,nM);
		}
		
		while (!this.txM.isEmpty() && this.getNumberOfMarkings() < numberOfMarkings) {
			T t = this.txM.keySet().iterator().next();
			
			if (this.txM.get(t).isEmpty()) {
				this.txM.remove(t);
				continue;
			}
			
			M m = this.txM.get(t).iterator().next();
			txM.get(t).remove(m);
			vTxM.get(t).add(m);

			for (T te : this.enabled.get(m)) {

				M nM = fireTransition(m, 
						this.enabled.get(m), te);
				
				if (this.projectionSetForStepMatrix.keySet().contains(te)) {
					if (this.projectionSetForStepMatrix.keySet().contains(t))
						addStep(t,te);
					
					if (!visited(te,nM)) 
						addToVisit(te,nM);
				}
				else {
					if (!visited(t,nM)) 
						addToVisit(t,nM);
				}
			}
		}
		
		/*
		 * Reset initial marking 
		 */
		this.netSystem.loadMarking(iM);
	}
	
	protected void addToVisit(T t, M m) {
		if (!this.txM.containsKey(t))
			this.txM.put(t, new HashSet<M>());
		
		this.txM.get(t).add(m);
	}

	
	protected boolean visited(T t, M m) {
		return this.vTxM.get(t).contains(m);
	}
	
	protected M fireTransition(M from, Set<T> enabled, T t) {
		
//		System.out.println("FIRE: " + t.getId() + " ( " + t.getLabel() + " )");
		
		this.netSystem.loadMarking(from);
		this.netSystem.fire(t);
		@SuppressWarnings("unchecked")
		M nM = (M) this.netSystem.getMarking().clone(); 
		
		if (!this.enabled.containsKey(nM)) {
			Set<T> nEnabled = this.netSystem.getEnabledTransitions(enabled, t);
			this.enabled.put(nM, nEnabled);
		}
		
		if (!this.stateTransitions.containsKey(from))
			this.stateTransitions.put(from, new HashMap<T,M>());
		this.stateTransitions.get(from).put(t, nM);
		
		return nM;
	}
	
	public int getNumberOfMarkings() {
		return this.enabled.keySet().size();
	}

	public void addStep(T t1, T t2) {
		this.stepMatrix[this.projectionSetForStepMatrix.get(t1)][this.projectionSetForStepMatrix.get(t2)] = true;			
	}
	
	public boolean isStep(N t1, N t2) {
		if (!this.projectionSetForStepMatrix.keySet().contains(t1) || !this.projectionSetForStepMatrix.keySet().contains(t2))
			throw new IllegalArgumentException("Transitions have not been in projection set.");
		
		return this.stepMatrix[this.projectionSetForStepMatrix.get(t1)][this.projectionSetForStepMatrix.get(t2)];
	}
}
