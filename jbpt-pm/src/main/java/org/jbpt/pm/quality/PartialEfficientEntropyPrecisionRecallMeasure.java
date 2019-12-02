package org.jbpt.pm.quality;

import org.apache.commons.math3.util.Pair;
import org.processmining.eigenvalue.MetricsCalculator;

import dk.brics.automaton.Automaton;

public class PartialEfficientEntropyPrecisionRecallMeasure extends AbstractQualityMeasure {

	public PartialEfficientEntropyPrecisionRecallMeasure(Object relevantTraces, Object retrievedTraces) {
		super(relevantTraces, retrievedTraces);
	}

	@Override
	protected void initializeLimitations() {
		this.limitations.add(QualityMeasureLimitation.RELEVANT_BOUNDED);
		this.limitations.add(QualityMeasureLimitation.RETRIEVED_BOUNDED);
	}

	@Override
	protected Pair<Double, Double> computeMeasureValue() {
		
		if ((relevantTraces instanceof Automaton) && (retrievedTraces instanceof Automaton)) {
			Pair<Double, Double> values = MetricsCalculator.calculate((Automaton)relevantTraces, "relevantBehavior", (Automaton)retrievedTraces, "retrievedBehavior", true, true);
			return values;
		}
		return new Pair<Double, Double>(0.0, 0.0);
	}
}
