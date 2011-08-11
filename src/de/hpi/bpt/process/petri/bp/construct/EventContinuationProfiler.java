package de.hpi.bpt.process.petri.bp.construct;

import hub.top.uma.DNode;
import hub.top.uma.DNodeBP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.rels.UnfoldingRelationType;
import de.hpi.bpt.process.petri.rels.UnfoldingRelationsProfiler;
import de.hpi.bpt.process.petri.util.PNAPIMapper;
import de.hpi.bpt.process.petri.util.UMAUnfolderWrapper;

public class EventContinuationProfiler {

	// the unfolding
	protected DNodeBP unfolding;

	// profiler for the relations of the unfolding
	protected UnfoldingRelationsProfiler profiler;

	protected Map<DNode,DNode> cutOffEventsToCorrespondingEvents = new HashMap<DNode, DNode>();

	public Map<DNode, Set<DNode>> cutOfLocalConf = new HashMap<DNode, Set<DNode>>();
	protected Map<DNode, Set<DNode>> causalOfCutOfLocalConf = new HashMap<DNode, Set<DNode>>();

	protected boolean[][] transitiveCausalityMatrixUnfolding; 
	protected List<DNode> nodesForTransitiveCausalityMatrixUnfolding = new ArrayList<DNode>();

	public EventContinuationProfiler(PetriNet pn) {
		
		this.unfolding = UMAUnfolderWrapper.getUMAUnfolding(PNAPIMapper.jBPT2PNAPI(pn));
		this.profiler = new UnfoldingRelationsProfiler(this.unfolding);

		for (DNode n : this.unfolding.equivalentNode().keySet()) {
			if (n.isCutOff && !this.unfolding.equivalentNode().get(n).equals(n)) {
				if (this.unfolding.equivalentNode().get(n).pre.length != 0)
					this.cutOffEventsToCorrespondingEvents.put(n.pre[0], this.unfolding.equivalentNode().get(n).pre[0]);
				else if (this.unfolding.equivalentNode().get(n).post.length != 0)
					this.cutOffEventsToCorrespondingEvents.put(n.pre[0], this.unfolding.equivalentNode().get(n).post[0]);
			}
		}
				
		this.deriveCutOfLocalConfiguration();
		this.deriveTransitiveCutoffRelation();

	}
	
	public DNodeBP getUnfolding() {
		return this.unfolding;
	}
	
	public UnfoldingRelationsProfiler getUnfoldingRelationsProfiler() {
		return this.profiler;
	}
	
	public boolean isCausalViaSequenceOfCutOffs(DNode src, DNode tar) {
		for (DNode eCut : this.cutOffEventsToCorrespondingEvents.keySet()) {
			for (DNode eCor : this.cutOffEventsToCorrespondingEvents.values()) {
				if ((src.equals(eCut) || this.profiler.getRelation(src,eCut).equals(UnfoldingRelationType.CAUSAL)) 
						&& this.isPathInTransitiveCausalityMatrix(eCut,eCor)
						&& (this.profiler.getRelation(eCor,tar).equals(UnfoldingRelationType.CAUSAL) ||
								(!eCor.equals(tar) && this.profiler.getRelation(eCor,tar).equals(UnfoldingRelationType.CONCURRENCY)))) {
					return true;
				}
			}
		}
		return false;
	}

	public UnfoldingRelationType getRelation(DNode n1, DNode n2) {
		return this.profiler.getRelation(n1, n2);
	}
	
	public boolean areCausal(DNode n1, DNode n2) {
		return this.profiler.areCausal(n1, n2);
	}

	public boolean areInConflict(DNode n1, DNode n2) {
		return this.profiler.areInConflict(n1, n2);
	}

	public boolean areConcurrent(DNode n1, DNode n2) {
		return this.profiler.areConcurrent(n1, n2);
	}
	
	public Set<DNode> getCutOfLocalConf(DNode e) {
		return this.cutOfLocalConf.get(e);
	}
	

	protected boolean isPathInTransitiveCausalityMatrix(DNode node1, DNode node2) {
		return transitiveCausalityMatrixUnfolding[this.nodesForTransitiveCausalityMatrixUnfolding.indexOf(node1)][this.nodesForTransitiveCausalityMatrixUnfolding.indexOf(node2)];
	}
	
	protected void deriveCutOfLocalConfiguration() {
		
		for (DNode e : this.unfolding.getBranchingProcess().getAllEvents()) {
			cutOfLocalConf.put(e,new HashSet<DNode>());
			
			for (DNode c : this.unfolding.getBranchingProcess().getAllConditions()) {
				if (Arrays.asList(e.post).contains(c))
					cutOfLocalConf.get(e).add(c);
				else if (this.profiler.getRelation(e,c).equals(UnfoldingRelationType.CONCURRENCY)) {
					if (c.pre.length == 0)
						cutOfLocalConf.get(e).add(c);
					else if (this.profiler.getRelation(c.pre[0],e).equals(UnfoldingRelationType.CAUSAL))
						cutOfLocalConf.get(e).add(c);
				}
			}
		}
	}
	
	protected void deriveTransitiveCutoffRelation() {
		
		this.nodesForTransitiveCausalityMatrixUnfolding.addAll(this.cutOffEventsToCorrespondingEvents.keySet());
		this.nodesForTransitiveCausalityMatrixUnfolding.addAll(this.cutOffEventsToCorrespondingEvents.values());
		transitiveCausalityMatrixUnfolding = new boolean[nodesForTransitiveCausalityMatrixUnfolding.size()][nodesForTransitiveCausalityMatrixUnfolding.size()];

		for (DNode eCut : this.cutOffEventsToCorrespondingEvents.keySet()) {
			int source = nodesForTransitiveCausalityMatrixUnfolding.indexOf(eCut);
			int target = nodesForTransitiveCausalityMatrixUnfolding.indexOf(this.cutOffEventsToCorrespondingEvents.get(eCut));
			transitiveCausalityMatrixUnfolding[source][target] = true;
		}

		for (DNode eCut : this.cutOffEventsToCorrespondingEvents.keySet()) {
			DNode eCor = this.cutOffEventsToCorrespondingEvents.get(eCut);
			for (DNode c : cutOfLocalConf.get(eCor)) {
				for (DNode eCut2 : this.cutOffEventsToCorrespondingEvents.keySet()) {
					if (this.profiler.getRelation(c,eCut2).equals(UnfoldingRelationType.CAUSAL) &&
							!this.profiler.getRelation(c,eCut).equals(UnfoldingRelationType.CONCURRENCY)) {
						int source = nodesForTransitiveCausalityMatrixUnfolding.indexOf(eCor);
						int target = nodesForTransitiveCausalityMatrixUnfolding.indexOf(eCut2);
						transitiveCausalityMatrixUnfolding[source][target] = true;
					}
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

}
