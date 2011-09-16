package de.hpi.bpt.process.petri.bp.sim;

import de.hpi.bpt.process.petri.bp.BPAlignment;

/**
 * Interface for all similarities that build upon
 * behavioural profiles.
 * 
 * @author matthias.weidlich, matthias.kunze
 *
 */
public interface BPSimilarity {
	
	/**
	 * Returns the name of the similarity measure
	 */
	public String getName();
		
	/**
	 * Scores the similarity of two behavioural profiles that are 
	 * related to each other by the alignment given as input.
	 * 
	 * @param alignment, that establishes the relation between two behavioural profiles
	 * @return the similarity score for the two behavioural profiles under the given alignment
	 */
	public double score(BPAlignment alignment);

}
