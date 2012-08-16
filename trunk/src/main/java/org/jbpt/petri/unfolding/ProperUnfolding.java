package org.jbpt.petri.unfolding;



/**
 * Proper complete prefix unfolding
 * 
 * Used for structuring
 *  
 * @author Artem Polyvyanyy
 */
public class ProperUnfolding extends AbstractCompletePrefixUnfolding {
	
	/*protected ProperUnfolding(){}

	public ProperUnfolding(NetSystem sys) {
		super(sys);
	}
	
	public ProperUnfolding(NetSystem sys, CompletePrefixUnfoldingSetup setup) {
		super(sys, setup);
	}

	*//**
	 * Check healthy property (check cutoff extension)
	 *//*
	@Override
	protected Event checkCutoffB(IEvent e, Event corr) {
		Set<Condition> ecs = new HashSet<Condition>(e.getLocalConfiguration().getCut());
		Set<Condition> ccs = new HashSet<Condition>(corr.getLocalConfiguration().getCut());
		
		ecs.removeAll(e.getPostConditions());
		ccs.removeAll(corr.getPostConditions());
		
		if (ecs.equals(ccs)) return corr;
		
		return null;
	}*/
}
