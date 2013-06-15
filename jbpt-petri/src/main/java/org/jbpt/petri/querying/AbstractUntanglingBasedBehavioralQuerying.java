package org.jbpt.petri.querying;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;
import org.jbpt.petri.unfolding.IBPNode;
import org.jbpt.petri.unfolding.ICondition;
import org.jbpt.petri.unfolding.IEvent;
import org.jbpt.petri.untangling.AbstractReductionBasedRepresentativeUntangling;
import org.jbpt.petri.untangling.IProcess;
import org.jbpt.petri.untangling.SignificanceCheckType;
import org.jbpt.petri.untangling.UntanglingSetup;

/**
 * An implementation of the {@link IBehavioralQuerying} interface (using untanglings).
 *
 * @author Artem Polyvyanyy
 */
public class AbstractUntanglingBasedBehavioralQuerying<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>>
	extends AbstractStructuralQuerying<BPN,C,E,F,N,P,T,M>
	implements IBehavioralQuerying<F,N,P,T,M> {

	protected Collection<IProcess<BPN,C,E,F,N,P,T,M>> untangling = null;	
	private AbstractReductionBasedRepresentativeUntangling<BPN,C,E,F,N,P,T,M> repUnt = null;
	
	public AbstractUntanglingBasedBehavioralQuerying(INetSystem<F,N,P,T,M> sys) {
		super(sys);
		
		UntanglingSetup setup = new UntanglingSetup();
		setup.REDUCE = false;
		setup.ISOMORPHISM_REDUCTION = false;
		setup.SIGNIFICANCE_CHECK = SignificanceCheckType.TREE_OF_RUNS;
		
		this.repUnt = new AbstractReductionBasedRepresentativeUntangling<>(this.sys,setup);
		this.untangling = repUnt.getProcesses();
		
	}
	
	public AbstractUntanglingBasedBehavioralQuerying(INetSystem<F,N,P,T,M> sys, Collection<IProcess<BPN,C,E,F,N,P,T,M>> untangling) {
		super(sys);
		
		this.untangling=untangling;
	}
	
	@Override
	public boolean canOccurAll(Set<String> setOfLabels) { 
		if (setOfLabels==null) return true;
		if (setOfLabels.isEmpty()) return true;
		
		for (IProcess<BPN,C,E,F,N,P,T,M> pi : this.untangling) {
			Set<String> sol = new HashSet<String>(setOfLabels);
			for (E e : pi.getEvents()) {
				if (sol.remove(e.getTransition().getLabel()))
					if (sol.isEmpty()) return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean canOccurAllExpanded(Set<Set<String>> setsOfLabels) {
		if (setsOfLabels==null) return true;
		if (setsOfLabels.isEmpty()) return true;
		
		for (IProcess<BPN,C,E,F,N,P,T,M> pi : this.untangling) {
			Set<Set<String>> sol = new HashSet<Set<String>>();
			for (E e : pi.getEvents()) {
				for (Set<String> setOfLabels : setsOfLabels) {
					if (setOfLabels.contains(e.getTransition().getLabel())) {
						if (sol.add(setOfLabels))
							if (setsOfLabels.size()==sol.size())
								return true;
					}
				}
			}
		}
		
		return false;
	}
	
	@Override
	public boolean canOccurOne(Set<String> setOfLabels) {
		if (setOfLabels==null) return true;
		if (setOfLabels.isEmpty()) return true;
		
		for (IProcess<BPN,C,E,F,N,P,T,M> pi : this.untangling) {
			for (E e : pi.getEvents()) {
				if (setOfLabels.contains(e.getTransition().getLabel()))
					return true;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean canOccurOneExpanded(Set<Set<String>> setsOfLabels) {
		Set<String> sol = new HashSet<>();

		for (Set<String> s : setsOfLabels)
			sol.addAll(s);

		return this.cannotOccurOne(sol);
	}

	@Override
	public boolean cannotOccurAll(Set<String> setOfLabels) {
		return !this.canOccurAll(setOfLabels);
	}

	@Override
	public boolean cannotOccurOne(Set<String> setOfLabels) {
		return !this.canOccurOne(setOfLabels);
	}

	@Override
	public boolean cannotOccurAllExpanded(Set<Set<String>> setsOfLabels) {
		return !this.canOccurAllExpanded(setsOfLabels);
	}

	@Override
	public boolean cannotOccurOneExpanded(Set<Set<String>> setsOfLabels) {
		return !this.canOccurOneExpanded(setsOfLabels);
	}
}
