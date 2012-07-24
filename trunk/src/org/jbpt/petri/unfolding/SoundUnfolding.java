package org.jbpt.petri.unfolding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.jbpt.algo.graph.DirectedGraphAlgorithms;
import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;
import org.jbpt.petri.Place;
import org.jbpt.petri.structure.PetriNetStructuralClassChecks;
import org.jbpt.petri.unfolding.order.UnfoldingAdequateOrder;


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
	
	protected static DirectedGraphAlgorithms<IFlow<INode>,INode> dga = new DirectedGraphAlgorithms<>();
	
	protected SoundUnfolding() {}

	public SoundUnfolding(INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> sys) {
		if (!PetriNetStructuralClassChecks.isFreeChoice(sys)) throw new IllegalArgumentException("Net must be free choice!");
		if (!PetriNetStructuralClassChecks.isWorkflowNet(sys)) throw new IllegalArgumentException("Net must be a WF-net!");
		if (dga.isAcyclic(sys)) throw new IllegalArgumentException("Net must be acyclic!");
		
		this.sys = sys;
		this.initialBP = new Cut(this.sys);
		this.totalOrderTs = new ArrayList<ITransition>(this.sys.getTransitions());
		
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
			OccurrenceNet BP = this.getOccurrenceNet();
			
			for (IPlace p : BP.getPlaces()) {
				if (BP.getPostset(p).isEmpty() && !this.sys.getPostset(BP.getCondition((Place)p).getPlace()).isEmpty()) {
					this.deadlock.add(BP.getCondition((Place)p));
				}
			}
						
			for (IPlace p : BP.getPlaces()) {
				for (ITransition t : BP.getPostset(p)){					
					for (IPlace p1 : BP.getPreset(t)) {
						if (p.equals(p1)) continue;
						
						for (IPlace p2 : BP.getSinkPlaces()) {
							if (BP.getOrderingRelation((Place)p,(Place)p2)==OrderingRelation.CONFLICT && BP.getOrderingRelation((Place)p1,(Place)p2)==OrderingRelation.CONCURRENT) {
								this.deadlock.add(BP.getCondition((Place)p));
							}
						}
					}
				}
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
