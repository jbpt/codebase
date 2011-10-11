package de.hpi.bpt.process.petri.bp.sim;

import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;
import de.hpi.bpt.process.petri.bp.RelSetAlignment;

/**
 * Scores two models by only assessing the overlap of nodes.
 * 
 * @author matthias.weidlich
 *
 */
public class BaselineSimilarity extends AbstractRelSetSimilarity {

	@Override
	public double score(RelSetAlignment alignment) {
		double in1 = 0;
		for (Node n : alignment.getFirstRelationSet().getEntities()) {
			if (n instanceof Place) continue;
			if (((Transition)n).equals(PetriNet.SILENT_LABEL)) continue;
			in1++;
		}
		double in2 = 0;
		for (Node n : alignment.getSecondRelationSet().getEntities()) {
			if (n instanceof Place) continue;
			if (((Transition)n).equals(PetriNet.SILENT_LABEL)) continue;
			in2++;
		}
		
		double intersection = alignment.getAlignedVerticesOfFirstGraph().size();

		return (intersection > 0) ? (intersection / (in1 + in2 - intersection)) : 0;
	}	
}
