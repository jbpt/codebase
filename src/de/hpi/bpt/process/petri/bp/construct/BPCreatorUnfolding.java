package de.hpi.bpt.process.petri.bp.construct;

import hub.top.uma.DNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Transition;
import de.hpi.bpt.process.petri.bp.BehaviouralProfile;
import de.hpi.bpt.process.petri.bp.BehaviouralProfile.CharacteristicRelationType;
import de.hpi.bpt.process.petri.rels.UnfoldingRelationType;


public class BPCreatorUnfolding extends AbstractBPCreator implements BPCreator {

	private static BPCreatorUnfolding eInstance;
	
	public static BPCreatorUnfolding getInstance() {
		if (eInstance == null)
			eInstance  = new BPCreatorUnfolding();
		return eInstance;
	}
	
	private BPCreatorUnfolding() {
		
	}

	protected EventContinuationProfiler eventContinuationProfiler;
	
	protected Map<DNode, Transition> unfoldingNodesToNetTransitions = new HashMap<DNode, Transition>(); 
	protected boolean[][] weakOrderMatrixForTransitions; 
	protected List<Transition> transitionsForWeakOrderMatrix;
			
	protected void clear() {

		eventContinuationProfiler = null;
		
		unfoldingNodesToNetTransitions = new HashMap<DNode, Transition>(); 
		weakOrderMatrixForTransitions = null; 
		transitionsForWeakOrderMatrix = new ArrayList<Transition>();
	}

	@Override
	public BehaviouralProfile deriveBehaviouralProfile(PetriNet pn) {
		return deriveBehaviouralProfile(pn, new ArrayList<Node>(pn.getTransitions()));
	}
	
	@Override
	public BehaviouralProfile deriveBehaviouralProfile(PetriNet pn,
			Collection<Node> nodes) {
		
		clear();
		
		this.eventContinuationProfiler = new EventContinuationProfiler(pn);
		
		BehaviouralProfile profile = new BehaviouralProfile(pn,nodes);
		CharacteristicRelationType[][] matrix = profile.getMatrix();
		
		for (Node t : nodes)
			if (t instanceof Transition)
				this.transitionsForWeakOrderMatrix.add((Transition)t);
		
		for (DNode n : this.eventContinuationProfiler.getUnfolding().getBranchingProcess().getAllEvents()) 
			for (Transition t : this.transitionsForWeakOrderMatrix) 
				if (t.getId().equals(this.eventContinuationProfiler.getUnfolding().getSystem().properNames[n.id]))
					this.unfoldingNodesToNetTransitions.put(n,t);

		this.deriveWeakOrderRelation();

		for(Node t1 : profile.getNodes()) {
			int index1 = profile.getNodes().indexOf(t1);
			for(Node t2 : profile.getNodes()) {
				int index2 = profile.getNodes().indexOf(t2);
				
				/*
				 * The behavioural profile matrix is symmetric. Therefore, we 
				 * need to traverse only half of the entries.
				 */
				if (index2 > index1)
					continue;
				
				if (this.isWeakOrder(t1,t2) && this.isWeakOrder(t2,t1))
					super.setMatrixEntry(matrix, index1, index2, CharacteristicRelationType.InterleavingOrder);
				else if (this.isWeakOrder(t1,t2))
					super.setMatrixEntryOrder(matrix, index1, index2);
				else if (this.isWeakOrder(t2,t1))
					super.setMatrixEntryOrder(matrix, index2, index1);
				else
					super.setMatrixEntry(matrix, index1, index2, CharacteristicRelationType.Exclusive);
			}
		}		
		
		return profile;
	}
		
	protected void deriveWeakOrderRelation() {
		
		weakOrderMatrixForTransitions = new boolean[this.transitionsForWeakOrderMatrix.size()][this.transitionsForWeakOrderMatrix.size()];
		
		for (DNode e1 : this.eventContinuationProfiler.getUnfolding().getBranchingProcess().getAllEvents()) {
			for (DNode e2 : this.eventContinuationProfiler.getUnfolding().getBranchingProcess().getAllEvents()) {
				if (this.eventContinuationProfiler.getRelation(e1,e2).equals(UnfoldingRelationType.CAUSAL)
						|| (!e1.equals(e2) && this.eventContinuationProfiler.getRelation(e1,e2).equals(UnfoldingRelationType.CONCURRENCY))) {
					addToRelation(weakOrderMatrixForTransitions, unfoldingNodesToNetTransitions.get(e1), unfoldingNodesToNetTransitions.get(e2));
				}
				else if (this.eventContinuationProfiler.isCausalViaSequenceOfCutOffs(e1,e2)){
					addToRelation(weakOrderMatrixForTransitions, unfoldingNodesToNetTransitions.get(e1), unfoldingNodesToNetTransitions.get(e2));
				}
			}
		}
	}
	
	private boolean isWeakOrder(Node n1, Node n2) {
		return weakOrderMatrixForTransitions[this.transitionsForWeakOrderMatrix.indexOf(n1)][this.transitionsForWeakOrderMatrix.indexOf(n2)];
	}
	

	private void addToRelation(boolean[][] matrix, Node n1, Node n2) {
		matrix[this.transitionsForWeakOrderMatrix.indexOf(n1)][this.transitionsForWeakOrderMatrix.indexOf(n2)] = true;
	}

}
