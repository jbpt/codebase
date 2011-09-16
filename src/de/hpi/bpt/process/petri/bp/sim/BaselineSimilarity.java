package de.hpi.bpt.process.petri.bp.sim;

import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;
import de.hpi.bpt.process.petri.bp.BPAlignment;

/**
 * Scores two models by only assessing the overlap of nodes.
 * 
 * @author matthias.weidlich
 *
 */
public class BaselineSimilarity extends AbstractBPSimilarity {

	@Override
	public double score(BPAlignment alignment) {
		double in1 = 0;
		for (Node n : alignment.getFirstProfile().getNodes()) {
			if (n instanceof Place) continue;
			if (((Transition)n).equals(PetriNet.SILENT_LABEL)) continue;
			in1++;
		}
		double in2 = 0;
		for (Node n : alignment.getSecondProfile().getNodes()) {
			if (n instanceof Place) continue;
			if (((Transition)n).equals(PetriNet.SILENT_LABEL)) continue;
			in2++;
		}
		
		double intersection = alignment.getAlignedVerticesOfFirstGraph().size();

		return (intersection > 0) ? (intersection / (in1 + in2 - intersection)) : 0;
	}	
}
