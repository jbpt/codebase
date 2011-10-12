package de.hpi.bpt.process.petri.bp.sim;

import de.hpi.bpt.alignment.Alignment;
import de.hpi.bpt.alignment.IEntity;
import de.hpi.bpt.alignment.IEntityModel;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;
import de.hpi.bpt.process.petri.bp.RelSet;

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
			if (((Transition)n).equals(PetriNet.SILENT_LABEL)) continue;
			in1++;
		}
		double in2 = 0;
		for (N n : alignment.getSecondModel().getEntities()) {
			if (n instanceof Place) continue;
			if (((Transition)n).equals(PetriNet.SILENT_LABEL)) continue;
			in2++;
		}
		
		double intersection = alignment.getAlignedEntitiesOfFirstModel().size();

		return (intersection > 0) ? (intersection / (in1 + in2 - intersection)) : 0;
	}	
}
