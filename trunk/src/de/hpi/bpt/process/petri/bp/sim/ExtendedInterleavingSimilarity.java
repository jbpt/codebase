package de.hpi.bpt.process.petri.bp.sim;

import de.hpi.bpt.process.petri.bp.RelSetAlignment;
import de.hpi.bpt.process.petri.bp.RelSetType;

/**
 * Scores two models by assessing the overlap of their 
 * order and interleaving relations. 
 * 
 * @author matthias.weidlich
 *
 */
public class ExtendedInterleavingSimilarity extends AbstractRelSetSimilarity {
		
	public double score(RelSetAlignment alignment) {

		double soIn1 = super.getSizeOfRelation(alignment.getFirstRelationSet(), RelSetType.Order);
		double soIn2 = super.getSizeOfRelation(alignment.getSecondRelationSet(), RelSetType.Order);
		double inIn1 = super.getSizeOfRelation(alignment.getFirstRelationSet(), RelSetType.Interleaving);
		double inIn2 = super.getSizeOfRelation(alignment.getSecondRelationSet(), RelSetType.Interleaving);
		
		double intersectionSo1So2  = super.getSizeOfIntersectionOfTwoRelations(alignment, RelSetType.Order,RelSetType.Order);
		double intersectionSo1Rso2 = super.getSizeOfIntersectionOfTwoRelations(alignment, RelSetType.Order,RelSetType.ReverseOrder);
		double intersectionSo1In2  = super.getSizeOfIntersectionOfTwoRelations(alignment, RelSetType.Order,RelSetType.Interleaving);
		double intersectionIn1In2  = super.getSizeOfIntersectionOfTwoRelations(alignment, RelSetType.Interleaving,RelSetType.Interleaving);
		
		double actualIntersection = 2.0*intersectionSo1So2  + 2.0*intersectionSo1Rso2 + 2.0*intersectionSo1In2 + intersectionIn1In2;
		
		return (actualIntersection > 0) ? actualIntersection / (2.0*soIn1 + 2.0*soIn2  + inIn1 + inIn2 - actualIntersection) : 0;
	}
}
