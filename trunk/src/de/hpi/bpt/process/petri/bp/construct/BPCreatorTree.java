package de.hpi.bpt.process.petri.bp.construct;

import java.util.ArrayList;
import java.util.Collection;

import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.bp.BehaviouralProfile;
import de.hpi.bpt.process.petri.bp.BehaviouralProfile.CharacteristicRelationType;
import de.hpi.bpt.process.petri.wft.WFTree;

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
 * @author matthias.weidlich
 *
 */
public class BPCreatorTree extends AbstractBPCreator implements BPCreator {
	
	private static BPCreatorTree eInstance;
	
	public static BPCreatorTree getInstance() {
		if (eInstance == null)
			eInstance  = new BPCreatorTree();
		return eInstance;
	}
	
	private BPCreatorTree() {
		
	}
	
	public BehaviouralProfile deriveBehaviouralProfile(PetriNet pn) {
		return deriveBehaviouralProfile(pn, new ArrayList<Node>(pn.getTransitions()));
	}
	
	public BehaviouralProfile deriveBehaviouralProfile(PetriNet pn, Collection<Node> nodes) {

		WFTree wfTree = new WFTree(pn);
		
		BehaviouralProfile profile = new BehaviouralProfile(pn,nodes);
		CharacteristicRelationType[][] matrix = profile.getMatrix();

		for(Node t1 : profile.getNodes()) {
			int index1 = profile.getNodes().indexOf(t1);
			for(Node t2 : profile.getNodes()) {
				int index2 = profile.getNodes().indexOf(t2);
				/*
				 * The matrix is symmetric. Therefore, we need to traverse only 
				 * half of the entries.
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
}
