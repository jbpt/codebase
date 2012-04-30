package org.jbpt.petri.bp.construct.uma;

import hub.top.uma.DNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;
import org.jbpt.petri.Transition;
import org.jbpt.petri.bp.RelSet;
import org.jbpt.petri.bp.RelSetType;
import org.jbpt.petri.bp.construct.AbstractRelSetCreator;
import org.jbpt.petri.bp.construct.RelSetCreator;


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
public class UMARelSetCreatorUnfolding extends AbstractRelSetCreator implements RelSetCreator<NetSystem, Node> {

	private static UMARelSetCreatorUnfolding eInstance;
	
	public static UMARelSetCreatorUnfolding getInstance() {
		if (eInstance == null)
			eInstance  = new UMARelSetCreatorUnfolding();
		return eInstance;
	}
	
	private UMARelSetCreatorUnfolding() {
		
	}
	
	// needed to extract the relations of events in the unfolding
	protected UMAEventStepProfiler eventStepProfiler;
	
	// captures relation between unfolding and original net
	protected Map<DNode, Transition> unfoldingNodesToNetTransitions = new HashMap<DNode, Transition>(); 
	
	// captures the weak order for transitions
	protected boolean[][] baseOrderMatrixForTransitions; 
	
	// list to have identifiers for the transitions in the matrix
	protected List<Transition> transitionsForBaseOrderMatrix;
	
	protected void clear() {
		eventStepProfiler = null;
		unfoldingNodesToNetTransitions = new HashMap<DNode, Transition>(); 
		baseOrderMatrixForTransitions = null; 
		transitionsForBaseOrderMatrix = new ArrayList<Transition>();
	}

	@Override
	public RelSet<NetSystem, Node> deriveRelationSet(NetSystem pn) {
		return deriveRelationSet(pn, new ArrayList<Node>(pn.getTransitions()));
	}
	
	public RelSet<NetSystem, Node> deriveRelationSet(NetSystem pn, int lookAhead) {
		return deriveRelationSet(pn, new ArrayList<Node>(pn.getTransitions()),lookAhead);
	}

	@Override
	public RelSet<NetSystem, Node> deriveRelationSet(NetSystem pn, Collection<Node> nodes) {
		return deriveRelationSet(pn, nodes, RelSet.RELATION_FAR_LOOKAHEAD);
	}
	
	public RelSet<NetSystem, Node> deriveRelationSet(NetSystem pn,
			Collection<Node> nodes, int lookAhead) {
		
		clear();
		
		this.eventStepProfiler = new UMAEventStepProfiler(pn);
		
		RelSet<NetSystem, Node> rs = new RelSet<NetSystem, Node>(pn,nodes,lookAhead);
		RelSetType[][] matrix = rs.getMatrix();
		
		for (Node t : nodes)
			if (t instanceof Transition)
				this.transitionsForBaseOrderMatrix.add((Transition)t);
		
		for (DNode n : this.eventStepProfiler.getUnfolding().getBranchingProcess().getAllEvents()) 
			for (Transition t : this.transitionsForBaseOrderMatrix) 
				if (t.getId().equals(this.eventStepProfiler.getUnfolding().getSystem().properNames[n.id]))
					this.unfoldingNodesToNetTransitions.put(n,t);

		this.deriveBaseOrderRelation(rs);

		for(Node t1 : rs.getEntities()) {
			int index1 = rs.getEntities().indexOf(t1);
			for(Node t2 : rs.getEntities()) {
				int index2 = rs.getEntities().indexOf(t2);
				
				/*
				 * The behavioural profile matrix is symmetric. Therefore, we 
				 * need to traverse only half of the entries.
				 */
				if (index2 > index1)
					continue;
				
				if (this.isBaseOrder(t1,t2) && this.isBaseOrder(t2,t1))
					super.setMatrixEntry(matrix, index1, index2, RelSetType.Interleaving);
				else if (this.isBaseOrder(t1,t2))
					super.setMatrixEntryOrder(matrix, index1, index2);
				else if (this.isBaseOrder(t2,t1))
					super.setMatrixEntryOrder(matrix, index2, index1);
				else
					super.setMatrixEntry(matrix, index1, index2, RelSetType.Exclusive);
			}
		}		
		
		return rs;
	}
		
	protected void deriveBaseOrderRelation(RelSet<NetSystem, Node> rs) {
		
		baseOrderMatrixForTransitions = new boolean[this.transitionsForBaseOrderMatrix.size()][this.transitionsForBaseOrderMatrix.size()];
		
		for (DNode e1 : this.eventStepProfiler.getUnfolding().getBranchingProcess().getAllEvents()) {
			for (DNode e2 : this.eventStepProfiler.getUnfolding().getBranchingProcess().getAllEvents()) {
				if (this.eventStepProfiler.getDistanceInStepMatrix(e1, e2) <= rs.getLookAhead())
					if (unfoldingNodesToNetTransitions.containsKey(e1) && unfoldingNodesToNetTransitions.containsKey(e2))
						addToRelation(baseOrderMatrixForTransitions, unfoldingNodesToNetTransitions.get(e1), unfoldingNodesToNetTransitions.get(e2));
			}
		}
	}
	
	private boolean isBaseOrder(Node n1, Node n2) {
		return baseOrderMatrixForTransitions[this.transitionsForBaseOrderMatrix.indexOf(n1)][this.transitionsForBaseOrderMatrix.indexOf(n2)];
	}

	private void addToRelation(boolean[][] matrix, Node n1, Node n2) {
		matrix[this.transitionsForBaseOrderMatrix.indexOf(n1)][this.transitionsForBaseOrderMatrix.indexOf(n2)] = true;
	}

}
