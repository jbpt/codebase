package de.hpi.bpt.process.petri.unf;

import java.util.HashSet;
import java.util.Set;

import de.hpi.bpt.graph.algo.DirectedGraphAlgorithms;
import de.hpi.bpt.process.petri.Flow;
import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;
import de.hpi.bpt.process.petri.unf.order.EsparzaTotalAdequateOrderForSafeSystems;

/**
 * Unfolding for soundness checks
 * 
 * @author Artem Polyvyanyy
 */
public class SoundUnfolding extends ProperUnfolding {
	
	private Set<Condition> unsafe	= null;
	private Set<Condition> deadlock	= null;

	public SoundUnfolding(PetriNet pn) {
		if (!pn.isFreeChoice()) return; // net must be free choice
		DirectedGraphAlgorithms<Flow,Node> dga = new DirectedGraphAlgorithms<Flow,Node>();
		if (dga.hasCycles(pn)) return; // net must be acyclic
		
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
				
				// TODO
			}
		}
		
		return this.deadlock;
	}
}
