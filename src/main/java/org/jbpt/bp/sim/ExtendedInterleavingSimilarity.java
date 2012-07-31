package org.jbpt.bp.sim;

import org.jbpt.alignment.Alignment;
import org.jbpt.alignment.IEntity;
import org.jbpt.alignment.IEntityModel;
import org.jbpt.bp.RelSet;
import org.jbpt.bp.RelSetType;

/**
 * Scores two models by assessing the overlap of their 
 * order and interleaving relations. 
 * 
 * @author matthias.weidlich
 *
 */
public class ExtendedInterleavingSimilarity<R extends RelSet<M, N>, M extends IEntityModel<N>, N extends IEntity> extends AbstractRelSetSimilarity<R,M,N> {
		
	public double score(Alignment<R,N> alignment) {
		double soIn1 = super.getSizeOfRelation(alignment.getFirstModel(), RelSetType.Order);
		double soIn2 = super.getSizeOfRelation(alignment.getSecondModel(), RelSetType.Order);
		double inIn1 = super.getSizeOfRelation(alignment.getFirstModel(), RelSetType.Interleaving);
		double inIn2 = super.getSizeOfRelation(alignment.getSecondModel(), RelSetType.Interleaving);
		
		double intersectionSo1So2  = super.getSizeOfIntersectionOfTwoRelations(alignment, RelSetType.Order,RelSetType.Order);
		double intersectionSo1Rso2 = super.getSizeOfIntersectionOfTwoRelations(alignment, RelSetType.Order,RelSetType.ReverseOrder);
		double intersectionSo1In2  = super.getSizeOfIntersectionOfTwoRelations(alignment, RelSetType.Order,RelSetType.Interleaving);
		double intersectionIn1In2  = super.getSizeOfIntersectionOfTwoRelations(alignment, RelSetType.Interleaving,RelSetType.Interleaving);
		
		double actualIntersection = 2.0*intersectionSo1So2  + 2.0*intersectionSo1Rso2 + 2.0*intersectionSo1In2 + intersectionIn1In2;
		
		return (actualIntersection > 0) ? actualIntersection / (2.0*soIn1 + 2.0*soIn2  + inIn1 + inIn2 - actualIntersection) : 0;
	}

	public double scoreDice(Alignment<R,N> alignment) {
		double soIn1 = super.getSizeOfRelation(alignment.getFirstModel(), RelSetType.Order);
		double soIn2 = super.getSizeOfRelation(alignment.getSecondModel(), RelSetType.Order);
		double inIn1 = super.getSizeOfRelation(alignment.getFirstModel(), RelSetType.Interleaving);
		double inIn2 = super.getSizeOfRelation(alignment.getSecondModel(), RelSetType.Interleaving);
		
		double intersectionSo1So2  = super.getSizeOfIntersectionOfTwoRelations(alignment, RelSetType.Order,RelSetType.Order);
		double intersectionSo1Rso2 = super.getSizeOfIntersectionOfTwoRelations(alignment, RelSetType.Order,RelSetType.ReverseOrder);
		double intersectionSo1In2  = super.getSizeOfIntersectionOfTwoRelations(alignment, RelSetType.Order,RelSetType.Interleaving);
		double intersectionIn1In2  = super.getSizeOfIntersectionOfTwoRelations(alignment, RelSetType.Interleaving,RelSetType.Interleaving);
		
		double actualIntersection = 2.0*intersectionSo1So2  + 2.0*intersectionSo1Rso2 + 2.0*intersectionSo1In2 + intersectionIn1In2;
		
		return (soIn1 + soIn2 + inIn1 + inIn2 > 0) ? (2*actualIntersection / (2.0*soIn1 + 2.0*soIn2  + inIn1 + inIn2)) : 0;
	}
}
