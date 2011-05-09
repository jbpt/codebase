package de.hpi.bpt.process.petri.bp.construct;

import java.util.ArrayList;
import java.util.Collection;

import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.bp.BehaviouralProfile;
import de.hpi.bpt.process.petri.bp.CausalBehaviouralProfile;
import de.hpi.bpt.process.petri.bp.BehaviouralProfile.CharacteristicRelationType;
import de.hpi.bpt.process.petri.wft.WFTree;


public class CBPCreatorTree extends AbstractBPCreator implements CBPCreator {
	
	private static CBPCreatorTree eInstance;
	
	public static CBPCreatorTree getInstance() {
		if (eInstance == null)
			eInstance  = new CBPCreatorTree();
		return eInstance;
	}
	
	private CBPCreatorTree() {
		
	}
	
	public CausalBehaviouralProfile deriveCausalBehaviouralProfile(PetriNet pn) {
		return deriveCausalBehaviouralProfile(pn, new ArrayList<Node>(pn.getTransitions()));
	}
	
	public CausalBehaviouralProfile deriveCausalBehaviouralProfile(PetriNet pn, Collection<Node> nodes) {

		WFTree wfTree = new WFTree(pn);
		
		CausalBehaviouralProfile profile = new CausalBehaviouralProfile(pn,nodes);
		CharacteristicRelationType[][] matrix = profile.getMatrix();
		boolean[][] cooccurrenceMatrix = profile.getCooccurrenceMatrix();

		for(Node t1 : profile.getNodes()) {
			int index1 = profile.getNodes().indexOf(t1);
			for(Node t2 : profile.getNodes()) {
				int index2 = profile.getNodes().indexOf(t2);
				
				if (wfTree.areCooccurring(t1, t2))
					cooccurrenceMatrix[index1][index2] = true;
				
				/*
				 * The behavioural profile matrix is symmetric. Therefore, we 
				 * need to traverse only half of the entries.
				 */
				if (index2 > index1)
					continue;
				
				if (wfTree.areExclusive(t1, t2)) {
					super.setMatrixEntry(matrix, index1, index2, CharacteristicRelationType.Exclusive);
				}
				else if (wfTree.areInterleaving(t1, t2)) {
					super.setMatrixEntry(matrix, index1, index2, CharacteristicRelationType.InterleavingOrder);
				}
				else if (wfTree.areInOrder(t1, t2)) {
					if (wfTree.areInStrictOrder(t1, t2))
						super.setMatrixEntryOrder(matrix, index1, index2);
					else
						super.setMatrixEntryOrder(matrix, index2, index1);
				}
			}
		}		
		
		
		return profile;
	}

	public CausalBehaviouralProfile deriveCausalBehaviouralProfile(BehaviouralProfile bp) {
		
		PetriNet pn = bp.getNet();

		/*
		 * Get the behavioural profile
		 */
		CausalBehaviouralProfile profile = new CausalBehaviouralProfile(pn, bp.getNodes());
		profile.setMatrix(bp.getMatrix());	

		WFTree wfTree = new WFTree(pn);

		boolean[][] cooccurrenceMatrix = profile.getCooccurrenceMatrix();

		for(Node t1 : profile.getNodes()) {
			int index1 = profile.getNodes().indexOf(t1);
			for(Node t2 : profile.getNodes()) {
				int index2 = profile.getNodes().indexOf(t2);
				
				if (wfTree.areCooccurring(t1, t2))
					cooccurrenceMatrix[index1][index2] = true;
			}
		}
	
		return profile;
	}

}
