package org.jbpt.bp.construct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jbpt.bp.BehaviouralProfile;
import org.jbpt.bp.RelSet;
import org.jbpt.bp.RelSetType;
import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;


/**
 * Computation of the behavioural profile for a given collection of 
 * transitions (or all transitions) of a sound free-choice WF-net using structural 
 * decomposition.
 * 
 * Note that this method cannot be used to create a behavioural profile for
 * all nodes of a net system. It is limited to behavioural profiles over transitions.
 * 
 * Soundness assumption is currently not checked!
 * 
 * Implemented as a singleton, use <code>getInstance()</code>.
 * 
 * @author Matthias Weidlich
 */
public class BPCreatorTree extends AbstractRelSetCreator implements RelSetCreator<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>,INode> {
	
	private static BPCreatorTree eInstance;
	
	public static BPCreatorTree getInstance() {
		if (eInstance == null)
			eInstance = new BPCreatorTree();
		return eInstance;
	}
	
	private BPCreatorTree() {}
	
	@Override
	public RelSet<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> deriveRelationSet(
			INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> pn) {
		return deriveRelationSet(pn, new ArrayList<INode>(pn.getTransitions()));
	}
	
	@Override
	public RelSet<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> deriveRelationSet(
			INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> pn,
			Collection<INode> nodes) {

		/*
		 * The construction of the WF-tree may augment the original net. Therefore,
		 * we clone the net and derive the WF-tree for the clone. We use a dedicated
		 * clone method that provides us with an according node mapping between the 
		 * original net and the clone.
		 */
		INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> netClone = null;
		Map<INode, INode> nodeMapping = new HashMap<>();
		netClone = pn.clone(nodeMapping);
		
		// Fall back to original net
		if (netClone == null) {
			netClone = pn;
			for (INode n : pn.getNodes())
				nodeMapping.put(n,n);
		}

		
		WFTreeHandler wfTreeHandler = new WFTreeHandler(netClone);
		
		BehaviouralProfile<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> profile = new BehaviouralProfile<>(pn,nodes);
		RelSetType[][] matrix = profile.getMatrix();

		for(INode t1 : profile.getEntities()) {
			int index1 = profile.getEntities().indexOf(t1);
			for(INode t2 : profile.getEntities()) {
				int index2 = profile.getEntities().indexOf(t2);
				/*
				 * The matrix is symmetric. Therefore, we need to traverse only 
				 * half of the entries.
				 */
				if (index2 > index1)
					continue;
				
				if (wfTreeHandler.areExclusive(nodeMapping.get(t1), nodeMapping.get(t2))) {
					super.setMatrixEntry(matrix, index1, index2, RelSetType.Exclusive);
				}
				else if (wfTreeHandler.areInterleaving(nodeMapping.get(t1), nodeMapping.get(t2))) {
					super.setMatrixEntry(matrix, index1, index2, RelSetType.Interleaving);
				}
				else if (wfTreeHandler.areInOrder(nodeMapping.get(t1), nodeMapping.get(t2))) {
					if (wfTreeHandler.areInStrictOrder(nodeMapping.get(t1), nodeMapping.get(t2)))
						super.setMatrixEntryOrder(matrix, index1, index2);
					else
						super.setMatrixEntryOrder(matrix, index2, index1);
				}
			}
		}
		
		return profile;
	}

}
