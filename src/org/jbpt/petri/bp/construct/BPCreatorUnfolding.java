package org.jbpt.petri.bp.construct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;
import org.jbpt.petri.Transition;
import org.jbpt.petri.bp.BehaviouralProfile;
import org.jbpt.petri.bp.RelSetType;
import org.jbpt.petri.unf.OccurrenceNet;
import org.jbpt.petri.unf.OrderingRelation;
import org.jbpt.petri.unf.Unfolding;
import org.jbpt.petri.unf.UnfoldingSetup;
import org.jbpt.petri.unf.order.EsparzaAdequateOrderForArbitrarySystems;


/**
 * Computation of the behavioural profile for a given collection of 
 * transitions (or all transitions) of a bounded net system using its complete
 * prefix unfolding. 
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
public class BPCreatorUnfolding extends AbstractRelSetCreator implements RelSetCreator<NetSystem, Node> {

	private static BPCreatorUnfolding eInstance;
	
	public static BPCreatorUnfolding getInstance() {
		if (eInstance == null)
			eInstance  = new BPCreatorUnfolding();
		return eInstance;
	}
	
	private BPCreatorUnfolding() {
		
	}
	
	// captures the weak order for transitions
	protected boolean[][] weakOrderMatrixForTransitions; 
	
	// list to have identifiers for the transitions in the matrix
	protected List<Transition> transitionsForWeakOrderMatrix;

	// the unfolding
	protected Unfolding unfolding;
	
	// the unfolding as an occurrence net
	protected OccurrenceNet occurrenceNet;

	protected boolean[][] transitiveCausalityMatrixUnfolding; 
	protected List<Transition> nodesForTransitiveCausalityMatrixUnfolding;

	protected void clear() {
		this.unfolding = null;
		this.occurrenceNet = null;
		this.transitiveCausalityMatrixUnfolding = null;
		this.nodesForTransitiveCausalityMatrixUnfolding = new ArrayList<Transition>();
		this.weakOrderMatrixForTransitions = null; 
		this.transitionsForWeakOrderMatrix = new ArrayList<Transition>();
	}

	@Override
	public BehaviouralProfile<NetSystem, Node> deriveRelationSet(NetSystem pn) {
		return deriveRelationSet(pn, new ArrayList<Node>(pn.getTransitions()));
	}
	
	@Override
	public BehaviouralProfile<NetSystem, Node> deriveRelationSet(NetSystem pn,
			Collection<Node> nodes) {
				
		// clear internal data structures
		clear();
		
		/*
		 * Derive unfolding
		 */
		UnfoldingSetup setup = new UnfoldingSetup();
		setup.ADEQUATE_ORDER = new EsparzaAdequateOrderForArbitrarySystems();
		
		this.unfolding = new Unfolding(pn,setup);
		this.occurrenceNet = this.unfolding.getOccurrenceNet();
		
		/*
		 * Derive transitive cutoff relation
		 */
		this.deriveTransitiveCutoffRelation();

		
		BehaviouralProfile<NetSystem, Node> profile = new BehaviouralProfile<NetSystem, Node>(pn,nodes);
		RelSetType[][] matrix = profile.getMatrix();
		
		for (Node t : nodes)
			if (t instanceof Transition)
				this.transitionsForWeakOrderMatrix.add((Transition)t);
		
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
		
		for (Transition e1 : this.occurrenceNet.getTransitions()) {
			for (Transition e2 : this.occurrenceNet.getTransitions()) {
				if (this.occurrenceNet.getOrderingRelation(e1,e2).equals(OrderingRelation.CAUSAL)
						|| (!e1.equals(e2) && this.occurrenceNet.getOrderingRelation(e1,e2).equals(OrderingRelation.CONCURRENT))) {
					weakOrderMatrixForTransitions[this.transitionsForWeakOrderMatrix.indexOf(this.occurrenceNet.getEvent(e1).getTransition())]
					                              [this.transitionsForWeakOrderMatrix.indexOf(this.occurrenceNet.getEvent(e2).getTransition())] = true;
				}
				else if (this.isCausalViaSequenceOfCutOffs(e1,e2)){
					weakOrderMatrixForTransitions[this.transitionsForWeakOrderMatrix.indexOf(this.occurrenceNet.getEvent(e1).getTransition())]
					                              [this.transitionsForWeakOrderMatrix.indexOf(this.occurrenceNet.getEvent(e2).getTransition())] = true;
				}
			}
		}
	}
	
	private boolean isWeakOrder(Node n1, Node n2) {
		return weakOrderMatrixForTransitions[this.transitionsForWeakOrderMatrix.indexOf(n1)][this.transitionsForWeakOrderMatrix.indexOf(n2)];
	}
	
	private void deriveTransitiveCutoffRelation() {
		
		this.nodesForTransitiveCausalityMatrixUnfolding.addAll(this.occurrenceNet.getCutoffEvents());
		for (Transition t : this.occurrenceNet.getCutoffEvents())
			this.nodesForTransitiveCausalityMatrixUnfolding.add(this.occurrenceNet.getCorrespondingEvent(t));
		
		this.transitiveCausalityMatrixUnfolding = new boolean[nodesForTransitiveCausalityMatrixUnfolding.size()][nodesForTransitiveCausalityMatrixUnfolding.size()];

		for (Transition eCut : this.occurrenceNet.getCutoffEvents()) {
			int source = nodesForTransitiveCausalityMatrixUnfolding.indexOf(eCut);
			int target = nodesForTransitiveCausalityMatrixUnfolding.indexOf(this.occurrenceNet.getCorrespondingEvent(eCut));
			transitiveCausalityMatrixUnfolding[source][target] = true;
		}
		
		for (Transition eCut : this.occurrenceNet.getCutoffEvents()) {
			Transition eCor = this.occurrenceNet.getCorrespondingEvent(eCut);
			
			// Corresponding event may be cut-off either
			while (this.occurrenceNet.getCutoffEvents().contains(eCor))
				eCor = this.occurrenceNet.getCorrespondingEvent(eCor);

			for (Transition eCut2 : this.occurrenceNet.getCutoffEvents()) {
				if (this.occurrenceNet.getOrderingRelation(eCor,eCut2).equals(OrderingRelation.CAUSAL)) {
					int source = nodesForTransitiveCausalityMatrixUnfolding.indexOf(eCor);
					int target = nodesForTransitiveCausalityMatrixUnfolding.indexOf(eCut2);
					transitiveCausalityMatrixUnfolding[source][target] = true;
				}
			}
		}

		// compute transitive closure
		this.transitiveCausalityMatrixUnfolding = computeTransitiveClosure(this.transitiveCausalityMatrixUnfolding);
	}

	private boolean[][] computeTransitiveClosure(boolean[][] matrix) {
		for (int k = 0; k < matrix.length; k++) {
			for (int row = 0; row < matrix.length; row++) {
				// In Warshall's original paper, the inner-most loop is
				// guarded by the boolean value in [row][k] --- omitting
				// the loop on false and removing the "&" in the evaluation.
				if (matrix[row][k]) {
					for (int col = 0; col < matrix.length; col++) {
						matrix[row][col] = matrix[row][col] | matrix[k][col];
					}
				}
			}
		}
		return matrix;
	}
	
	private boolean isCausalViaSequenceOfCutOffs(Transition src, Transition tar) {
		for (Transition eCut : this.occurrenceNet.getCutoffEvents()) {
			for (Transition eCut2 : this.occurrenceNet.getCutoffEvents()) {
				Transition eCor = this.occurrenceNet.getCorrespondingEvent(eCut2);
				if ((src.equals(eCut) || this.occurrenceNet.getOrderingRelation(src,eCut).equals(OrderingRelation.CAUSAL)) 
						&& this.isPathInTransitiveCausalityMatrix(eCut,eCor)
						&& (this.occurrenceNet.getOrderingRelation(eCor,tar).equals(OrderingRelation.CAUSAL) ||
								(!eCor.equals(tar) && this.occurrenceNet.getOrderingRelation(eCor,tar).equals(OrderingRelation.CONCURRENT)))) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isPathInTransitiveCausalityMatrix(Transition node1, Transition node2) {
		return transitiveCausalityMatrixUnfolding[this.nodesForTransitiveCausalityMatrixUnfolding.indexOf(node1)][this.nodesForTransitiveCausalityMatrixUnfolding.indexOf(node2)];
	}

}
