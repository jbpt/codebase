package de.hpi.bpt.process.petri.unf;

import java.util.HashSet;
import java.util.Set;

import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.unf.order.EsparzaTotalAdequateOrderForSafeSystems;

/**
 * Proper complete prefix unfolding
 * 
 * Used for structuring
 *  
 * @author Artem Polyvyanyy
 */
public class ProperUnfolding extends Unfolding {
	
	protected static UnfoldingSetup properSetup = new UnfoldingSetup(); 
	
	static {
		ProperUnfolding.properSetup.ADEQUATE_ORDER = new EsparzaTotalAdequateOrderForSafeSystems();		
	}

	protected ProperUnfolding() {}
	
	public ProperUnfolding(PetriNet pn) {	
		super(pn,ProperUnfolding.properSetup);
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
