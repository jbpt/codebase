package org.jbpt.pm.quality;

import org.processmining.eigenvalue.MetricsCalculator;

import dk.brics.automaton.Automaton;

public class PartialEntropyMeasure extends AbstractEntropyMeasure {

	public PartialEntropyMeasure(Object model) {
		super(model);
	}

	@Override
	protected void initializeLimitations() {
		this.limitations.add(EntropyMeasureLimitation.BOUNDED);
	}

	@Override
	protected double computeMeasureValue() {
		System.out.println();
		System.out.println("===================Calculating partial entropy=============================");
		System.out.println();
		
		if (model instanceof Automaton) {
			double value = MetricsCalculator.calculateEntropy((Automaton)model, "model", true, false);
			return value;
		}
		return 0.0;
	}
}
