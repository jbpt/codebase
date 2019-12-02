package org.jbpt.pm.quality;

public enum EntropyMeasureLimitation {

	BOUNDED(1, "The boundness of the model");
	
	
	private final int limitation;
	private final String description;
	
	EntropyMeasureLimitation(int limitation, String description) {
        this.limitation = limitation;
        this.description = description;
    }
	
	/**
	 * Get code of this entropy measure limitation.
	 * 
	 * @return Code of this entropy measure limitation.
	 */
	public int getEntropyMeasureLimitationCode() {
		return this.limitation;
	}
	
	public String getDescription() {
		return this.description;
	}
}
