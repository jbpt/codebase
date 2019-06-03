package org.jbpt.pm.quality;

/**
 * An enumeration of index statuses:
 * 
 * RELEVANT_BOUNDED		- the model that described relevant traces must be bounded
 * RETRIEVED_BOUNDED	- the model that described retrieved traces must be bounded
 * 
 * @author Artem Polyvyanyy
 */
public enum QualityMeasureLimitation {
	RELEVANT_BOUNDED(1),
	RETRIEVED_BOUNDED(2);
	
	private final int limitation;
	
	QualityMeasureLimitation(int limitation) {
        this.limitation = limitation;
    }
	
	/**
	 * Get code of this quality measure limitation.
	 * 
	 * @return Code of this quality measure limitation.
	 */
	public int getQualityMeasureLimitationCode() {
		return this.limitation;
	}

}
