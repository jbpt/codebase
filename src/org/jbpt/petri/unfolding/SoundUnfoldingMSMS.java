package org.jbpt.petri.unfolding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jbpt.algo.CombinationGenerator;
import org.jbpt.algo.graph.TransitiveClosure;
import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPetriNet;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.petri.unfolding.order.EsparzaAdequateOrderForArbitrarySystems;


/**
 * Unfolding for soundness checks (multi-source-multi-sink nets)
 * 
 * @author Artem Polyvyanyy
 */
public class SoundUnfoldingMSMS extends SoundUnfolding {

	protected INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> originativeSystem = null;
	protected Map<INode,INode> nodeMapping = null;
	
	/**
	 * @assumption Net system is free-choice
	 * @assumption Net system is multi-terminal
	 * @assumption Net system is acyclic
	 */
	public SoundUnfoldingMSMS(INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> sys) {		
		this.originativeSystem = sys;
		this.nodeMapping = new HashMap<>();
		
		this.sys = this.constructAugmentedVersion();
		this.initialBP = new Cut(this.sys);
		this.totalOrderTs = new ArrayList<ITransition>(this.sys.getTransitions());
		
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
	private INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> constructAugmentedVersion() {
		INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> result = this.originativeSystem.clone(this.nodeMapping);
		
		Collection<IPlace> sources = result.getSourcePlaces();
		Place s = new Place();
		for (int i=0; i<sources.size(); i++) {
			CombinationGenerator<IPlace> cg = new CombinationGenerator<IPlace>(sources, i+1);
			while (cg.hasMore()) {
				Collection<IPlace> comb = cg.getNextCombination();
				Transition t = new Transition();
				result.addFlow(s,t);
				for (IPlace p : comb) {
					result.addFlow(t,p);
				}
			}
		}
		
		result.loadNaturalMarking();
		return  result;
	}
	
	@Override
	public INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> getNetSystem() {
		return this.originativeSystem;
	}
	
	@Override
	public boolean isSound() {
		Collection<ITransition> augTs = new ArrayList<>(this.sys.getTransitions());
		Collection<ITransition> augStartTs = new ArrayList<>(this.sys.getPostset(this.sys.getSourcePlaces().iterator().next()));
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
		// TODO
		//errors.addAll(this.getLocalDeadlockConditions());
		
		OccurrenceNet occ = this.getOccurrenceNet();
		Collection<ITransition> starts = new ArrayList<>(occ.getPostset(occ.getSourcePlaces().iterator().next()));
		Collection<ITransition> correctStarts = new ArrayList<>();
		TransitiveClosure<IFlow<INode>,INode> tc = new TransitiveClosure<>(occ);
		
		for (ITransition start : starts) {
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
		for (ITransition start : correctStarts) {
			Transition t = new Transition();
			this.originativeSystem.addFlow(src,t);
			for (IPlace p : occ.getPostset(start)) {
				Place pp = this.getOriginativePlace(occ,(Place)p);
				this.originativeSystem.addFlow(t,pp);
			}
		}
		
	}
	
	private Place getOriginativePlace(OccurrenceNet occ, Place p) {
		Place pp = occ.getCondition(p).getPlace(); // place in this.sys
		for (Map.Entry<INode,INode> entry : this.nodeMapping.entrySet()) {
			if (entry.getValue().equals(pp))
				return (Place) entry.getKey();
		}
		return null;
	}

	/**
	 * Get original net without augmentation
	 * @return original net
	 */
	public IPetriNet<IFlow<INode>, INode, IPlace, ITransition> getOriginalNet() {
		return this.originativeSystem;
	}
}
