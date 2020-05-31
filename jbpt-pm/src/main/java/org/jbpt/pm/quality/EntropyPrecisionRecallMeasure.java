package org.jbpt.pm.quality;

import org.apache.commons.math3.util.Pair;
import org.processmining.eigenvalue.MetricsCalculator;

import dk.brics.automaton.Automaton;

public class EntropyPrecisionRecallMeasure extends AbstractQualityMeasure {

	int skipsRel = 0; // max allowed number of skips in relevant traces
	int skipsRet = 0; // max allowed number of skips in retrieved traces
	
	public EntropyPrecisionRecallMeasure(Object relevantTraces, Object retrievedTraces, int skipsRel, int skipsRet) {
		super(relevantTraces, retrievedTraces);
		
		this.skipsRel = skipsRel;
		this.skipsRet = skipsRet;
	}

	@Override
	protected void initializeLimitations() {
		this.limitations.add(QualityMeasureLimitation.RETRIEVED_BOUNDED);
		this.limitations.add(QualityMeasureLimitation.RELEVANT_BOUNDED);
	}

	@Override
	protected Pair<Double, Double> computeMeasureValue() {
		System.out.println();
		System.out.println("===================Calculating precision and recall=============================");
		System.out.println();
		
		if ((relevantTraces instanceof Automaton) && (retrievedTraces instanceof Automaton)) {
			Pair<Double, Double> values = MetricsCalculator.calculate((Automaton)relevantTraces, "REL", (Automaton)retrievedTraces, "RET", false, false, skipsRel, skipsRet);
			return values;
		}
		return new Pair<Double, Double>(0.0, 0.0);
	}
}
