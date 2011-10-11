package de.hpi.bpt.process.petri.bp.sim;

import de.hpi.bpt.process.petri.bp.RelSetAlignment;
import de.hpi.bpt.process.petri.bp.RelSetType;

/**
 * Scores two models by only assessing the overlap of their
 * order relation.
 * 
 * @author matthias.weidlich
 *
 */
public class OrderSimilarity extends AbstractRelSetSimilarity {

	public double score(RelSetAlignment alignment) {
		double in1 = super.getSizeOfRelation(alignment.getFirstRelationSet(), RelSetType.Order);
		double in2 = super.getSizeOfRelation(alignment.getSecondRelationSet(), RelSetType.Order);
		
		double intersection = super.getSizeOfIntersectionOfRelation(alignment, RelSetType.Order);
		
		return (intersection > 0) ? (intersection / (in1 + in2 - intersection)) : 0;
	}	
}
