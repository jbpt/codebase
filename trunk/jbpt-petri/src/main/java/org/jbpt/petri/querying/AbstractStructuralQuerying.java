package org.jbpt.petri.querying;

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

/**
 * An implementation of the {@link IStructuralQuerying} interface.
 * 
 * @author Artem Polyvyanyy
 */
public class AbstractStructuralQuerying<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> 
	implements IStructuralQuerying<F,N,P,T,M> {
	
	protected INetSystem<F,N,P,T,M> sys = null;
	
	public AbstractStructuralQuerying(INetSystem<F,N,P,T,M> sys) {
		if (sys==null) return;
		
		this.sys = sys;
	}
	
	public INetSystem<F,N,P,T,M> getNetSystem() {
		return this.sys;
	}

	@Override
	public boolean areModeledAll(Set<String> setOfLabels) {
		if (setOfLabels==null) return true;
		if (setOfLabels.isEmpty()) return true;
		
		Set<String> sol = new HashSet<>(setOfLabels);
		for (T t : this.sys.getTransitions()) {
			if (sol.remove(t.getLabel())) {
				if (sol.isEmpty())
					return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean isModeledOne(Set<String> setOfLabels) {
		if (setOfLabels==null) return true;
		if (setOfLabels.isEmpty()) return true;
		
		for (T t : this.sys.getTransitions()) {
			if (setOfLabels.contains(t.getLabel()))
				return true;
		}
		
		return false;
	}

	@Override
	public boolean areModeledAllExpanded(Set<Set<String>> setsOfLabels) {
		if (setsOfLabels==null) return true;
		if (setsOfLabels.isEmpty()) return true;
		
		Set<Set<String>> ssol = new HashSet<>();
		for (T t : this.sys.getTransitions()) {
			for (Set<String> sol : setsOfLabels) {
				if (sol.contains(t.getLabel())) {
					ssol.add(sol);
					if (ssol.size()==setsOfLabels.size()) return true;
				}
			}
		}
		
		return false;
	}

}
