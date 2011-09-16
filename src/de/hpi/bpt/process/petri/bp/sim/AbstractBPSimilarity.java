package de.hpi.bpt.process.petri.bp.sim;

import java.util.HashMap;
import java.util.Map;

import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;
import de.hpi.bpt.process.petri.bp.BPAlignment;
import de.hpi.bpt.process.petri.bp.BehaviouralProfile;
import de.hpi.bpt.process.petri.bp.BehaviouralProfile.CharacteristicRelationType;

public abstract class AbstractBPSimilarity implements BPSimilarity {

	private Map<BehaviouralProfile,Map<CharacteristicRelationType,Integer>> relationChache = new HashMap<BehaviouralProfile,Map<CharacteristicRelationType,Integer>>();
	
	public String getName() {
		return this.getClass().getName();
	}
	
	protected int getSizeOfIntersectionOfRelation(BPAlignment alignment, CharacteristicRelationType relation) {
		return getSizeOfIntersectionOfTwoRelations(alignment,relation,relation);
	}
	
	protected int getSizeOfIntersectionOfTwoRelations(BPAlignment alignment, CharacteristicRelationType relation1, CharacteristicRelationType relation2) {
		int sizeOfIntersection = 0;
		
		for (Node n1 : alignment.getFirstProfile().getNodes()) {
			if (n1 instanceof Place) continue;
			if (((Transition)n1).equals(PetriNet.SILENT_LABEL)) continue;
			
			for (Node n2 : alignment.getFirstProfile().getNodes()) {
				if (n1 instanceof Place) continue;
				if (((Transition)n1).equals(PetriNet.SILENT_LABEL)) continue;
				
				if (!alignment.getFirstProfile().getRelationForNodes(n1, n2).equals(relation1)) continue;
				
				if (alignment.getAlignedVerticesOfFirstGraph().contains(n1) && alignment.getAlignedVerticesOfFirstGraph().contains(n2)) {
					if (alignment.getSecondProfile().getRelationForNodes(alignment.getCorrespondingVerticesForVertexOfFirstGraph(n1).iterator().next(), alignment.getCorrespondingVerticesForVertexOfFirstGraph(n2).iterator().next()).equals(relation2)) {
						sizeOfIntersection++;
					}
				}
			}
		}
				
		return sizeOfIntersection;
	}
	
	protected int getSizeOfRelation(BehaviouralProfile profile, CharacteristicRelationType relation) {
		if (!relationChache.containsKey(profile))
			relationChache.put(profile,new HashMap<BehaviouralProfile.CharacteristicRelationType, Integer>());

		if (relationChache.get(profile).containsKey(relation))
			return relationChache.get(profile).get(relation);

		int sizeOfRelation = 0;
		
		for (Node n1 : profile.getNodes()) {
			if (n1 instanceof Place) continue;
			if (((Transition)n1).equals(PetriNet.SILENT_LABEL)) continue;
			
			for (Node n2 : profile.getNodes()) {
				if (n1 instanceof Place) continue;
				if (((Transition)n1).equals(PetriNet.SILENT_LABEL)) continue;

				if (profile.getRelationForNodes(n1,n2).equals(relation))
					sizeOfRelation++;
			}
		}
		
		relationChache.get(profile).put(relation,sizeOfRelation);
		
		if (relation.equals(CharacteristicRelationType.StrictOrder))
			relationChache.get(profile).put(CharacteristicRelationType.ReverseStrictOrder,sizeOfRelation);
		if (relation.equals(CharacteristicRelationType.ReverseStrictOrder))
			relationChache.get(profile).put(CharacteristicRelationType.StrictOrder,sizeOfRelation);
		
		return sizeOfRelation;
	}
	

	
}
