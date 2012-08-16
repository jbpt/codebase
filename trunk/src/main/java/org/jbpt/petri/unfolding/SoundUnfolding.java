package org.jbpt.petri.unfolding;



/**
 * Unfolding for soundness checks
 * 
 * Proof of concept - must be improved
 * 
 * @author Artem Polyvyanyy
 */
public class SoundUnfolding extends ProperUnfolding {
	
	/*private Set<Condition> unsafe	= null;
	private Set<Condition> deadlock	= null;
	
	protected static DirectedGraphAlgorithms<Flow,Node> dga = new DirectedGraphAlgorithms<Flow,Node>();
	
	protected SoundUnfolding() {}

	public SoundUnfolding(NetSystem sys) {
		if (!PetriNet.STRUCTURAL_CHECKS.isFreeChoice(sys)) throw new IllegalArgumentException("Net must be free choice!");
		if (!PetriNet.STRUCTURAL_CHECKS.isWorkflowNet(sys)) throw new IllegalArgumentException("Net must be a WF-net!");
		if (dga.isAcyclic(sys)) throw new IllegalArgumentException("Net must be acyclic!");
		
		this.sys = sys;
		this.initialBranchingProcess = new Cut(this.sys);
		this.totalOrderTs = new ArrayList<Transition>(this.sys.getTransitions());
		
		BranchingProcessSetup setup = new BranchingProcessSetup();
		setup.ADEQUATE_ORDER = new UnfoldingAdequateOrder();
		setup.MAX_BOUND		 = Integer.MAX_VALUE;
		setup.MAX_EVENTS	 = Integer.MAX_VALUE;
		this.setup = setup;
		
		this.construct();
	}

	*//**
	 * Get locally unsafe conditions
	 * @return set of locally unsafe conditions
	 *//*
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
	
	*//**
	 * Get local deadlock conditions
	 * @return set of local deadlock conditions
	 *//*
	public Set<Condition> getLocalDeadlockConditions() {
		if (this.deadlock == null) {
			this.deadlock = new HashSet<Condition>();			
			OccurrenceNet BP = this.getOccurrenceNet();
			
			for (Place p : BP.getPlaces()) {
				if (BP.getPostset(p).isEmpty() && !this.sys.getPostset(BP.getCondition(p).getPlace()).isEmpty()) {
					this.deadlock.add(BP.getCondition(p));
				}
			}
						
			for (Place p : BP.getPlaces()) {
				for (Transition t : BP.getPostset(p)){					
					for (Place p1 : BP.getPreset(t)) {
						if (p.equals(p1)) continue;
						
						for (Place p2 : BP.getSinkPlaces()) {
							if (BP.getOrderingRelation(p,p2)==OrderingRelationType.CONFLICT && BP.getOrderingRelation(p1,p2)==OrderingRelationType.CONCURRENT) {
								this.deadlock.add(BP.getCondition(p));
							}
						}
					}
				}
			}
		}
		
		return this.deadlock;
	}
	
	*//**
	 * Check if the net is sound
	 * @return true if originative net is sound; otherwise false
	 *//*
	public boolean isSound() {
		return this.getLocallyUnsafeConditions().size()==0 && this.getLocalDeadlockConditions().size()==0;
	}*/
}
