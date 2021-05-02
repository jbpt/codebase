package org.jbpt.pm.quality;

import org.deckfour.xes.model.XLog;
import org.jbpt.petri.NetSystem;
import org.jbpt.pm.utils.Utils;
import org.processmining.eigenvalue.MetricsCalculator;
import org.processmining.eigenvalue.automata.TopologicalEntropyComputer;
import org.processmining.eigenvalue.data.EntropyResult;

import dk.brics.automaton.Automaton;
import gnu.trove.map.TObjectShortMap;
import gnu.trove.map.custom_hash.TObjectShortCustomHashMap;
import gnu.trove.strategy.HashingStrategy;

public class DistanceMeasure {
	
	boolean partial;

	Object model1;
	Object model2;
	
	public DistanceMeasure(Object model1, Object model2, boolean partial) {
	
		this.model1 = model1;
		this.model2 = model2;
		
		this.partial = partial;
	}
	
	public double computeMeasure() throws Exception {
        
		HashingStrategy<String> strategy = new HashingStrategy<String>() {

			public int computeHashCode(String object) {
				return object.hashCode();
			}

			public boolean equals(String o1, String o2) {
				return o1.equals(o2);
			}
		};
		TObjectShortMap<String> activity2short = new TObjectShortCustomHashMap<String>(strategy, 10, 0.5f, (short) -1);
	    
		if (model1 instanceof NetSystem) {
			model1 = Utils.constructAutomatonFromNetSystem((NetSystem) model1, activity2short);
			System.out.println("Number of states: " + ((Automaton)model1).getNumberOfStates());
			System.out.println("Number of transitions: " + ((Automaton)model1).getNumberOfStates());
		} else if (model1 instanceof XLog){
			model1 = Utils.constructAutomatonFromLog((XLog) model1, activity2short);
		}
		if (partial) {
			Utils.addTau((Automaton)model1);
			((Automaton) model1).determinize();
			((Automaton) model1).minimize();
		}
		if (model2 instanceof NetSystem) {
			model2 = Utils.constructAutomatonFromNetSystem((NetSystem) model2, activity2short);
			System.out.println("Number of states: " + ((Automaton)model2).getNumberOfStates());
			System.out.println("Number of transitions: " + ((Automaton)model2).getNumberOfStates());
		} else if (model2 instanceof XLog){
			model2 = Utils.constructAutomatonFromLog((XLog) model2, activity2short);
		}
		if (partial) {
			Utils.addTau((Automaton)model2);
			((Automaton)model2).determinize();
			((Automaton)model2).minimize();
		}		
		Automaton intersection = (((Automaton)model1).intersection((Automaton)model2));
		intersection.determinize();
		intersection.minimize();
		
		Automaton union = (((Automaton)model1).union((Automaton)model2));
		union.determinize();
		union.minimize();
		
		EntropyResult entropyIntersection = TopologicalEntropyComputer.getTopologicalEntropy(intersection, "");
		EntropyResult entropyUnion = TopologicalEntropyComputer.getTopologicalEntropy(union, "");
		
		return 1 - entropyIntersection.topologicalEntropy/entropyUnion.topologicalEntropy;
	}
}
