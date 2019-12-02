package org.jbpt.pm.quality;

import org.processmining.eigenvalue.MetricsCalculator;

import dk.brics.automaton.Automaton;

public class PartialEfficientEntropyMeasure extends AbstractEntropyMeasure {

	public PartialEfficientEntropyMeasure(Object model) {
		super(model);
	}

	@Override
	protected void initializeLimitations() {
		this.limitations.add(EntropyMeasureLimitation.BOUNDED);
	}

	@Override
	protected double computeMeasureValue() {
		System.out.println();
		System.out.println("===================Calculating partial entropy efficiently=============================");
		System.out.println();
		
		if (model instanceof Automaton) {
			double value = MetricsCalculator.calculateEntropy((Automaton)model, "model", true, true);
			return value;
		}
		return 0.0;
	}
}