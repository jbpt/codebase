package org.jbpt.bp.construct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jbpt.bp.RelSet;
import org.jbpt.bp.RelSetType;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;
import org.jbpt.petri.Transition;
import org.jbpt.petri.unfolding.CompletePrefixUnfolding;
import org.jbpt.petri.unfolding.CompletePrefixUnfoldingSetup;
import org.jbpt.petri.unfolding.OccurrenceNet;
import org.jbpt.petri.unfolding.OrderingRelationType;
import org.jbpt.petri.unfolding.order.AdequateOrderType;


/**
 * Computation of a relation set for a given collection of 
 * transitions (or all transitions) of a bounded net system using 
 * its complete prefix unfolding. 
 *
 * 
 * Note that boundedness is not checked explicitly. If this class is
 * used for unbounded nets, it will still return a relation set
 * as a result since the unfolder has a fixed boundary (default = 1)
 * for concurrent conditions that relate to the same place in the 
 * unfolding. Hence, it stops even if there does not exist a finite 
 * prefix. However, it is not guaranteed that the obtained relation 
 * set is correct in this case!
 * 
 * Implemented as a singleton, use <code>getInstance()</code>.
 * 
 * @author matthias.weidlich
 *
 */
public class RelSetCreatorUnfolding extends AbstractRelSetCreator implements RelSetCreator<NetSystem, Node> {

	private static RelSetCreatorUnfolding eInstance;
	
	public static RelSetCreatorUnfolding getInstance() {
		if (eInstance == null)
			eInstance  = new RelSetCreatorUnfolding();
		return eInstance;
	}
	
	private RelSetCreatorUnfolding() {
		
	}
	
	// the unfolding
	protected CompletePrefixUnfolding unfolding;
	
	// the unfolding as an occurrence net
	protected OccurrenceNet occurrenceNet;

	protected long[][] stepMatrix; 
	protected List<Transition> nodesForStepMatrix;
		
	// captures the order for transitions
	protected boolean[][] baseOrderMatrixForTransitions; 
	
	// list to have identifiers for the transitions in the matrix
	protected List<Transition> transitionsForBaseOrderMatrix;
	
	protected void clear() {
		this.unfolding = null;
		this.occurrenceNet = null;
		this.stepMatrix = null;
		this.nodesForStepMatrix = new ArrayList<Transition>();
		this.baseOrderMatrixForTransitions = null; 
		this.transitionsForBaseOrderMatrix = new ArrayList<Transition>();
	}

	@Override
	public RelSet<NetSystem, Node> deriveRelationSet(NetSystem pn) {
		return deriveRelationSet(pn, new ArrayList<Node>(pn.getTransitions()));
	}
	
	public RelSet<NetSystem, Node> deriveRelationSet(NetSystem pn, int lookAhead) {
		return deriveRelationSet(pn, new ArrayList<Node>(pn.getTransitions()),lookAhead);
	}

	@Override
	public RelSet<NetSystem, Node> deriveRelationSet(NetSystem pn,
			Collection<Node> nodes) {
		return deriveRelationSet(pn, nodes, RelSet.RELATION_FAR_LOOKAHEAD);
	}
	
	public RelSet<NetSystem, Node> deriveRelationSet(NetSystem pn,
			Collection<Node> nodes, int lookAhead) {
		
		// clear internal data structures
		clear();
		
		/*
		 * Derive unfolding
		 */
		CompletePrefixUnfoldingSetup setup = new CompletePrefixUnfoldingSetup();
		setup.ADEQUATE_ORDER = AdequateOrderType.ESPARZA_FOR_ARBITRARY_SYSTEMS;
		
		this.unfolding = new CompletePrefixUnfolding(pn,setup);
		this.occurrenceNet = (OccurrenceNet) this.unfolding.getOccurrenceNet();
		
//		System.out.println(this.occurrenceNet.toDOT());
		
		/*
		 * Derive step matrix from unfolding
		 */
		this.deriveStepMatrix();

		/*
		 * Init rel set
		 */
		RelSet<NetSystem, Node> rs = new RelSet<NetSystem, Node>(pn,nodes,lookAhead);
		RelSetType[][] matrix = rs.getMatrix();
		
		for (Node t : nodes)
			if (t instanceof Transition)
				if (!this.transitionsForBaseOrderMatrix.contains((Transition)t))
					this.transitionsForBaseOrderMatrix.add((Transition)t);
		
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
		
		for (Transition e1 : this.occurrenceNet.getTransitions()) {
			for (Transition e2 : this.occurrenceNet.getTransitions()) {
				if (getDistanceInStepMatrix(e1, e2) <= rs.getLookAhead())
					if (this.transitionsForBaseOrderMatrix.contains(this.occurrenceNet.getEvent(e1).getTransition()) &&
							this.transitionsForBaseOrderMatrix.contains(this.occurrenceNet.getEvent(e2).getTransition()))
					baseOrderMatrixForTransitions
						[this.transitionsForBaseOrderMatrix.indexOf(this.occurrenceNet.getEvent(e1).getTransition())]
						[this.transitionsForBaseOrderMatrix.indexOf(this.occurrenceNet.getEvent(e2).getTransition())] = true;
			}
		}
	}
	
