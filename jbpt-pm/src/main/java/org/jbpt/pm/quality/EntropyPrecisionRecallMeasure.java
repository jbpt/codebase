package org.jbpt.pm.quality;

import org.apache.commons.math3.util.Pair;
import org.processmining.eigenvalue.MetricsCalculator;

import dk.brics.automaton2.Automaton;

public class EntropyPrecisionRecallMeasure extends AbstractQualityMeasure {

	public EntropyPrecisionRecallMeasure(Object relevantTraces, Object retrievedTraces) {
		super(relevantTraces, retrievedTraces);
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
			Pair<Double, Double> values = MetricsCalculator.calculate((Automaton)relevantTraces, "relevantBehavior", (Automaton)retrievedTraces, "retrievedBehavior", false, false);
			return values;
		}
		return new Pair<Double, Double>(0.0, 0.0);
	}
}
