package de.hpi.bpt.process.petri.bp.sim;

import de.hpi.bpt.process.petri.bp.RelSetAlignment;
import de.hpi.bpt.process.petri.bp.RelSetType;

/**
 * Scores two models by assessing the overlap of their 
 * matching and non-matching order relations. 
 * 
 * @author matthias.weidlich
 *
 */
public class ExtendedOrderSimilarity extends AbstractRelSetSimilarity {
	
	public double score(RelSetAlignment alignment) {
		
		double soIn1 = super.getSizeOfRelation(alignment.getFirstRelationSet(), RelSetType.Order);
		double soIn2 = super.getSizeOfRelation(alignment.getSecondRelationSet(), RelSetType.Order);
		
		double intersectionSo1So2 = super.getSizeOfIntersectionOfTwoRelations(alignment, RelSetType.Order,RelSetType.Order);
		double intersectionSo1Rso2 = super.getSizeOfIntersectionOfTwoRelations(alignment, RelSetType.Order,RelSetType.ReverseOrder);
		
		double actualIntersection = 2.0*intersectionSo1So2  + 2.0*intersectionSo1Rso2;
		
		return (actualIntersection > 0) ? actualIntersection / (2.0*soIn1 + 2.0*soIn2 - actualIntersection) : 0;
		
	}	
}
