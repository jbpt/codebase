package org.jbpt.petri.querying;

import java.util.Set;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;

public interface IBehavioralQuerying<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> {
	
	/**
	 * Check if there exists an occurrence sequence in the net system that for every label x in a given set of labels contains a transition labeled with x.
	 * 
	 * @param setOfLabels Set of labels.
	 * @return <tt>true</tt> if there exists an occurrence sequence in the net system that for every label x in 'setOfLabels' contains a transition labeled with x; otherwise <tt>false</tt>.
	 */
	public boolean canOccurAll(Set<String> setOfLabels);
	
	public boolean cannotOccurAll(Set<String> setOfLabels);
	
	public boolean canOccurOne(Set<String> setOfLabels);
	
	public boolean cannotOccurOne(Set<String> setOfLabels);
	
	public boolean canOccurAllExpanded(Set<Set<String>> setsOfLabels);
	
	public boolean cannotOccurAllExpanded(Set<Set<String>> setsOfLabels);
	
	public boolean canOccurOneExpanded(Set<Set<String>> setsOfLabels);
	
	public boolean cannotOccurOneExpanded(Set<Set<String>> setsOfLabels);
}
