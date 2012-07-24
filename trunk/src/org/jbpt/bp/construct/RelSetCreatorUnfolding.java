package org.jbpt.bp.construct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jbpt.bp.RelSet;
import org.jbpt.bp.RelSetType;
import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;
import org.jbpt.petri.Transition;
import org.jbpt.petri.unfolding.OccurrenceNet;
import org.jbpt.petri.unfolding.OrderingRelation;
import org.jbpt.petri.unfolding.Unfolding;
import org.jbpt.petri.unfolding.UnfoldingSetup;
import org.jbpt.petri.unfolding.order.EsparzaAdequateOrderForArbitrarySystems;


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
public class RelSetCreatorUnfolding extends AbstractRelSetCreator implements RelSetCreator<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> {

	private static RelSetCreatorUnfolding eInstance;
	
	public static RelSetCreatorUnfolding getInstance() {
		if (eInstance == null)
			eInstance  = new RelSetCreatorUnfolding();
		return eInstance;
	}
	
	private RelSetCreatorUnfolding() {
		
	}
	
	// the unfolding
	protected Unfolding unfolding;
	
	// the unfolding as an occurrence net
	protected OccurrenceNet occurrenceNet;

	protected long[][] stepMatrix; 
	protected List<ITransition> nodesForStepMatrix;
		
	// captures the order for transitions
	protected boolean[][] baseOrderMatrixForTransitions; 
	
	// list to have identifiers for the transitions in the matrix
	protected List<ITransition> transitionsForBaseOrderMatrix;
	
	protected void clear() {
		this.unfolding = null;
		this.occurrenceNet = null;
		this.stepMatrix = null;
		this.nodesForStepMatrix = new ArrayList<>();
		this.baseOrderMatrixForTransitions = null; 
		this.transitionsForBaseOrderMatrix = new ArrayList<>();
	}

	@Override
	public RelSet<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> deriveRelationSet(
			INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> pn) {
		return deriveRelationSet(pn, new ArrayList<INode>(pn.getTransitions()));
	}
	
	public RelSet<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> deriveRelationSet(INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> pn, int lookAhead) {
		return deriveRelationSet(pn, new ArrayList<INode>(pn.getTransitions()),lookAhead);
	}

	@Override
	public RelSet<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> deriveRelationSet(
			INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> pn,
			Collection<INode> nodes) {
		return deriveRelationSet(pn, nodes, RelSet.RELATION_FAR_LOOKAHEAD);
	}
	
	public RelSet<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> deriveRelationSet(INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> pn,
			Collection<INode> nodes, int lookAhead) {
		
		// clear internal data structures
		clear();
		
		/*
		 * Derive unfolding
		 */
		UnfoldingSetup setup = new UnfoldingSetup();
		setup.ADEQUATE_ORDER = new EsparzaAdequateOrderForArbitrarySystems();
		
		this.unfolding = new Unfolding(pn,setup);
		this.occurrenceNet = this.unfolding.getOccurrenceNet();
		
//		System.out.println(this.occurrenceNet.toDOT());
		
		/*
		 * Derive step matrix from unfolding
		 */
		this.deriveStepMatrix();

		/*
		 * Init rel set
		 */
		RelSet<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> rs = new RelSet<>(pn,nodes,lookAhead);
		RelSetType[][] matrix = rs.getMatrix();
		
		for (INode t : nodes)
			if (t instanceof Transition)
				this.transitionsForBaseOrderMatrix.add((Transition)t);
		
		this.deriveBaseOrderRelation(rs);

		for(INode t1 : rs.getEntities()) {
			int index1 = rs.getEntities().indexOf(t1);
			for(INode t2 : rs.getEntities()) {
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
		
	protected void deriveBaseOrderRelation(RelSet<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> rs) {
		
		baseOrderMatrixForTransitions = new boolean[this.transitionsForBaseOrderMatrix.size()][this.transitionsForBaseOrderMatrix.size()];
		
		for (ITransition e1 : this.occurrenceNet.getTransitions()) {
			for (ITransition e2 : this.occurrenceNet.getTransitions()) {
				if (getDistanceInStepMatrix(e1, e2) <= rs.getLookAhead())
					if (this.transitionsForBaseOrderMatrix.contains(this.occurrenceNet.getEvent(e1).getTransition()) &&
							this.transitionsForBaseOrderMatrix.contains(this.occurrenceNet.getEvent(e2).getTransition()))
					baseOrderMatrixForTransitions
						[this.transitionsForBaseOrderMatrix.indexOf(this.occurrenceNet.getEvent(e1).getTransition())]
						[this.transitionsForBaseOrderMatrix.indexOf(this.occurrenceNet.getEvent(e2).getTransition())] = true;
			}
		}
	}
	
	private long getDistanceInStepMatrix(ITransition node1, ITransition node2) {
		if (!node1.equals(node2) && this.unfolding.getOrderingRelation(
				this.occurrenceNet.getEvent(node1),
				this.occurrenceNet.getEvent(node2)).equals(OrderingRelation.CONCURRENT)) 
			return 1;
		
		return stepMatrix[this.nodesForStepMatrix.indexOf(node1)][this.nodesForStepMatrix.indexOf(node2)];
	}

	private boolean isBaseOrder(INode n1, INode n2) {
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
		for (ITransition e1 : this.occurrenceNet.getTransitions()) {
			int iE1 = this.nodesForStepMatrix.indexOf(e1);
			for (ITransition e2 : this.occurrenceNet.getTransitions()) {				
				int iE2 = this.nodesForStepMatrix.indexOf(e2);
				this.stepMatrix[iE1][iE2]= 999999999;
			}
		}
		
		/*
		 * Second, direct successors
		 */
		for (ITransition e1 : this.occurrenceNet.getTransitions()) {
			int iE1 = this.nodesForStepMatrix.indexOf(e1);
			for (ITransition e2 : this.occurrenceNet.getTransitions()) {				
				int iE2 = this.nodesForStepMatrix.indexOf(e2);
				for (INode c : this.occurrenceNet.getPreset(e2)) {
					if (this.occurrenceNet.getPreset(c).contains(e1))
						this.stepMatrix[iE1][iE2] = 1;
				}
			}
		}

		/*
		 * Third, init distance for cut-offs.
		 */
		for (ITransition cutE : this.occurrenceNet.getCutoffEvents()) {
			int iCutE = this.nodesForStepMatrix.indexOf(cutE);
			ITransition corE = this.occurrenceNet.getCorrespondingEvent(cutE);
			
			// Corresponding event may be cut-off either
			while (this.occurrenceNet.getCutoffEvents().contains(corE))
				corE = this.occurrenceNet.getCorrespondingEvent(corE);
			
			// There may be multiple events following the corresponding condition
			for (INode c : this.occurrenceNet.getPostset(corE)) {
				for (INode e : this.occurrenceNet.getPostset(c)) {
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
			for (ITransition e1 : this.occurrenceNet.getTransitions()) {
				int iE1 = this.nodesForStepMatrix.indexOf(e1);
				for (ITransition e2 : this.occurrenceNet.getTransitions()) {
					int iE2 = this.nodesForStepMatrix.indexOf(e2);
					this.stepMatrix[iE1][iE2] = Math.min(this.stepMatrix[iE1][iE2], this.stepMatrix[iE1][r] + this.stepMatrix[r][iE2]);
				}
			}
		}
	}

}
