package de.hpi.bpt.process.petri.bp.sim;

import de.hpi.bpt.alignment.Alignment;
import de.hpi.bpt.alignment.IEntity;
import de.hpi.bpt.alignment.IEntityModel;
import de.hpi.bpt.process.petri.bp.RelSet;
import de.hpi.bpt.process.petri.bp.RelSetType;

/**
 * Scores two models by only assessing the overlap of their
 * interleaving order relation.
 * 
 * @author matthias.weidlich
 *
 */
public class InterleavingSimilarity<R extends RelSet<M, N>, M extends IEntityModel<N>, N extends IEntity> extends AbstractRelSetSimilarity<R,M,N> {
	
	public double score(Alignment<R,N> alignment) {
		
		double in1 = super.getSizeOfRelation(alignment.getFirstModel(), RelSetType.Interleaving);
		double in2 = super.getSizeOfRelation(alignment.getSecondModel(), RelSetType.Interleaving);
		
		double intersection = super.getSizeOfIntersectionOfRelation(alignment, RelSetType.Interleaving);
		
		return (intersection > 0) ? (intersection / (in1 + in2 - intersection)) : 0;
	}	
}
