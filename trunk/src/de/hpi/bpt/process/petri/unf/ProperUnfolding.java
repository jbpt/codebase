package de.hpi.bpt.process.petri.unf;

import java.util.HashSet;
import java.util.Set;

import de.hpi.bpt.process.petri.PetriNet;

/**
 * Proper complete prefix unfolding
 * 
 * Used for structuring
 *  
 * @author Artem Polyvyanyy
 */
public class ProperUnfolding extends Unfolding {
	
	protected ProperUnfolding() {}

	public ProperUnfolding(PetriNet pn) {
		super(pn);
	}
	
	public ProperUnfolding(PetriNet pn, UnfoldingSetup setup) {
		super(pn, setup);
	}

	/**
	 * Check healthy property (check cutoff extension)
	 */
	@Override
	protected Event checkCutoffExt(Event e, Event corr) {
		Set<Condition> ecs = new HashSet<Condition>(e.getLocalConfiguration().getCut());
		Set<Condition> ccs = new HashSet<Condition>(corr.getLocalConfiguration().getCut());
		
		ecs.removeAll(e.getPostConditions());
		ccs.removeAll(corr.getPostConditions());
		
		if (ecs.equals(ccs)) return corr;
		
		return null;
	}
}
