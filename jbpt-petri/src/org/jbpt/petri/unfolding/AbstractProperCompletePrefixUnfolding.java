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
 * Proper complete prefix unfolding.
 *  
 * @author Artem Polyvyanyy
 */
public abstract class AbstractProperCompletePrefixUnfolding<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> 
		extends AbstractCompletePrefixUnfolding<BPN,C,E,F,N,P,T,M>
{
	public AbstractProperCompletePrefixUnfolding() {
		super();
	}

	public AbstractProperCompletePrefixUnfolding(INetSystem<F,N,P,T,M> sys, CompletePrefixUnfoldingSetup setup) {
		super(sys, setup);
	}

	public AbstractProperCompletePrefixUnfolding(INetSystem<F,N,P,T,M> sys) {
		super(sys);
	}

	/**
	 * Check healthy property
	 */
	@Override
	protected E checkCutoffB(E e, E corr) {
		Set<C> ecs = new HashSet<C>(e.getLocalConfiguration().getCut());
		Set<C> ccs = new HashSet<C>(corr.getLocalConfiguration().getCut());
		
		ecs.removeAll(e.getPostConditions());
		ccs.removeAll(corr.getPostConditions());
		
		if (ecs.equals(ccs)) return corr;
		
		return null;
	}
}
