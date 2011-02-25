package de.hpi.bpt.process.petri.rels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.hpi.bpt.graph.algo.CombinationGenerator;
import de.hpi.bpt.graph.algo.ReflexiveTransitiveClosure;
import de.hpi.bpt.process.petri.Flow;
import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PNAnalyzer;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;

public class UnfoldingRelationsProfiler {

	UnfoldingRelationType[][] rels = null;
	List<Node> nodes = null;
	
	public UnfoldingRelationsProfiler(PetriNet net) {
		if (!PNAnalyzer.isBipartite(net)) throw new IllegalArgumentException();
		if (!PNAnalyzer.isConnected(net)) throw new IllegalArgumentException();
		
		int size = net.getNodes().size();
		rels = new UnfoldingRelationType[size][size];
		nodes = new ArrayList<Node>(net.getNodes());
		
		// initialize with concurrency relations
		for (int i=0; i<size; i++)
			for (int j=0; j<size; j++)
				this.setRelation(i, j, UnfoldingRelationType.CONCURRENCY);
		
		ReflexiveTransitiveClosure<Flow, Node> tc = new ReflexiveTransitiveClosure<Flow, Node>(net);
		
		// set causal relation
		for (int i=0; i<size; i++) {
			for (int j=i+1; j<size; j++) {
				if (tc.hasPath(nodes.get(i), nodes.get(j))) {
					this.setRelation(i, j, UnfoldingRelationType.CAUSAL);
					this.setRelation(j, i, UnfoldingRelationType.INVERSE_CAUSAL);
				}
				
				if (tc.hasPath(nodes.get(j), nodes.get(i))) {
					this.setRelation(i, j, UnfoldingRelationType.INVERSE_CAUSAL);
					this.setRelation(j, i, UnfoldingRelationType.CAUSAL);
				}
			}
		}
		
		// get conflicts
		Collection<Transition> transitions = new ArrayList<Transition>(net.getTransitions());
		Collection<Collection<Transition>> conflicts = new ArrayList<Collection<Transition>>();
		if (transitions.size()>1) {
			CombinationGenerator<Transition> pairs = new CombinationGenerator<Transition>(transitions,2);
			while (pairs.hasMore()) {
				Collection<Transition> pair = pairs.getNextCombination();
				
				Iterator<Transition> iter = pair.iterator();
				Transition t1 = iter.next();
				Transition t2 = iter.next();

				for (Place p1 : net.getPreset(t1))
					for (Place p2 : net.getPreset(t2))
						if (p1.equals(p2))
							conflicts.add(pair);			
			}
		}
		
		// set conflict relation
		for (int i=0; i<size; i++) {
			for (int j=i; j<size; j++) {
				if (this.getRelation(i,j) == UnfoldingRelationType.CONCURRENCY) {
					for (Collection<Transition> pair : conflicts) {
						Iterator<Transition> iter = pair.iterator();
						Transition t1 = iter.next();
						Transition t2 = iter.next();
						Node x1 = nodes.get(i);
						Node x2 = nodes.get(j);
						
						if ((tc.hasPath(t1, x1) && tc.hasPath(t2, x2)) || (tc.hasPath(t1, x2) && tc.hasPath(t2, x1))) {
							this.setRelation(x1, x2, UnfoldingRelationType.CONFLICT);
							this.setRelation(x2, x1, UnfoldingRelationType.CONFLICT);
						}
					}
				}
			}
		}
	}
	
	public UnfoldingRelationType getRelation(Node n1, Node n2) {
		return rels[nodes.indexOf(n1)][nodes.indexOf(n2)];
	}
	
	private UnfoldingRelationType getRelation(int i1, int i2) {
		return rels[i1][i2];
	}
	
	private void setRelation (int i1, int i2, UnfoldingRelationType type) {
		rels[i1][i2] = type;
	}
	
	private void setRelation (Node n1, Node n2, UnfoldingRelationType type) {
		rels[nodes.indexOf(n1)][nodes.indexOf(n2)] = type;
	}
	
	@Override
	public String toString() {
		String result = "";
		
		result += "==================================================\n";
		result += " Unfolding Relations Profile\n";
		result += "--------------------------------------------------\n";
		for (int i=0; i<nodes.size(); i++)
			result += String.format("%d : %s\n", i, nodes.get(i));
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
