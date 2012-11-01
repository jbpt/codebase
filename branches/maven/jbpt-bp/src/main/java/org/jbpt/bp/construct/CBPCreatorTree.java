package org.jbpt.bp.construct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jbpt.bp.BehaviouralProfile;
import org.jbpt.bp.CausalBehaviouralProfile;
import org.jbpt.bp.RelSetType;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;



public class CBPCreatorTree extends AbstractRelSetCreator implements CBPCreator<NetSystem, Node> {
	
	private static CBPCreatorTree eInstance;
	
	public static CBPCreatorTree getInstance() {
		if (eInstance == null)
			eInstance  = new CBPCreatorTree();
		return eInstance;
	}
	
	private CBPCreatorTree() {
		
	}
	
	public CausalBehaviouralProfile<NetSystem, Node> deriveCausalBehaviouralProfile(NetSystem pn) {
		return deriveCausalBehaviouralProfile(pn, new ArrayList<Node>(pn.getTransitions()));
	}
	
	public CausalBehaviouralProfile<NetSystem, Node> deriveCausalBehaviouralProfile(NetSystem pn, Collection<Node> nodes) {

		/*
		 * The construction of the WF-tree may augment the original net. Therefore,
		 * we clone the net and derive the WF-tree for the clone. We use a dedicated
		 * clone method that provides us with an according node mapping between the 
		 * original net and the clone.
		 */
		NetSystem netClone = null;
		Map<Node,Node> nodeMapping = new HashMap<Node, Node>();
		netClone = (NetSystem) pn.clone(nodeMapping);
	
		// Fall back to original net
		if (netClone == null) {
			netClone = pn;
			for (Node n : pn.getNodes())
				nodeMapping.put(n, n);
		}

		
		WFTreeHandler wfTreeHandler = new WFTreeHandler(netClone);
		
		CausalBehaviouralProfile<NetSystem, Node> profile = new CausalBehaviouralProfile<NetSystem, Node>(pn,nodes);
		RelSetType[][] matrix = profile.getMatrix();
		boolean[][] cooccurrenceMatrix = profile.getCooccurrenceMatrix();

		for(Node t1 : profile.getEntities()) {
			int index1 = profile.getEntities().indexOf(t1);
			for(Node t2 : profile.getEntities()) {
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

	public CausalBehaviouralProfile<NetSystem, Node> deriveCausalBehaviouralProfile(BehaviouralProfile<NetSystem, Node> bp) {
		
		NetSystem pn = bp.getModel();
		
		/*
		 * The construction of the WF-tree may augment the original net. Therefore,
		 * we clone the net and derive the WF-tree for the clone. We use a dedicated
		 * clone method that provides us with an according node mapping between the 
		 * original net and the clone.
		 */
		NetSystem netClone = null;
		Map<Node, Node> nodeMapping = new HashMap<Node, Node>();
		netClone = (NetSystem) pn.clone(nodeMapping);

		// Fall back to original net
		if (netClone == null)
			netClone = pn;
		
		WFTreeHandler wfTreeHandler = new WFTreeHandler(netClone);

		/*
		 * Get the behavioural profile
		 */
		CausalBehaviouralProfile<NetSystem, Node> profile = new CausalBehaviouralProfile<NetSystem, Node>(pn, bp.getEntities());
		profile.setMatrix(bp.getMatrix());	

		boolean[][] cooccurrenceMatrix = profile.getCooccurrenceMatrix();

		for(Node t1 : profile.getEntities()) {
			int index1 = profile.getEntities().indexOf(t1);
			for(Node t2 : profile.getEntities()) {
				int index2 = profile.getEntities().indexOf(t2);
				
				if (wfTreeHandler.areCooccurring(nodeMapping.get(t1), nodeMapping.get(t2)))
					cooccurrenceMatrix[index1][index2] = true;
			}
		}
	
		return profile;
	}

}
