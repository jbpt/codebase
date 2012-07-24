package org.jbpt.bp.construct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jbpt.bp.BehaviouralProfile;
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
public class BPCreatorUnfolding extends AbstractRelSetCreator implements RelSetCreator<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> {

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
	protected List<ITransition> transitionsForWeakOrderMatrix;

	// the unfolding
	protected Unfolding unfolding;
	
	// the unfolding as an occurrence net
	protected OccurrenceNet occurrenceNet;

	protected boolean[][] transitiveCausalityMatrixUnfolding; 
	protected List<ITransition> nodesForTransitiveCausalityMatrixUnfolding;

	protected void clear() {
		this.unfolding = null;
		this.occurrenceNet = null;
		this.transitiveCausalityMatrixUnfolding = null;
		this.nodesForTransitiveCausalityMatrixUnfolding = new ArrayList<>();
		this.weakOrderMatrixForTransitions = null; 
		this.transitionsForWeakOrderMatrix = new ArrayList<>();
	}

	@Override
	public RelSet<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> deriveRelationSet(
			INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> pn) {
		return deriveRelationSet(pn, new ArrayList<INode>(pn.getTransitions()));
	}
	
	@Override
	public RelSet<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> deriveRelationSet(
			INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> pn,
			Collection<INode> nodes) {
				
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

		
		BehaviouralProfile<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> profile = new BehaviouralProfile<>(pn,nodes);
		RelSetType[][] matrix = profile.getMatrix();
		
		for (INode t : nodes)
			if (t instanceof Transition)
				this.transitionsForWeakOrderMatrix.add((Transition)t);
		
		this.deriveWeakOrderRelation();

		for(INode t1 : profile.getEntities()) {
			int index1 = profile.getEntities().indexOf(t1);
			for(INode t2 : profile.getEntities()) {
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
		
		for (ITransition e1 : this.occurrenceNet.getTransitions()) {
			for (ITransition e2 : this.occurrenceNet.getTransitions()) {
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
	
	private boolean isWeakOrder(INode n1, INode n2) {
		return weakOrderMatrixForTransitions[this.transitionsForWeakOrderMatrix.indexOf(n1)][this.transitionsForWeakOrderMatrix.indexOf(n2)];
	}
	
	private void deriveTransitiveCutoffRelation() {
		
		this.nodesForTransitiveCausalityMatrixUnfolding.addAll(this.occurrenceNet.getCutoffEvents());
		for (ITransition t : this.occurrenceNet.getCutoffEvents())
			this.nodesForTransitiveCausalityMatrixUnfolding.add(this.occurrenceNet.getCorrespondingEvent(t));
		
		this.transitiveCausalityMatrixUnfolding = new boolean[nodesForTransitiveCausalityMatrixUnfolding.size()][nodesForTransitiveCausalityMatrixUnfolding.size()];

		for (ITransition eCut : this.occurrenceNet.getCutoffEvents()) {
			int source = nodesForTransitiveCausalityMatrixUnfolding.indexOf(eCut);
			int target = nodesForTransitiveCausalityMatrixUnfolding.indexOf(this.occurrenceNet.getCorrespondingEvent(eCut));
			transitiveCausalityMatrixUnfolding[source][target] = true;
		}
		
		for (ITransition eCut : this.occurrenceNet.getCutoffEvents()) {
			ITransition eCor = this.occurrenceNet.getCorrespondingEvent(eCut);
			
			// Corresponding event may be cut-off either
			while (this.occurrenceNet.getCutoffEvents().contains(eCor))
				eCor = this.occurrenceNet.getCorrespondingEvent(eCor);

			for (ITransition eCut2 : this.occurrenceNet.getCutoffEvents()) {
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
	
	private boolean isCausalViaSequenceOfCutOffs(ITransition src, ITransition tar) {
		for (ITransition eCut : this.occurrenceNet.getCutoffEvents()) {
			for (ITransition eCut2 : this.occurrenceNet.getCutoffEvents()) {
				ITransition eCor = this.occurrenceNet.getCorrespondingEvent(eCut2);
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
	
	private boolean isPathInTransitiveCausalityMatrix(ITransition node1, ITransition node2) {
		return transitiveCausalityMatrixUnfolding[this.nodesForTransitiveCausalityMatrixUnfolding.indexOf(node1)][this.nodesForTransitiveCausalityMatrixUnfolding.indexOf(node2)];
	}

}
