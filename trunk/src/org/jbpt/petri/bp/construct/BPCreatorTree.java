package org.jbpt.petri.bp.construct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jbpt.petri.Node;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.bp.BehaviouralProfile;
import org.jbpt.petri.bp.RelSetType;
import org.jbpt.petri.wft.WFTree;


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
public class BPCreatorTree extends AbstractRelSetCreator implements RelSetCreator<PetriNet, Node> {
	
	private static BPCreatorTree eInstance;
	
	public static BPCreatorTree getInstance() {
		if (eInstance == null)
			eInstance  = new BPCreatorTree();
		return eInstance;
	}
	
	private BPCreatorTree() {
		
	}
	
	public BehaviouralProfile<PetriNet, Node> deriveRelationSet(PetriNet pn) {
		return deriveRelationSet(pn, new ArrayList<Node>(pn.getTransitions()));
	}
	
	public BehaviouralProfile<PetriNet, Node> deriveRelationSet(PetriNet pn, Collection<Node> nodes) {

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
		if (netClone == null) {
			netClone = pn;
			for (Node n : pn.getNodes())
				nodeMapping.put(n, n);
		}

		
		WFTree wfTree = new WFTree(netClone);
		
		BehaviouralProfile<PetriNet, Node> profile = new BehaviouralProfile<PetriNet, Node>(pn,nodes);
		RelSetType[][] matrix = profile.getMatrix();

		for(Node t1 : profile.getEntities()) {
			int index1 = profile.getEntities().indexOf(t1);
			for(Node t2 : profile.getEntities()) {
				int index2 = profile.getEntities().indexOf(t2);
				/*
				 * The matrix is symmetric. Therefore, we need to traverse only 
				 * half of the entries.
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
}
