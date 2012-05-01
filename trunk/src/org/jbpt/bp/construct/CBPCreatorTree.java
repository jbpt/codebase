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
import org.jbpt.petri.wft.WFTree;



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
		Map<Node, Node> nodeMapping = new HashMap<Node, Node>();
		try {
			netClone = (NetSystem) pn.clone(nodeMapping);
		} catch (CloneNotSupportedException e) {
			System.err.println("Clone not supported for NetSystem in CBPCreatorTree. Take original net.");
		}
		// Fall back to original net
		if (netClone == null) {
			netClone = pn;
			for (Node n : pn.getNodes())
				nodeMapping.put(n, n);
		}

		
		WFTree wfTree = new WFTree(netClone);
		
		CausalBehaviouralProfile<NetSystem, Node> profile = new CausalBehaviouralProfile<NetSystem, Node>(pn,nodes);
		RelSetType[][] matrix = profile.getMatrix();
		boolean[][] cooccurrenceMatrix = profile.getCooccurrenceMatrix();

		for(Node t1 : profile.getEntities()) {
			int index1 = profile.getEntities().indexOf(t1);
			for(Node t2 : profile.getEntities()) {
				int index2 = profile.getEntities().indexOf(t2);
				
				if (wfTree.areCooccurring(nodeMapping.get(t1), nodeMapping.get(t2)))
					cooccurrenceMatrix[index1][index2] = true;
				
				/*
				 * The behavioural profile matrix is symmetric. Therefore, we 
				 * need to traverse only half of the entries.
				 */
				if (index2 > index1)
					continue;
				
				if (wfTree.areExclusive(nodeMapping.get(t1), nodeMapping.get(t2))) {
					super.setMatrixEntry(matrix, index1, index2, RelSetType.Exclusive);
				}
				else if (wfTree.areInterleaving(nodeMapping.get(t1), nodeMapping.get(t2))) {
					super.setMatrixEntry(matrix, index1, index2, RelSetType.Interleaving);
				}
				else if (wfTree.areInOrder(nodeMapping.get(t1), nodeMapping.get(t2))) {
					if (wfTree.areInStrictOrder(nodeMapping.get(t1), nodeMapping.get(t2)))
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
		try {
			netClone = (NetSystem) pn.clone(nodeMapping);
		} catch (CloneNotSupportedException e) {
			System.err.println("Clone not supported for NetSystem in BPCreatorTree. Take original net.");
		}
		// Fall back to original net
		if (netClone == null)
			netClone = pn;
		
		WFTree wfTree = new WFTree(netClone);

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
				
				if (wfTree.areCooccurring(nodeMapping.get(t1), nodeMapping.get(t2)))
					cooccurrenceMatrix[index1][index2] = true;
			}
		}
	
		return profile;
	}

}
