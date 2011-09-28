package de.hpi.bpt.process.petri.unf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.hpi.bpt.graph.algo.DirectedGraphAlgorithms;
import de.hpi.bpt.graph.algo.TransitiveClosure;
import de.hpi.bpt.process.petri.Flow;
import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;
import de.hpi.bpt.process.petri.unf.order.UnfoldingAdequateOrder;

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
	
	protected static DirectedGraphAlgorithms<Flow,Node> dga = new DirectedGraphAlgorithms<Flow,Node>();
	
	protected SoundUnfolding() {}

	public SoundUnfolding(PetriNet pn) {
		if (!pn.isFreeChoice()) throw new IllegalArgumentException("Net must be free choice!");
		if (!pn.isWFNet()) throw new IllegalArgumentException("Net must be a WF-net!");
		if (dga.hasCycles(pn)) throw new IllegalArgumentException("Net must be acyclic!");
		
		this.net = pn;
		this.totalOrderTs = new ArrayList<Transition>(this.net.getTransitions());
		
		UnfoldingSetup setup = new UnfoldingSetup();
		setup.ADEQUATE_ORDER = new UnfoldingAdequateOrder();
		setup.MAX_BOUND		 = Integer.MAX_VALUE;
		setup.MAX_EVENTS	 = Integer.MAX_VALUE;
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
	
	/**
	 * Get local deadlock conditions
	 * @return set of local deadlock conditions
	 */
	public Set<Condition> getLocalDeadlockConditions() {
		if (this.deadlock == null) {
			this.deadlock = new HashSet<Condition>();			
			OccurrenceNet BP = null;
			try { BP = (OccurrenceNet) this.getOccurrenceNet().clone(); } 
			catch (CloneNotSupportedException e) { e.printStackTrace(); }
			
			// p* is empty, nu(p)* is not empty, and there exists no t in *p that is a cutoff
			for (Place p : BP.getPlaces()) {
				if (BP.getPostset(p).isEmpty() && !this.net.getPostset(BP.getCondition(p).getPlace()).isEmpty()) {
					if (!BP.getPreset(p).isEmpty()) {
						Transition t = BP.getPreset(p).iterator().next();
						if (!BP.isCutoffEvent(t))
							this.deadlock.add(BP.getCondition(p));
					}
					else
						this.deadlock.add(BP.getCondition(p));
				}
			}
			
			// !!!
			Collection<Place> sinks = BP.getSinkPlaces();
			Map<Place,Set<Transition>> p2s = new HashMap<Place,Set<Transition>>(); 
			
			for (Place p : BP.getPlaces()) {
				boolean flag = false;
				
				for (Transition t : BP.getPostset(p)){					
					for (Place p1 : BP.getPreset(t)) {
						if (p.equals(p1)) continue;
						
						for (Place p2 : sinks) {
							if (BP.getOrderingRelation(p,p2)==OrderingRelation.CONFLICT && 
									BP.getOrderingRelation(p1,p2)==OrderingRelation.CONCURRENT) {
								
								if (BP.getPreset(p2).size()==0) {
									this.deadlock.add(BP.getCondition(p));
									p2s.remove(p);
									flag = true;
									break;
								}
								
								Transition t1 = BP.getPreset(p2).iterator().next();
								if (!BP.isCutoffEvent(t1)) {
									this.deadlock.add(BP.getCondition(p));
									p2s.remove(p);
									flag = true;
									break;
								}
								else {
									if (p2s.get(p)==null) p2s.put(p, new HashSet<Transition>());
									p2s.get(p).add(t1);
								}
							}
						}
						if (flag) break;
					}	
				}
			}
			
			// update occurrence net (re-wire)
			Set<Transition> cutoffs = BP.getCutoffEvents();
			for (Transition cutoff : cutoffs) {
				BP.removeVertices(BP.getSuccessors(cutoff));
				Transition corr = BP.getCorrespondingEvent(cutoff);
				for (Place pcorr : BP.getPostset(corr))
					BP.addFlow(cutoff, pcorr);
			}
			
			TransitiveClosure<Flow, Node> tc = new TransitiveClosure<Flow,Node>(BP);
			
			for (Map.Entry<Place,Set<Transition>> entry : p2s.entrySet()) {
				boolean flag = false;
				for (Transition t : entry.getValue()) {
					if (tc.hasPath(t,entry.getKey())) {
						flag = true;
						break;
					}
				}
				
				if (!flag)
					this.deadlock.add(BP.getCondition(entry.getKey()));
			}
		}
		
		return this.deadlock;
	}
	
	/**
	 * Check if the net is sound
	 * @return true if originative net is sound; otherwise false
	 */
	public boolean isSound() {
		return this.getLocallyUnsafeConditions().size()==0 && this.getLocalDeadlockConditions().size()==0;
	}
}
