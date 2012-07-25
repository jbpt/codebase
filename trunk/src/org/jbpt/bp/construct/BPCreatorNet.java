package org.jbpt.bp.construct;

import java.util.Collection;

import org.jbpt.bp.BehaviouralProfile;
import org.jbpt.bp.RelSetType;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.behavior.ConcurrencyRelation;
import org.jbpt.petri.structure.PetriNetPathUtils;


/**
 * Computation of the behavioural profile for a given collection of 
 * nodes (or all nodes) of a sound free-choice WF-net.
 * 
 * Soundness assumption is currently not checked!
 * 
 * Implemented as a singleton, use <code>getInstance()</code>.
 * 
 * @author matthias.weidlich
 *
 */
public class BPCreatorNet extends AbstractRelSetCreator implements RelSetCreator<NetSystem, Node> {
	
	private static BPCreatorNet eInstance;
	
	public static BPCreatorNet getInstance() {
		if (eInstance == null)
			eInstance  = new BPCreatorNet();
		return eInstance;
	}
	
	private BPCreatorNet() {
		
	}

	public BehaviouralProfile<NetSystem, Node> deriveRelationSet(NetSystem pn) {
		return deriveRelationSet(pn, pn.getNodes());
	}
	
	public BehaviouralProfile<NetSystem, Node> deriveRelationSet(NetSystem pn, Collection<Node> nodes) {
		
		/*
		 * Check some of the assumptions.
		 */
		//if (!PetriNet.StructuralClassChecks.isExtendedFreeChoice(pn)) throw new IllegalArgumentException();
		if (!PetriNet.StructuralClassChecks.isWorkflowNet(pn)) throw new IllegalArgumentException();

		BehaviouralProfile<NetSystem, Node> profile = new BehaviouralProfile<NetSystem, Node>(pn,nodes);
		RelSetType[][] matrix = profile.getMatrix();
		
		ConcurrencyRelation concurrencyRelation = new ConcurrencyRelation(pn);
		
		for(Node n1 : profile.getEntities()) {
			int index1 = profile.getEntities().indexOf(n1);
			for(Node n2 : profile.getEntities()) {
				int index2 = profile.getEntities().indexOf(n2);
				/*
				 * The matrix is symmetric. Therefore, we need to traverse only 
				 * half of the entries.
				 */
				if (index2 > index1)
					continue;
				/*
				 * What about the relation of a node to itself?
				 */
				if (index1 == index2) {
					if (PetriNetPathUtils.hasDirectedNonEmptyPath(pn,n1,n2))
						matrix[index1][index1] = RelSetType.Interleaving;
					else
						matrix[index1][index1] = RelSetType.Exclusive;
				}
				/*
				 * Check all cases for two distinct nodes of the net
				 */
				else if (PetriNetPathUtils.hasDirectedNonEmptyPath(pn,n1,n2) && PetriNetPathUtils.hasDirectedNonEmptyPath(pn,n2,n1)) {
					super.setMatrixEntry(matrix,index1,index2,RelSetType.Interleaving);
				}
				else if (concurrencyRelation.areConcurrent(index1,index2)) {
					super.setMatrixEntry(matrix,index1,index2,RelSetType.Interleaving);
				}
				else if (!concurrencyRelation.areConcurrent(index1,index2) && !PetriNetPathUtils.hasDirectedNonEmptyPath(pn,n1,n2) && !PetriNetPathUtils.hasDirectedNonEmptyPath(pn,n2,n1)) {
					super.setMatrixEntry(matrix,index1,index2,RelSetType.Exclusive);
				}
				else if (PetriNetPathUtils.hasDirectedNonEmptyPath(pn,n1,n2) && !PetriNetPathUtils.hasDirectedNonEmptyPath(pn,n2,n1)) {
					super.setMatrixEntryOrder(matrix,index1,index2);
				}
				else if (PetriNetPathUtils.hasDirectedNonEmptyPath(pn,n2,n1) && !PetriNetPathUtils.hasDirectedNonEmptyPath(pn,n1,n2)) {
					super.setMatrixEntryOrder(matrix,index2,index1);
				}
			}
		}
		
		return profile;
	}
}
