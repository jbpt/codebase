package de.hpi.bpt.process.petri.bp.sim;

import de.hpi.bpt.process.petri.bp.BPAlignment;
import de.hpi.bpt.process.petri.bp.BehaviouralProfile.CharacteristicRelationType;

/**
 * Scores two models by assessing the overlap of their 
 * order and interleaving relations. 
 * 
 * @author matthias.weidlich
 *
 */
public class ExtendedInterleavingOrderSimilarity extends AbstractBPSimilarity {
		
	public double score(BPAlignment alignment) {

		double soIn1 = super.getSizeOfRelation(alignment.getFirstProfile(), CharacteristicRelationType.StrictOrder);
		double soIn2 = super.getSizeOfRelation(alignment.getSecondProfile(), CharacteristicRelationType.StrictOrder);
		double inIn1 = super.getSizeOfRelation(alignment.getFirstProfile(), CharacteristicRelationType.InterleavingOrder);
		double inIn2 = super.getSizeOfRelation(alignment.getSecondProfile(), CharacteristicRelationType.InterleavingOrder);
		
		double intersectionSo1So2  = super.getSizeOfIntersectionOfTwoRelations(alignment, CharacteristicRelationType.StrictOrder,CharacteristicRelationType.StrictOrder);
		double intersectionSo1Rso2 = super.getSizeOfIntersectionOfTwoRelations(alignment, CharacteristicRelationType.StrictOrder,CharacteristicRelationType.ReverseStrictOrder);
		double intersectionSo1In2  = super.getSizeOfIntersectionOfTwoRelations(alignment, CharacteristicRelationType.StrictOrder,CharacteristicRelationType.InterleavingOrder);
		double intersectionIn1In2  = super.getSizeOfIntersectionOfTwoRelations(alignment, CharacteristicRelationType.InterleavingOrder,CharacteristicRelationType.InterleavingOrder);
		
		double actualIntersection = 2.0*intersectionSo1So2  + 2.0*intersectionSo1Rso2 + 2.0*intersectionSo1In2 + intersectionIn1In2;
		
		return (actualIntersection > 0) ? actualIntersection / (2.0*soIn1 + 2.0*soIn2  + inIn1 + inIn2 - actualIntersection) : 0;
	}
}
