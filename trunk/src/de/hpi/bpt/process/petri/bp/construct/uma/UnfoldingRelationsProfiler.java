package de.hpi.bpt.process.petri.bp.construct.uma;

import hub.top.uma.DNode;
import hub.top.uma.DNodeBP;
import hub.top.uma.DNodeSet.DNodeSetElement;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class UnfoldingRelationsProfiler {

	// unfolding relations
	UnfoldingRelationType[][] rels = null;
	
	// all nodes of the unfolding
	DNodeSetElement nodes = null;
	
	Map<DNode, Integer> entryMap = new LinkedHashMap<DNode, Integer>();
	
	public UnfoldingRelationsProfiler(DNodeBP unfolding) {
		
		int size = unfolding.getBranchingProcess().getAllNodes().size();
		rels = new UnfoldingRelationType[size][size];
		nodes = unfolding.getBranchingProcess().getAllNodes();
		
		computePrefixRelations();
		
	}
	
	/**
	 * Computes ordering relations of all events in a Complete Prefix brprocolding
	 * (This method implements the first phase in Algorithm 1).
	 * 
	 * @param brproc	The complete prefix brprocolding
	 */
	private void computePrefixRelations() {
		// STEP 1: Initialize all ordering relations to CONCURRENCY
		rels = new UnfoldingRelationType[nodes.size()][nodes.size()];
		for (int i = 0; i < rels.length; i++)
			for (int j = 0; j < rels.length; j++)
				rels[i][j] = UnfoldingRelationType.CONCURRENCY;
		
		int index = 0;
		
		// STEP 2
		//   - Outer-most loop: Traverse the brprocolding using a pre-order DFS strategy.
		//   - Nested loops are implemented in updateEventRelations method.
		HashSet<DNode> visited = new HashSet<DNode>();
		LinkedList<DNode> worklist = new LinkedList<DNode>();

		for (DNode n : nodes) {
			if (n.pre.length == 0) {
				visited.add(n);
				entryMap.put(n, index++);
				worklist.add(n);
			}
		}
		
		while (!worklist.isEmpty()) {
			DNode n = worklist.removeFirst();
			if (!entryMap.containsKey(n)) {
				entryMap.put(n, index++);
			}
			if (visited.containsAll(Arrays.asList(n.pre))) {
				updateRelations(n);
				visited.add(n);
				if (n.post != null) {
					for (DNode succ: n.post) {
						if (!worklist.contains(succ))
								worklist.add(succ);
					}
				}
			} else
				worklist.addLast(n);
		}
	}
	
	private void updateRelations(DNode n_i) {
		
		if (n_i.pre.length != 0) {
			for (DNode n_j : n_i.pre) {
				rels[entryMap.get(n_j)][entryMap.get(n_i)] = UnfoldingRelationType.CAUSAL;
				rels[entryMap.get(n_i)][entryMap.get(n_j)] = UnfoldingRelationType.INVERSE_CAUSAL;		
	
				for (int k = 0; k < rels.length; k++) {
					if (rels[entryMap.get(n_j)][k] == UnfoldingRelationType.INVERSE_CAUSAL) {
						rels[k][entryMap.get(n_i)] = UnfoldingRelationType.CAUSAL;
						rels[entryMap.get(n_i)][k] = UnfoldingRelationType.INVERSE_CAUSAL;
					}
					if (rels[entryMap.get(n_j)][k] == UnfoldingRelationType.CONFLICT) {
						rels[k][entryMap.get(n_i)] = UnfoldingRelationType.CONFLICT;
						rels[entryMap.get(n_i)][k] = UnfoldingRelationType.CONFLICT;					
					}
				}
			}
		}
		
		if (n_i.isEvent) {
			for (DNode n_pre: n_i.pre) {
				for (DNode n_j: n_pre.post) {
					if (n_j != n_i && entryMap.get(n_j) != null && entryMap.get(n_i) != null) {
						rels[entryMap.get(n_j)][entryMap.get(n_i)] = UnfoldingRelationType.CONFLICT;
						rels[entryMap.get(n_i)][entryMap.get(n_j)] = UnfoldingRelationType.CONFLICT;
						for (int k = 0; k < rels.length; k++) {
							if (entryMap.get(n_i) != k && rels[entryMap.get(n_j)][k] == UnfoldingRelationType.CAUSAL) {
								rels[k][entryMap.get(n_i)] = UnfoldingRelationType.CONFLICT;
								rels[entryMap.get(n_i)][k] = UnfoldingRelationType.CONFLICT;		
							}
						}
					}
				}
			}
		}
	}

	
	public UnfoldingRelationType getRelation(DNode n1, DNode n2) {
		return rels[entryMap.get(n1)][entryMap.get(n2)];
	}
	
	public boolean areCausal(DNode n1, DNode n2) {
		return (rels[entryMap.get(n1)][entryMap.get(n2)]).equals(UnfoldingRelationType.CAUSAL);
	}

	public boolean areInConflict(DNode n1, DNode n2) {
		return (rels[entryMap.get(n1)][entryMap.get(n2)]).equals(UnfoldingRelationType.CONFLICT);
	}

	public boolean areConcurrent(DNode n1, DNode n2) {
		return (rels[entryMap.get(n1)][entryMap.get(n2)]).equals(UnfoldingRelationType.CONCURRENCY);
	}

	@Override
	public String toString() {
		
		String result = "";
		
		result += "==================================================\n";
		result += " Unfolding Relations Profile\n";
		result += "--------------------------------------------------\n";
		for (DNode n : entryMap.keySet())
			result += String.format("%d : %s\n", entryMap.get(n), n);
		result += "--------------------------------------------------\n";
		result += "    ";
		for (int i=0; i<nodes.size(); i++) result += String.format("%-4d", i);
		result += "    \n";
		for (int i=0; i<nodes.size(); i++) {
			result += String.format("%-4d", i);
			for (int j=0; j<nodes.size(); j++) {
				result += String.format("%-4s",rels[i][j]);
			}
			result += String.format("%-4d", i);
			result += "\n";
		}
		result += "    ";
		for (int i=0; i<nodes.size(); i++) result += String.format("%-4d", i);
		result += "    \n";
		result += "==================================================";
		
		return result;
	}
}
