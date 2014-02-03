package org.jbpt.petri.behavior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;

public class SimpleStateSpace<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F, N, P, T>> {

	protected INetSystem<F,N,P,T,M> netSystem = null;
	
	protected Map<M,Set<T>> enabled = null;
	protected Map<M,Set<T>> toVisit = null;
	
	protected Map<M, Map<T, M>> stateTransitions = null;
	
	public SimpleStateSpace(INetSystem<F, N, P, T, M> netSystem) {
		super();
		this.netSystem = netSystem;
		this.enabled = new HashMap<M, Set<T>>();
		this.toVisit = new HashMap<M, Set<T>>();
		this.stateTransitions = new HashMap<M, Map<T, M>>();
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
		
		this.enabled.put(iM, this.netSystem.getEnabledTransitions());
		this.toVisit.put(iM, this.netSystem.getEnabledTransitions());
		
		while (!this.toVisit.isEmpty() && this.getNumberOfMarkings() < numberOfMarkings) {
			// select marking
			M m = this.toVisit.keySet().iterator().next();
			
			// no transitions to check?
			if (this.toVisit.get(m).isEmpty()) {
				this.toVisit.remove(m);
				continue;
			}
			
			// select transition
			T t = this.toVisit.get(m).iterator().next();
			
			// fire transition
			this.netSystem.loadMarking(m);
			this.netSystem.fire(t);
			this.toVisit.get(m).remove(t);
			@SuppressWarnings("unchecked")
			M nM = (M) this.netSystem.getMarking().clone(); 
			
			// record transition
			if (!this.stateTransitions.containsKey(m))
				this.stateTransitions.put(m, new HashMap<T,M>());
			this.stateTransitions.get(m).put(t,nM);
			
//			System.out.println("FIRE: " + t.getId() + " ( " + t.getLabel() + " )");
//			System.out.println(m.toString() + "  " + t.getId() + "  " + nM.toString());

			// get new enabled
			Set<T> nEnabled = this.netSystem.getEnabledTransitions(this.enabled.get(m), t);
			this.enabled.put(nM, nEnabled);
			
			// check whether transitions have to be checked
			Set<T> stillToCheck = new HashSet<T>(nEnabled);
			if (this.stateTransitions.containsKey(nM))
				stillToCheck.removeAll(this.stateTransitions.get(nM).keySet());
			
			if (!stillToCheck.isEmpty())
				this.toVisit.put(nM, stillToCheck);
			
		}
		
		/*
		 * Reset initial marking 
		 */
		this.netSystem.loadMarking(iM);
	}

	public void clear() {
		this.enabled = new HashMap<M, Set<T>>();
		this.toVisit = new HashMap<M, Set<T>>();
		this.stateTransitions = new HashMap<M, Map<T, M>>();
	}
	
	public String toDOT() {
		String result = "digraph G {\n";
		result += "graph [fontname=\"Helvetica\" fontsize=10 nodesep=0.35 ranksep=\"0.25 equally\"];\n";
		result += "node [fontname=\"Helvetica\" fontsize=10 fixedsize style=filled fillcolor=white penwidth=\"2\"];\n";
		result += "edge [fontname=\"Helvetica\" fontsize=10 arrowhead=normal color=black];\n";
		result += "\n";
		result += "node [shape=circle];\n";
		
		List<M> tmpMarkings = new ArrayList<M>(this.enabled.keySet());
		
		for (int i = 0; i < tmpMarkings.size(); i++) {
			result += String.format("\tn%s[label=\"%s\" width=\".3\" height=\".3\"];\n", i, tmpMarkings.get(i).toString());
		}
		
		result += "\n";
		
		result += "\n";
		for (M m1 : this.stateTransitions.keySet()) {
			for (Entry<T, M> step : this.stateTransitions.get(m1).entrySet()) {
				result += String.format("\tn%s->n%s [label=\"%s\"];\n", tmpMarkings.indexOf(m1), tmpMarkings.indexOf(step.getValue()), step.getKey().getLabel());
			}
		}
		result += "}\n";
		
		return result;
	}
	
	public int getNumberOfMarkings() {
		return this.enabled.keySet().size();
	}

}
