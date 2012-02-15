package org.jbpt.petri.bp.sim;

import org.jbpt.alignment.Alignment;
import org.jbpt.alignment.IEntity;
import org.jbpt.alignment.IEntityModel;
import org.jbpt.petri.bp.RelSet;

/**
 * Interface for all similarities that build upon
 * relation sets.
 * 
 * @author matthias.weidlich, matthias.kunze
 *
 */
public interface RelSetSimilarity<R extends RelSet<M, N>, M extends IEntityModel<N>, N extends IEntity> {
	
	/**
	 * Returns the name of the similarity measure
	 */
	public String getName();
		
	/**
	 * Scores the similarity of two relation sets that are 
	 * related to each other by the alignment given as input.
	 * 
	 * This score is based on the Jaccard Coefficient.
	 * 
	 * @param alignment, that establishes the relation between two relation sets
	 * @return the similarity score for the two relation sets under the given alignment
	 */
	public double score(Alignment<R,N> alignment);

	/**
	 * Scores the similarity of two relation sets that are 
	 * related to each other by the alignment given as input.
	 *
 	 * This score is based on the Dice Coefficient.
	 *
	 * @param alignment, that establishes the relation between two relation sets
	 * @return the similarity score for the two relation sets under the given alignment
	 */
	public double scoreDice(Alignment<R,N> alignment);

}