	private long getDistanceInStepMatrix(Transition node1, Transition node2) {
		if (!node1.equals(node2) && this.occurrenceNet.getOrderingRelation(node1, node2).equals(OrderingRelationType.CONCURRENT)) 
			return 1;
		
		return stepMatrix[this.nodesForStepMatrix.indexOf(node1)][this.nodesForStepMatrix.indexOf(node2)];
	}

	private boolean isBaseOrder(Node n1, Node n2) {
		return baseOrderMatrixForTransitions[this.transitionsForBaseOrderMatrix.indexOf(n1)][this.transitionsForBaseOrderMatrix.indexOf(n2)];
	}
	
	protected void deriveStepMatrix() {
		
		this.nodesForStepMatrix.addAll(this.occurrenceNet.getTransitions());
		this.stepMatrix = new long[nodesForStepMatrix.size()][nodesForStepMatrix.size()];

		/*
		 * Using Floyd and Warshall’s algorithm to compute the shortest distance matrix
		 */

		/*
		 * First, init matrix
		 */
		for (Transition e1 : this.occurrenceNet.getTransitions()) {
			int iE1 = this.nodesForStepMatrix.indexOf(e1);
			for (Transition e2 : this.occurrenceNet.getTransitions()) {				
				int iE2 = this.nodesForStepMatrix.indexOf(e2);
				this.stepMatrix[iE1][iE2]= 999999999;
			}
		}
		
		/*
		 * Second, direct successors
		 */
		for (Transition e1 : this.occurrenceNet.getTransitions()) {
			int iE1 = this.nodesForStepMatrix.indexOf(e1);
			for (Transition e2 : this.occurrenceNet.getTransitions()) {				
				int iE2 = this.nodesForStepMatrix.indexOf(e2);
				for (Node c : this.occurrenceNet.getPreset(e2)) {
					if (this.occurrenceNet.getPreset(c).contains(e1))
						this.stepMatrix[iE1][iE2] = 1;
				}
			}
		}

		/*
		 * Third, init distance for cut-offs.
		 */
		for (Transition cutE : this.occurrenceNet.getCutoffEvents()) {
			int iCutE = this.nodesForStepMatrix.indexOf(cutE);
			Transition corE = this.occurrenceNet.getCorrespondingEvent(cutE);
			
			// Corresponding event may be cut-off either
			while (this.occurrenceNet.getCutoffEvents().contains(corE))
				corE = this.occurrenceNet.getCorrespondingEvent(corE);
			
			// There may be multiple events following the corresponding condition
			for (Node c : this.occurrenceNet.getPostset(corE)) {
				for (Node e : this.occurrenceNet.getPostset(c)) {
					int iE = this.nodesForStepMatrix.indexOf(e);
					this.stepMatrix[iCutE][iE] = 1;
				}
			}
		}
				
		/*
		 * Next, dynamically compute the distances by stepwise increasing the length of the
		 * path allowed (parameter r).
		 */
		for (int r = 0; r < this.nodesForStepMatrix.size(); r++) {
			for (Transition e1 : this.occurrenceNet.getTransitions()) {
				int iE1 = this.nodesForStepMatrix.indexOf(e1);
				for (Transition e2 : this.occurrenceNet.getTransitions()) {
					int iE2 = this.nodesForStepMatrix.indexOf(e2);
					this.stepMatrix[iE1][iE2] = Math.min(this.stepMatrix[iE1][iE2], this.stepMatrix[iE1][r] + this.stepMatrix[r][iE2]);
				}
			}
		}
	}
}
