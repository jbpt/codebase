package org.jbpt.bp.construct;

import java.util.Collection;

import org.jbpt.bp.BehaviouralProfile;
import org.jbpt.bp.RelSet;
import org.jbpt.bp.RelSetType;
import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;
import org.jbpt.petri.behavior.ConcurrencyRelation;
import org.jbpt.petri.structure.PetriNetPathUtils;
import org.jbpt.petri.structure.PetriNetStructuralClassChecks;


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
public class BPCreatorNet extends AbstractRelSetCreator implements RelSetCreator<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> {
	
	private static BPCreatorNet eInstance;
	
	public static BPCreatorNet getInstance() {
		if (eInstance == null)
			eInstance  = new BPCreatorNet();
		return eInstance;
	}
	
	private BPCreatorNet() {
		
	}

	@Override
	public RelSet<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> deriveRelationSet(
			INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> model) {
		return deriveRelationSet(model, model.getNodes());
	}
	
	@Override
	public RelSet<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> deriveRelationSet(
			INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> model,
			Collection<INode> entities) {
		
		/*
		 * Check some of the assumptions.
		 */
		if (!PetriNetStructuralClassChecks.isExtendedFreeChoice(model)) throw new IllegalArgumentException();
		if (!PetriNetStructuralClassChecks.isWorkflowNet(model)) throw new IllegalArgumentException();

		BehaviouralProfile<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> profile = new BehaviouralProfile<>(model,entities);
		RelSetType[][] matrix = profile.getMatrix();
		
		ConcurrencyRelation concurrencyRelation = new ConcurrencyRelation(model);
		
		for(INode n1 : profile.getEntities()) {
			int index1 = profile.getEntities().indexOf(n1);
			for(INode n2 : profile.getEntities()) {
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
					if (PetriNetPathUtils.hasDirectedNonEmptyPath(model,n1,n2))
						matrix[index1][index1] = RelSetType.Interleaving;
					else
						matrix[index1][index1] = RelSetType.Exclusive;
				}
				/*
				 * Check all cases for two distinct nodes of the net
				 */
				else if (PetriNetPathUtils.hasDirectedNonEmptyPath(model,n1,n2) && PetriNetPathUtils.hasDirectedNonEmptyPath(model,n2,n1)) {
					super.setMatrixEntry(matrix,index1,index2,RelSetType.Interleaving);
				}
				else if (concurrencyRelation.areConcurrent(index1,index2)) {
					super.setMatrixEntry(matrix,index1,index2,RelSetType.Interleaving);
				}
				else if (!concurrencyRelation.areConcurrent(index1,index2) && !PetriNetPathUtils.hasDirectedNonEmptyPath(model,n1,n2) && !PetriNetPathUtils.hasDirectedNonEmptyPath(model,n2,n1)) {
					super.setMatrixEntry(matrix,index1,index2,RelSetType.Exclusive);
				}
				else if (PetriNetPathUtils.hasDirectedNonEmptyPath(model,n1,n2) && !PetriNetPathUtils.hasDirectedNonEmptyPath(model,n2,n1)) {
					super.setMatrixEntryOrder(matrix,index1,index2);
				}
				else if (PetriNetPathUtils.hasDirectedNonEmptyPath(model,n2,n1) && !PetriNetPathUtils.hasDirectedNonEmptyPath(model,n1,n2)) {
					super.setMatrixEntryOrder(matrix,index2,index1);
				}
			}
		}
		
		return profile;
	}



}
