package de.hpi.bpt.process.petri.bp.construct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.bp.BehaviouralProfile;
import de.hpi.bpt.process.petri.bp.CausalBehaviouralProfile;
import de.hpi.bpt.process.petri.bp.RelSetType;
import de.hpi.bpt.process.petri.wft.WFTree;


public class CBPCreatorTree extends AbstractRelSetCreator implements CBPCreator<PetriNet, Node> {
	
	private static CBPCreatorTree eInstance;
	
	public static CBPCreatorTree getInstance() {
		if (eInstance == null)
			eInstance  = new CBPCreatorTree();
		return eInstance;
	}
	
	private CBPCreatorTree() {
		
	}
	
	public CausalBehaviouralProfile<PetriNet, Node> deriveCausalBehaviouralProfile(PetriNet pn) {
		return deriveCausalBehaviouralProfile(pn, new ArrayList<Node>(pn.getTransitions()));
	}
	
	public CausalBehaviouralProfile<PetriNet, Node> deriveCausalBehaviouralProfile(PetriNet pn, Collection<Node> nodes) {

		/*
		 * The construction of the WF-tree may augment the original net. Therefore,
		 * we clone the net and derive the WF-tree for the clone. We use a dedicated
		 * clone method that provides us with an according node mapping between the 
		 * original net and the clone.
		 */
		PetriNet netClone = null;
		Map<Node, Node> nodeMapping = new HashMap<Node, Node>();
		try {
			netClone = (PetriNet) pn.clone(nodeMapping);
		} catch (CloneNotSupportedException e) {
			System.err.println("Clone not supported for PetriNet in CBPCreatorTree. Take original net.");
		}
		// Fall back to original net
		if (netClone == null) {
			netClone = pn;
			for (Node n : pn.getNodes())
				nodeMapping.put(n, n);
		}

		
		WFTree wfTree = new WFTree(netClone);
		
		CausalBehaviouralProfile<PetriNet, Node> profile = new CausalBehaviouralProfile<PetriNet, Node>(pn,nodes);
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

	public CausalBehaviouralProfile<PetriNet, Node> deriveCausalBehaviouralProfile(BehaviouralProfile<PetriNet, Node> bp) {
		
		PetriNet pn = bp.getModel();
		
		/*
		 * The construction of the WF-tree may augment the original net. Therefore,
		 * we clone the net and derive the WF-tree for the clone. We use a dedicated
		 * clone method that provides us with an according node mapping between the 
		 * original net and the clone.
		 */
		PetriNet netClone = null;
		Map<Node, Node> nodeMapping = new HashMap<Node, Node>();
		try {
			netClone = (PetriNet) pn.clone(nodeMapping);
		} catch (CloneNotSupportedException e) {
			System.err.println("Clone not supported for PetriNet in BPCreatorTree. Take original net.");
		}
		// Fall back to original net
		if (netClone == null)
			netClone = pn;
		
		WFTree wfTree = new WFTree(netClone);

		/*
		 * Get the behavioural profile
		 */
		CausalBehaviouralProfile<PetriNet, Node> profile = new CausalBehaviouralProfile<PetriNet, Node>(pn, bp.getEntities());
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
