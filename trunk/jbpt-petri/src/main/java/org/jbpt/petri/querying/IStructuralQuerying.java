package org.jbpt.petri.querying;

import java.util.Set;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;

public interface IStructuralQuerying<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> {
	
	public boolean areModeledAll(Set<String> setOfLabels);
	
	public boolean areModeledAllExpanded(Set<Set<String>> setsOfLabels);
	
	public boolean isModeledOne(Set<String> setOfLabels);
}
