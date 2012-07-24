package org.jbpt.petri.unfolding;

import java.util.HashSet;
import java.util.Set;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;


/**
 * Proper complete prefix unfolding
 * 
 * Used for structuring
 *  
 * @author Artem Polyvyanyy
 */
public class ProperUnfolding extends Unfolding {
	
	protected ProperUnfolding(){}

	public ProperUnfolding(INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> sys) {
		super(sys);
	}
	
	public ProperUnfolding(INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> sys, UnfoldingSetup setup) {
		super(sys, setup);
	}

	/**
	 * Check healthy property (check cutoff extension)
	 */
	@Override
	protected Event checkCutoffB(Event e, Event corr) {
		Set<Condition> ecs = new HashSet<Condition>(e.getLocalConfiguration().getCut());
		Set<Condition> ccs = new HashSet<Condition>(corr.getLocalConfiguration().getCut());
		
		ecs.removeAll(e.getPostConditions());
		ccs.removeAll(corr.getPostConditions());
		
		if (ecs.equals(ccs)) return corr;
		
		return null;
	}
}
