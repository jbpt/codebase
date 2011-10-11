package de.hpi.bpt.process.petri.bp.sim;

import de.hpi.bpt.process.petri.bp.RelSetAlignment;
import de.hpi.bpt.process.petri.bp.RelSetType;

/**
 * Scores two models by only assessing the overlap of their
 * interleaving order relation.
 * 
 * @author matthias.weidlich
 *
 */
public class InterleavingSimilarity extends AbstractRelSetSimilarity {
	
	public double score(RelSetAlignment alignment) {
		
		double in1 = super.getSizeOfRelation(alignment.getFirstRelationSet(), RelSetType.Interleaving);
		double in2 = super.getSizeOfRelation(alignment.getSecondRelationSet(), RelSetType.Interleaving);
		
		double intersection = super.getSizeOfIntersectionOfRelation(alignment, RelSetType.Interleaving);
		
		return (intersection > 0) ? (intersection / (in1 + in2 - intersection)) : 0;
	}	
}
