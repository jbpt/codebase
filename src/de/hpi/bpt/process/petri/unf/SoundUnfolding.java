package de.hpi.bpt.process.petri.unf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.hpi.bpt.graph.algo.DirectedGraphAlgorithms;
import de.hpi.bpt.graph.algo.TransitiveClosure;
import de.hpi.bpt.process.petri.Flow;
import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;
import de.hpi.bpt.process.petri.unf.order.EsparzaTotalAdequateOrderForSafeSystems;

/**
 * Unfolding for soundness checks
 * 
 * Proof of concept - must be improved
 * 
 * @author Artem Polyvyanyy
 */
public class SoundUnfolding extends ProperUnfolding {
	
	private Set<Condition> unsafe	= null;
	private Set<Condition> deadlock	= null;
	
	protected SoundUnfolding() {}

	public SoundUnfolding(PetriNet pn) {
		if (!pn.isFreeChoice()) return; // must be free choice
		if (!pn.isWFNet()) return; // must be WF-net
		DirectedGraphAlgorithms<Flow,Node> dga = new DirectedGraphAlgorithms<Flow,Node>();
		if (dga.hasCycles(pn)) return; // must be acyclic
		
		this.totalOrderTs = new ArrayList<Transition>(pn.getTransitions());
		this.net = pn;
		
		UnfoldingSetup setup = new UnfoldingSetup();
		setup.ADEQUATE_ORDER = new EsparzaTotalAdequateOrderForSafeSystems();
		setup.MAX_BOUND	= Integer.MAX_VALUE;
		setup.MAX_EVENTS = Integer.MAX_VALUE;
		
		this.setup = setup;
		
		this.construct();
	}

	/**
	 * Get locally unsafe conditions
	 * @return set of locally unsafe conditions
	 */
	public Set<Condition> getLocallyUnsafeConditions() {
		if (this.unsafe == null) {
			this.unsafe = new HashSet<Condition>();
			
			for (Condition c1 : this.getConditions()) {
				for (Condition c2 : this.getConditions()) {
					if (c1.equals(c2)) continue;
					
					if (this.areConcurrent(c1,c2) && c1.getPlace().equals(c2.getPlace()))
						this.unsafe.add(c1);
				}
			}
			
		}
		
		return this.unsafe;
	}
	
	class DeadlockCandidate {
		public Place p;
		public Place p1;
		public Transition t1;
	}
	
	/**
	 * Get local deadlock conditions
	 * @return set of local deadlock conditions
	 */
	public Set<Condition> getLocalDeadlockConditions() {
		if (this.deadlock == null) {
			this.deadlock = new HashSet<Condition>();			
			OccurrenceNet occNet = this.getOccurrenceNet();
			
			for (Place p : occNet.getPlaces()) {
				if (occNet.getPostset(p).size()==0 && this.net.getPostset(occNet.getCondition(p).getPlace()).size()>0) {
					if (occNet.getPreset(p).size()>0) {
						Transition t = occNet.getPreset(p).iterator().next();
						if (occNet.getCorrespondingEvent(t) == null)
							this.deadlock.add(occNet.getCondition(p));
					}
					else
						this.deadlock.add(occNet.getCondition(p));
				}
			}
			
			Collection<Place> sinks = occNet.getSinkPlaces();
			Collection<DeadlockCandidate> dcs = new ArrayList<DeadlockCandidate>();
			
			for (Place p : occNet.getPlaces()) {
				if (occNet.getPostset(p).size()==0) continue;
				
				Transition t = occNet.getPostset(p).iterator().next();  
				if (occNet.getPreset(t).size()<2) continue;
				
				for (Place p1 : occNet.getPreset(t)) {
					if (p.equals(p1)) continue;
					
					for (Place p2 : sinks) {
						if (occNet.getOrderingRelation(p,p2)==OrderingRelation.CONFLICT &&
								occNet.getOrderingRelation(p1, p2)==OrderingRelation.CONCURRENT) {
							if (occNet.getPreset(p2).size()==0) continue;
							
							Transition t1 = occNet.getPreset(p2).iterator().next();
							if (!occNet.isCutoffEvent(t1)) continue;
							
							DeadlockCandidate dc = new DeadlockCandidate();
							dc.p = p; dc.p1 = p1; dc.t1 = t1;
							
							dcs.add(dc);
						}
					}
				}
			}
			
			// update occurrence net (re-wire)
			// TODO must be corrected - there should exist no path!
			Set<Transition> cutoffs = occNet.getCutoffEvents();
			for (Transition cutoff : cutoffs) {
				occNet.removeVertices(occNet.getSuccessors(cutoff));
				Transition corr = occNet.getCorrespondingEvent(cutoff);
				for (Place pcorr : occNet.getPostset(corr))
					occNet.addFlow(cutoff, pcorr);
			}
			
			TransitiveClosure<Flow, Node> tc = new TransitiveClosure<Flow,Node>(occNet);
			
			for (DeadlockCandidate dc : dcs) {
				if (tc.hasPath(dc.t1, dc.p))
					this.deadlock.add(occNet.getCondition(dc.p));
			}
		}
		
		return this.deadlock;
	}
	
	public boolean isSound() {
		return this.getLocallyUnsafeConditions().size()==0 && this.getLocalDeadlockConditions().size()==0;
	}
}
