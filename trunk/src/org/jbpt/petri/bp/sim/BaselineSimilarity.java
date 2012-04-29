package org.jbpt.petri.bp.sim;

import org.jbpt.alignment.Alignment;
import org.jbpt.alignment.IEntity;
import org.jbpt.alignment.IEntityModel;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.petri.bp.RelSet;

/**
 * Scores two models by only assessing the overlap of nodes.
 * 
 * @author matthias.weidlich
 *
 */
public class BaselineSimilarity<R extends RelSet<M, N>, M extends IEntityModel<N>, N extends IEntity> extends AbstractRelSetSimilarity<R,M,N> {

	@Override
	public double score(Alignment<R,N> alignment) {
		double in1 = 0;
		for (N n : alignment.getFirstModel().getEntities()) {
			if (n instanceof Place) continue;
			if (((Transition)n).isSilent()) continue;
			in1++;
		}
		double in2 = 0;
		for (N n : alignment.getSecondModel().getEntities()) {
			if (n instanceof Place) continue;
			if (((Transition)n).isSilent()) continue;
			in2++;
		}
		
		double intersection = alignment.getAlignedEntitiesOfFirstModel().size();

		return (intersection > 0) ? (intersection / (in1 + in2 - intersection)) : 0;
	}	
	
	@Override
	public double scoreDice(Alignment<R,N> alignment) {
		double in1 = 0;
		for (N n : alignment.getFirstModel().getEntities()) {
			if (n instanceof Place) continue;
			if (((Transition)n).isSilent()) continue;
			in1++;
		}
		double in2 = 0;
		for (N n : alignment.getSecondModel().getEntities()) {
			if (n instanceof Place) continue;
			if (((Transition)n).isSilent()) continue;
			in2++;
		}
		
		double intersection = alignment.getAlignedEntitiesOfFirstModel().size();

		return (in1 + in2 > 0) ? (2*intersection / (in1 + in2)) : 0;
	}	
}
