package org.jbpt.bp.construct.uma;

import hub.top.uma.DNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpt.bp.BehaviouralProfile;
import org.jbpt.bp.RelSetType;
import org.jbpt.bp.construct.AbstractRelSetCreator;
import org.jbpt.bp.construct.RelSetCreator;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;
import org.jbpt.petri.Transition;


/**
 * Computation of the behavioural profile for a given collection of 
 * transitions (or all transitions) of a bounded net system using its complete
 * prefix unfolding. To derive the complete prefix unfolding we rely on 
 * UMA.
 * 
 * Note that boundedness is not checked explicitly. If this class is
 * used for unbounded nets, it will still return a behavioural profile
 * as a result since UMA has a fixed boundary for concurrent conditions
 * that relate to the same place in the unfolding. Hence, UMA stops
 * even if there does not exist a finite prefix. However, it is not 
 * guaranteed that the obtained behavioural profile is correct in this
 * case!
 * 
 * Implemented as a singleton, use <code>getInstance()</code>.
 * 
 * @author matthias.weidlich
 *
 */
public class UMABPCreatorUnfolding extends AbstractRelSetCreator implements RelSetCreator<NetSystem, Node> {

	private static UMABPCreatorUnfolding eInstance;
	
	public static UMABPCreatorUnfolding getInstance() {
		if (eInstance == null)
			eInstance  = new UMABPCreatorUnfolding();
		return eInstance;
	}
	
	private UMABPCreatorUnfolding() {
		
	}
	
	// needed to extract the relations of events in the unfolding
	protected EventContinuationProfiler eventContinuationProfiler;
	
	// captures relation between unfolding and original net
	protected Map<DNode, Transition> unfoldingNodesToNetTransitions = new HashMap<DNode, Transition>(); 
	
	// captures the weak order for transitions
	protected boolean[][] weakOrderMatrixForTransitions; 
	
	// list to have identifiers for the transitions in the matrix
	protected List<Transition> transitionsForWeakOrderMatrix;
	
	protected void clear() {
		eventContinuationProfiler = null;
		unfoldingNodesToNetTransitions = new HashMap<DNode, Transition>(); 
		weakOrderMatrixForTransitions = null; 
		transitionsForWeakOrderMatrix = new ArrayList<Transition>();
	}

	@Override
	public BehaviouralProfile<NetSystem, Node> deriveRelationSet(NetSystem pn) {
		return deriveRelationSet(pn, new ArrayList<Node>(pn.getTransitions()));
	}
	
	@Override
	public BehaviouralProfile<NetSystem, Node> deriveRelationSet(NetSystem pn,
			Collection<Node> nodes) {
		
		clear();
		
		this.eventContinuationProfiler = new EventContinuationProfiler(pn);
		
		BehaviouralProfile<NetSystem, Node> profile = new BehaviouralProfile<NetSystem, Node>(pn,nodes);
		RelSetType[][] matrix = profile.getMatrix();
		
		for (Node t : nodes)
			if (t instanceof Transition)
				this.transitionsForWeakOrderMatrix.add((Transition)t);
		
		for (DNode n : this.eventContinuationProfiler.getUnfolding().getBranchingProcess().getAllEvents()) 
			for (Transition t : this.transitionsForWeakOrderMatrix) 
				if (t.getId().equals(this.eventContinuationProfiler.getUnfolding().getSystem().properNames[n.id]))
					this.unfoldingNodesToNetTransitions.put(n,t);

		this.deriveWeakOrderRelation();

		for(Node t1 : profile.getEntities()) {
			int index1 = profile.getEntities().indexOf(t1);
			for(Node t2 : profile.getEntities()) {
				int index2 = profile.getEntities().indexOf(t2);
				
				/*
				 * The behavioural profile matrix is symmetric. Therefore, we 
				 * need to traverse only half of the entries.
				 */
				if (index2 > index1)
					continue;
				
				if (this.isWeakOrder(t1,t2) && this.isWeakOrder(t2,t1))
					super.setMatrixEntry(matrix, index1, index2, RelSetType.Interleaving);
				else if (this.isWeakOrder(t1,t2))
					super.setMatrixEntryOrder(matrix, index1, index2);
				else if (this.isWeakOrder(t2,t1))
					super.setMatrixEntryOrder(matrix, index2, index1);
				else
					super.setMatrixEntry(matrix, index1, index2, RelSetType.Exclusive);
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
					if (unfoldingNodesToNetTransitions.containsKey(e1) && unfoldingNodesToNetTransitions.containsKey(e2))
						addToRelation(weakOrderMatrixForTransitions, unfoldingNodesToNetTransitions.get(e1), unfoldingNodesToNetTransitions.get(e2));
				}
				else if (this.eventContinuationProfiler.isCausalViaSequenceOfCutOffs(e1,e2)){
					if (unfoldingNodesToNetTransitions.containsKey(e1) && unfoldingNodesToNetTransitions.containsKey(e2))
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
