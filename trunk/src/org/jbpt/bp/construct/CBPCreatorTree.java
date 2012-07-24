package org.jbpt.bp.construct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jbpt.bp.BehaviouralProfile;
import org.jbpt.bp.CausalBehaviouralProfile;
import org.jbpt.bp.RelSetType;
import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;



public class CBPCreatorTree extends AbstractRelSetCreator implements CBPCreator<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> {
	
	private static CBPCreatorTree eInstance;
	
	public static CBPCreatorTree getInstance() {
		if (eInstance == null)
			eInstance  = new CBPCreatorTree();
		return eInstance;
	}
	
	private CBPCreatorTree() {
		
	}
	
	@Override
	public CausalBehaviouralProfile<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> deriveCausalBehaviouralProfile(
			INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> pn) {
		return deriveCausalBehaviouralProfile(pn, new ArrayList<INode>(pn.getTransitions()));
	}
	
	@Override
	public CausalBehaviouralProfile<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> deriveCausalBehaviouralProfile(
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
				nodeMapping.put(n, n);
		}

		
		WFTreeHandler wfTreeHandler = new WFTreeHandler(netClone);
		
		CausalBehaviouralProfile<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> profile = new CausalBehaviouralProfile<>(pn,nodes);
		RelSetType[][] matrix = profile.getMatrix();
		boolean[][] cooccurrenceMatrix = profile.getCooccurrenceMatrix();

		for(INode t1 : profile.getEntities()) {
			int index1 = profile.getEntities().indexOf(t1);
			for(INode t2 : profile.getEntities()) {
				int index2 = profile.getEntities().indexOf(t2);
				
				if (wfTreeHandler.areCooccurring(nodeMapping.get(t1), nodeMapping.get(t2)))
					cooccurrenceMatrix[index1][index2] = true;
				
				/*
				 * The behavioural profile matrix is symmetric. Therefore, we 
				 * need to traverse only half of the entries.
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

	@Override
	public CausalBehaviouralProfile<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> deriveCausalBehaviouralProfile(
			BehaviouralProfile<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> bp) {
		
		INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> pn = bp.getModel();
		
		/*
		 * The construction of the WF-tree may augment the original net. Therefore,
		 * we clone the net and derive the WF-tree for the clone. We use a dedicated
		 * clone method that provides us with an according node mapping between the 
		 * original net and the clone.
		 */
		INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> netClone = null;
		Map<INode, INode> nodeMapping = new HashMap<INode, INode>();
		netClone = pn.clone(nodeMapping);

		// Fall back to original net
		if (netClone == null)
			netClone = pn;
		
		WFTreeHandler wfTreeHandler = new WFTreeHandler(netClone);

		/*
		 * Get the behavioural profile
		 */
		CausalBehaviouralProfile<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> profile = new CausalBehaviouralProfile<>(pn, bp.getEntities());
		profile.setMatrix(bp.getMatrix());	

		boolean[][] cooccurrenceMatrix = profile.getCooccurrenceMatrix();

		for(INode t1 : profile.getEntities()) {
			int index1 = profile.getEntities().indexOf(t1);
			for(INode t2 : profile.getEntities()) {
				int index2 = profile.getEntities().indexOf(t2);
				
				if (wfTreeHandler.areCooccurring(nodeMapping.get(t1), nodeMapping.get(t2)))
					cooccurrenceMatrix[index1][index2] = true;
			}
		}
	
		return profile;
	}



}
