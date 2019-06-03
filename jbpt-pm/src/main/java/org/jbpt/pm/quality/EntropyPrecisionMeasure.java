package org.jbpt.pm.quality;

public class EntropyPrecisionMeasure extends AbstractQualityMeasure {

	public EntropyPrecisionMeasure(Object relevantTraces, Object retrievedTraces) {
		super(relevantTraces, retrievedTraces);
	}

	@Override
	protected void initializeLimitations() {
		this.limitations.add(QualityMeasureLimitation.RELEVANT_BOUNDED);
		this.limitations.add(QualityMeasureLimitation.RETRIEVED_BOUNDED);
	}

	@Override
	protected double computeMeasureValue() {
		// TODO Auto-generated method stub
		return 0;
	}

}
