package org.jbpt.bp.sim;

import org.jbpt.alignment.Alignment;
import org.jbpt.bp.RelSet;
import org.jbpt.bp.RelSetType;
import org.jbpt.hypergraph.abs.IEntity;
import org.jbpt.hypergraph.abs.IEntityModel;

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

	public double scoreDice(Alignment<R,N> alignment) {
		double in1 = super.getSizeOfRelation(alignment.getFirstModel(), RelSetType.Interleaving);
		double in2 = super.getSizeOfRelation(alignment.getSecondModel(), RelSetType.Interleaving);
		
		double intersection = super.getSizeOfIntersectionOfRelation(alignment, RelSetType.Interleaving);
		
		return (in1 + in2 > 0) ? (2*intersection / (in1 + in2)) : 0;
	}	

}
