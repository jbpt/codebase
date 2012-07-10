package org.jbpt.petri.unfolding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jbpt.algo.CombinationGenerator;
import org.jbpt.algo.graph.TransitiveClosure;
import org.jbpt.petri.Flow;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.petri.unfolding.order.EsparzaAdequateOrderForArbitrarySystems;


/**
 * Unfolding for soundness checks (multi-source-multi-sink nets)
 * 
 * @author Artem Polyvyanyy
 */
public class SoundUnfoldingMSMS extends SoundUnfolding {

	protected NetSystem originativeSystem = null;
	protected Map<Node,Node> nodeMapping = null;
	
	/**
	 * @assumption Net system is free-choice
	 * @assumption Net system is multi-terminal
	 * @assumption Net system is acyclic
	 */
	public SoundUnfoldingMSMS(NetSystem sys) {		
		this.originativeSystem = sys;
		this.nodeMapping = new HashMap<Node,Node>();
		
		this.sys = this.constructAugmentedVersion();
		this.initialBP = new Cut(this.sys);
		this.totalOrderTs = new ArrayList<Transition>(this.sys.getTransitions());
		
		UnfoldingSetup setup = new UnfoldingSetup();
		setup.ADEQUATE_ORDER = new EsparzaAdequateOrderForArbitrarySystems();
		setup.MAX_BOUND		 = Integer.MAX_VALUE;
		setup.MAX_EVENTS	 = Integer.MAX_VALUE;
		this.setup = setup;
		
		// construct unfolding
		this.construct();
	}

	/**
	 * Construct the augmented version of the net
	 * - Add a fresh source place s 
	 * - Add a fresh start transition t_c for every combination c of source places of the net
	 * - Add a fresh flow from s to every start transition
	 * - For every start transition t_c, add fresh flow from t_c to every place in c
	 */
	private NetSystem constructAugmentedVersion() {
		NetSystem result = this.originativeSystem.clone(this.nodeMapping);
		
		Collection<Place> sources = result.getSourcePlaces();
		Place s = new Place();
		for (int i=0; i<sources.size(); i++) {
			CombinationGenerator<Place> cg = new CombinationGenerator<Place>(sources, i+1);
			while (cg.hasMore()) {
				Collection<Place> comb = cg.getNextCombination();
				Transition t = new Transition();
				result.addFlow(s,t);
				for (Place p : comb) {
					result.addFlow(t,p);
				}
			}
		}
		
		result.loadNaturalMarking();
		return  result;
	}
	
	@Override
	public NetSystem getNetSystem() {
		return this.originativeSystem;
	}
	
	@Override
	public boolean isSound() {
		Collection<Transition> augTs = new ArrayList<Transition>(this.sys.getTransitions());
		Collection<Transition> augStartTs = new ArrayList<Transition>(this.sys.getPostset(this.sys.getSourcePlaces().iterator().next()));
		augTs.removeAll(augStartTs);
		
		Set<Condition> cs = new HashSet<Condition>(this.getLocallyUnsafeConditions());
		cs.addAll(this.getLocalDeadlockConditions());
		
		for (Event e : this.getEvents()) {
			boolean flag = false;
			for (Condition c : cs) {
				if (this.areCausal(e,c) || this.areCausal(c,e)) {
					flag = true;
					break;
				}
			}
			if (flag) continue;
			
			augTs.remove(e.getTransition());
		}
		
		return augTs.isEmpty();
	}
	
	public void completeOriginativeSystemWithCorrectInstantiations() {
		Set<Condition> errors = new HashSet<Condition>(this.getLocallyUnsafeConditions());
		errors.addAll(this.getLocalDeadlockConditions());
		
		OccurrenceNet occ = this.getOccurrenceNet();
		Collection<Transition> starts = new ArrayList<Transition>(occ.getPostset(occ.getSourcePlaces().iterator().next()));
		Collection<Transition> correctStarts = new ArrayList<Transition>();
		TransitiveClosure<Flow,Node> tc = new TransitiveClosure<Flow,Node>(occ);
		
		for (Transition start : starts) {
			boolean flag = true;
			for (Condition error : errors) {
				if (tc.hasPath(start, occ.getPlace(error))) {
					flag = false;
					break;
				}
			}
			
			if (flag)
				correctStarts.add(start);
		}
		
		Place src = new Place();
		this.originativeSystem.addPlace(src);
		for (Transition start : correctStarts) {
			Transition t = new Transition();
			this.originativeSystem.addFlow(src,t);
			for (Place p : occ.getPostset(start)) {
				Place pp = this.getOriginativePlace(occ,p);
				this.originativeSystem.addFlow(t,pp);
			}
		}
		
	}
	
	private Place getOriginativePlace(OccurrenceNet occ, Place p) {
		Place pp = occ.getCondition(p).getPlace(); // place in this.sys
		for (Map.Entry<Node,Node> entry : this.nodeMapping.entrySet()) {
			if (entry.getValue().equals(pp))
				return (Place) entry.getKey();
		}
		return null;
	}

	/**
	 * Get original net without augmentation
	 * @return original net
	 */
	public PetriNet getOriginalNet() {
		return this.originativeSystem;
	}
}
