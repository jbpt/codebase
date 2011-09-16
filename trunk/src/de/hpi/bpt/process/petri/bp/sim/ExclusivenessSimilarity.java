package de.hpi.bpt.process.petri.bp.sim;

import de.hpi.bpt.process.petri.bp.BPAlignment;
import de.hpi.bpt.process.petri.bp.BehaviouralProfile.CharacteristicRelationType;

/**
 * Scores two models by only assessing the overlap of their
 * exclusiveness relation.
 * 
 * @author matthias.weidlich
 *
 */
public class ExclusivenessSimilarity extends AbstractBPSimilarity {

	public double score(BPAlignment alignment) {
		double in1 = super.getSizeOfRelation(alignment.getFirstProfile(), CharacteristicRelationType.Exclusive);
		double in2 = super.getSizeOfRelation(alignment.getSecondProfile(), CharacteristicRelationType.Exclusive);
		
		double intersection = super.getSizeOfIntersectionOfRelation(alignment, CharacteristicRelationType.Exclusive);
		
		return (intersection > 0) ? (intersection / (in1 + in2 - intersection)) : 0;
	}
}
