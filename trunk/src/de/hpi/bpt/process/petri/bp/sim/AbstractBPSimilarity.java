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

/**
 * Abstract class for BP similarities that contains utility functions
 * for the concrete similarity implementations. Those mainly relate to 
 * the computation of the size of a profile relation and the computation
 * of the intersection of two profile relations.
 * 
 * Note that these computations are cached for behavioural profiles. Cache
 * may be emptied by calling <code>invalidateCache()</code>.
 * 
 * @author matthias.weidlich
 *
 */
public abstract class AbstractBPSimilarity implements BPSimilarity {
	
	/**
	 * The cache for the size of a certain relation of a certain 
	 * behavioural profile.
	 */
	private Map<BehaviouralProfile,Map<CharacteristicRelationType,Integer>> relationChache = new HashMap<BehaviouralProfile,Map<CharacteristicRelationType,Integer>>();
	
	/**
	 * Standard method for the name of a similarity. Simply returns the class name.
	 */
	public String getName() {
		return this.getClass().getName();
	}
	
	/**
	 * Computes the size of the intersection of a relation of two aligned behavioural profiles.
	 * 
	 * @param alignment, establishes the relation between two behavioural profiles
	 * @param relation, the type of the relation for which the size of the intersection is determined
	 * @return the size of the intersection of the relations of given type 
	 */
	protected int getSizeOfIntersectionOfRelation(BPAlignment alignment, CharacteristicRelationType relation) {
		return getSizeOfIntersectionOfTwoRelations(alignment,relation,relation);
	}

	/**
	 * Computes the size of the intersection of two given relations of two aligned behavioural profiles.
	 * 
	 * @param alignment, establishes the relation between two behavioural profiles
	 * @param relation1, the type of the considered relation in the first model
	 * @param relation2, the type fo the considered relation in the second model
	 * @return the size of the intersection of the two relations of given type 
	 */
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
	
	/**
	 * Computes the size of a given relation of the given behavioural profile.
	 * 
	 * @param profile, the behavioural profile 
	 * @param relation, the type of the respective relation
	 * @return the size of the relation in the given behavioural profile
	 */
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
		// put into cache
		relationChache.get(profile).put(relation,sizeOfRelation);
		
		// leverage symmetries to fill more details into the cache 
		if (relation.equals(CharacteristicRelationType.StrictOrder))
			relationChache.get(profile).put(CharacteristicRelationType.ReverseStrictOrder,sizeOfRelation);
		if (relation.equals(CharacteristicRelationType.ReverseStrictOrder))
			relationChache.get(profile).put(CharacteristicRelationType.StrictOrder,sizeOfRelation);
		
		return sizeOfRelation;
	}
	
	/**
	 * Resets the internal cache that stores the sizes of relations for behavioural profiles.
	 */
	public void invalidateCache() {
		relationChache = new HashMap<BehaviouralProfile,Map<CharacteristicRelationType,Integer>>();		
	}
	
}
