package de.hpi.bpt.process.petri.bp.sim;

import de.hpi.bpt.process.petri.bp.BPAlignment;
import de.hpi.bpt.process.petri.bp.BehaviouralProfile.CharacteristicRelationType;

/**
 * Scores two models by only assessing the overlap of their
 * interleaving order relation.
 * 
 * @author matthias.weidlich
 *
 */
public class InterleavingOrderSimilarity extends AbstractBPSimilarity {
	
	public double score(BPAlignment alignment) {
		
		double in1 = super.getSizeOfRelation(alignment.getFirstProfile(), CharacteristicRelationType.InterleavingOrder);
		double in2 = super.getSizeOfRelation(alignment.getSecondProfile(), CharacteristicRelationType.InterleavingOrder);
		
		double intersection = super.getSizeOfIntersectionOfRelation(alignment, CharacteristicRelationType.InterleavingOrder);
		
		return (intersection > 0) ? (intersection / (in1 + in2 - intersection)) : 0;
	}	
}
