package org.jbpt.pm.quality;

/**
 * An enumeration of index statuses:
 * 
 * RELEVANT_BOUNDED		- the model that described relevant traces must be bounded
 * RETRIEVED_BOUNDED	- the model that described retrieved traces must be bounded
 * 
 * @author Artem Polyvyanyy, Anna Kalenkova
 */
public enum QualityMeasureLimitation {
	
	RETRIEVED_BOUNDED(1, "The boundness of the retrieved model"),
	RELEVANT_BOUNDED(2, "The boundness of the relevant model ");
	
	
	private final int limitation;
	private final String description;
	
	QualityMeasureLimitation(int limitation, String description) {
        this.limitation = limitation;
        this.description = description;
    }
	
	/**
	 * Get code of this quality measure limitation.
	 * 
	 * @return Code of this quality measure limitation.
	 */
	public int getQualityMeasureLimitationCode() {
		return this.limitation;
	}
	
	public String getDescription() {
		return this.description;
	}
}
