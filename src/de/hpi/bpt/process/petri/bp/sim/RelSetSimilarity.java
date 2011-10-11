package de.hpi.bpt.process.petri.bp.sim;

import de.hpi.bpt.process.petri.bp.RelSetAlignment;

/**
 * Interface for all similarities that build upon
 * relation sets.
 * 
 * @author matthias.weidlich, matthias.kunze
 *
 */
public interface RelSetSimilarity {
	
	/**
	 * Returns the name of the similarity measure
	 */
	public String getName();
		
	/**
	 * Scores the similarity of two relation sets that are 
	 * related to each other by the alignment given as input.
	 * 
	 * @param alignment, that establishes the relation between two relation sets
	 * @return the similarity score for the two relation sets under the given alignment
	 */
	public double score(RelSetAlignment alignment);

}
