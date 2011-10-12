package de.hpi.bpt.process.petri.bp.sim;

import de.hpi.bpt.alignment.Alignment;
import de.hpi.bpt.alignment.IEntity;
import de.hpi.bpt.alignment.IEntityModel;
import de.hpi.bpt.process.petri.bp.RelSet;
import de.hpi.bpt.process.petri.bp.RelSetType;

/**
 * Scores two models by assessing the overlap of their 
 * matching and non-matching order relations. 
 * 
 * @author matthias.weidlich
 *
 */
public class ExtendedOrderSimilarity<R extends RelSet<M, N>, M extends IEntityModel<N>, N extends IEntity> extends AbstractRelSetSimilarity<R,M,N> {
	
	public double score(Alignment<R,N> alignment) {
		
		double soIn1 = super.getSizeOfRelation(alignment.getFirstModel(), RelSetType.Order);
		double soIn2 = super.getSizeOfRelation(alignment.getSecondModel(), RelSetType.Order);
		
		double intersectionSo1So2 = super.getSizeOfIntersectionOfTwoRelations(alignment, RelSetType.Order,RelSetType.Order);
		double intersectionSo1Rso2 = super.getSizeOfIntersectionOfTwoRelations(alignment, RelSetType.Order,RelSetType.ReverseOrder);
		
		double actualIntersection = 2.0*intersectionSo1So2  + 2.0*intersectionSo1Rso2;
		
		return (actualIntersection > 0) ? actualIntersection / (2.0*soIn1 + 2.0*soIn2 - actualIntersection) : 0;
		
	}	
}
