package org.jbpt.petri.bp.construct.uma;

import hub.top.uma.DNode;
import hub.top.uma.DNodeBP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpt.petri.NetSystem;
import org.jbpt.petri.util.PNAPIMapper;
import org.jbpt.petri.util.UMAUnfolderWrapper;


public class UMAEventStepProfiler {

	// the unfolding
	protected DNodeBP unfolding;

	// profiler for the relations of the unfolding
	protected UnfoldingRelationsProfiler profiler;

	protected Map<DNode,DNode> cutOffConditionsToCorrespondingConditions = new HashMap<DNode, DNode>();

	public Map<DNode, Set<DNode>> cutOfLocalConf = new HashMap<DNode, Set<DNode>>();
	protected Map<DNode, Set<DNode>> causalOfCutOfLocalConf = new HashMap<DNode, Set<DNode>>();

	protected long[][] stepMatrix; 
	protected List<DNode> nodesForStepMatrix = new ArrayList<DNode>();

	public UMAEventStepProfiler(NetSystem pn) {
		
		this.unfolding = UMAUnfolderWrapper.getUMAUnfolding(PNAPIMapper.jBPT2PNAPI(pn));
//		System.out.println(this.unfolding.toDot());
		
		this.profiler = new UnfoldingRelationsProfiler(this.unfolding);

		for (DNode n : this.unfolding.getBranchingProcess().getAllConditions()) {
			if (n.isCutOff  && ! this.unfolding.equivalentNode().get(n).equals(n))
				this.cutOffConditionsToCorrespondingConditions.put(n, this.unfolding.equivalentNode().get(n));
		}
		System.out.println(this.cutOffConditionsToCorrespondingConditions.keySet().size());

		this.deriveCutOfLocalConfiguration();
		this.deriveStepMatrix();

	}
	
	public DNodeBP getUnfolding() {
		return this.unfolding;
	}
	
	public UnfoldingRelationsProfiler getUnfoldingRelationsProfiler() {
		return this.profiler;
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
	
	public long getDistanceInStepMatrix(DNode node1, DNode node2) {
		if (!node1.equals(node2) && this.profiler.getRelation(node1,node2).equals(UnfoldingRelationType.CONCURRENCY)) 
			return 1;
		
		return stepMatrix[this.nodesForStepMatrix.indexOf(node1)][this.nodesForStepMatrix.indexOf(node2)];
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
	
	protected void deriveStepMatrix() {
		
		this.nodesForStepMatrix.addAll(this.unfolding.getBranchingProcess().getAllEvents());
		this.stepMatrix = new long[nodesForStepMatrix.size()][nodesForStepMatrix.size()];

		/*
		 * Using Floyd and Warshall’s algorithm to compute the shortest distance matrix
		 */
		
		/*
		 * First, init distance for path between nodes that do not have any node in between. Those
		 * are the path between nodes that are direct neighbours. In addition, concurrency between
		 * events as treated as if both events would be neighbours.
		 */
		
		for (DNode e1 : this.unfolding.getBranchingProcess().getAllEvents()) {
			int iE1 = this.nodesForStepMatrix.indexOf(e1);
			for (DNode e2 : this.unfolding.getBranchingProcess().getAllEvents()) {				
				int iE2 = this.nodesForStepMatrix.indexOf(e2);
				this.stepMatrix[iE1][iE2]= 100000000;
			}
		}

		for (DNode e1 : this.unfolding.getBranchingProcess().getAllEvents()) {
			int iE1 = this.nodesForStepMatrix.indexOf(e1);
			for (DNode e2 : this.unfolding.getBranchingProcess().getAllEvents()) {				

				if (e1.equals(e2))
					continue;
				
				int iE2 = this.nodesForStepMatrix.indexOf(e2);

				// Direct causal dependency
				if (e1.post.length != 0 && e2.pre.length != 0) {
					for (DNode c : e2.pre) {
						if (c.pre.length != 0)
							if (c.pre[0] == e1)
								this.stepMatrix[iE1][iE2] = 1;
					}
				}
			}
		}
		
		/*
		 * Second, init distance for cut-offs.
		 */
		for (DNode cutC : this.cutOffConditionsToCorrespondingConditions.keySet()) {
			int iCutE = this.nodesForStepMatrix.indexOf(cutC.pre[0]);
			DNode corC = this.cutOffConditionsToCorrespondingConditions.get(cutC);
			
			// There may be no event following the corresponding condition
			if (corC.post == null) 
				continue;
			if (corC.post.length == 0)
				continue;

			// There may be multiple events following the corresponding condition
			for (DNode corE : corC.post) {
				int iCorE = this.nodesForStepMatrix.indexOf(corE);
				this.stepMatrix[iCutE][iCorE] = 1;
			}
		}
		
		/*
		 * Next, dynamically compute the distances by stepwise increasing the length of the
		 * path allowed (parameter r).
		 */
		for (int r = 1; r < this.nodesForStepMatrix.size(); r++) {
			for (DNode e1 : this.unfolding.getBranchingProcess().getAllEvents()) {
				int iE1 = this.nodesForStepMatrix.indexOf(e1);
				for (DNode e2 : this.unfolding.getBranchingProcess().getAllEvents()) {
					int iE2 = this.nodesForStepMatrix.indexOf(e2);
					this.stepMatrix[iE1][iE2] = Math.min(this.stepMatrix[iE1][iE2], this.stepMatrix[iE1][r] + this.stepMatrix[r][iE2]);
				}
			}
		}
		
	}

}
