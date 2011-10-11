package de.hpi.bpt.process.petri.bp.sim;

import java.util.HashMap;
import java.util.Map;

import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;
import de.hpi.bpt.process.petri.bp.RelSetAlignment;
import de.hpi.bpt.process.petri.bp.RelSet;
import de.hpi.bpt.process.petri.bp.RelSetType;

/**
 * Abstract class for similarities for relation sets
 * that contains utility functions for the concrete similarity 
 * implementations. Those mainly relate to the computation of 
 * the size of a behavioural relation and the computation
 * of the intersection of two behavioural relations.
 * 
 * Note that these computations are cached for relation sets. 
 * Cache may be emptied by calling <code>invalidateCache()</code>.
 * 
 * @author matthias.weidlich
 *
 */
public abstract class AbstractRelSetSimilarity implements RelSetSimilarity {
	
	/**
	 * The cache for the size of a certain relation of a certain 
	 * relation set.
	 */
	private Map<RelSet,Map<RelSetType,Integer>> relationChache = new HashMap<RelSet,Map<RelSetType,Integer>>();
	
	/**
	 * Standard method for the name of a similarity. Simply returns the class name.
	 */
	public String getName() {
		return this.getClass().getName();
	}
	
	/**
	 * Computes the size of the intersection of a relation of two aligned relation sets.
	 * 
	 * @param alignment, establishes the relation between two relation sets
	 * @param relation, the type of the relation for which the size of the intersection is determined
	 * @return the size of the intersection of the relations of given type 
	 */
	protected int getSizeOfIntersectionOfRelation(RelSetAlignment alignment, RelSetType relation) {
		return getSizeOfIntersectionOfTwoRelations(alignment,relation,relation);
	}

	/**
	 * Computes the size of the intersection of two given relations of two aligned relation sets.
	 * 
	 * @param alignment, establishes the relation between two relation sets
	 * @param relation1, the type of the considered relation in the first model
	 * @param relation2, the type fo the considered relation in the second model
	 * @return the size of the intersection of the two relations of given type 
	 */
	protected int getSizeOfIntersectionOfTwoRelations(RelSetAlignment alignment, RelSetType relation1, RelSetType relation2) {
		int sizeOfIntersection = 0;
		
		for (Node n1 : alignment.getFirstRelationSet().getNodes()) {
			if (n1 instanceof Place) continue;
			if (((Transition)n1).equals(PetriNet.SILENT_LABEL)) continue;
			
			for (Node n2 : alignment.getFirstRelationSet().getNodes()) {
				if (n1 instanceof Place) continue;
				if (((Transition)n1).equals(PetriNet.SILENT_LABEL)) continue;
				
				if (!alignment.getFirstRelationSet().getRelationForNodes(n1, n2).equals(relation1)) continue;
				
				if (alignment.getAlignedVerticesOfFirstGraph().contains(n1) && alignment.getAlignedVerticesOfFirstGraph().contains(n2)) {
					if (alignment.getSecondRelationSet().getRelationForNodes(alignment.getCorrespondingVerticesForVertexOfFirstGraph(n1).iterator().next(), alignment.getCorrespondingVerticesForVertexOfFirstGraph(n2).iterator().next()).equals(relation2)) {
						sizeOfIntersection++;
					}
				}
			}
		}			
		return sizeOfIntersection;
	}
	
	/**
	 * Computes the size of a given relation of the given relation set.
	 * 
	 * @param rs, the relation set
	 * @param relation, the type of the respective relation
	 * @return the size of the relation in the given relation set
	 */
	protected int getSizeOfRelation(RelSet rs, RelSetType relation) {
		if (!relationChache.containsKey(rs))
			relationChache.put(rs,new HashMap<RelSetType, Integer>());

		if (relationChache.get(rs).containsKey(relation))
			return relationChache.get(rs).get(relation);

		int sizeOfRelation = 0;
		
		for (Node n1 : rs.getNodes()) {
			if (n1 instanceof Place) continue;
			if (((Transition)n1).equals(PetriNet.SILENT_LABEL)) continue;
			
			for (Node n2 : rs.getNodes()) {
				if (n1 instanceof Place) continue;
				if (((Transition)n1).equals(PetriNet.SILENT_LABEL)) continue;

				if (rs.getRelationForNodes(n1,n2).equals(relation))
					sizeOfRelation++;
			}
		}
		// put into cache
		relationChache.get(rs).put(relation,sizeOfRelation);
		
		// leverage symmetries to fill more details into the cache 
		if (relation.equals(RelSetType.Order))
			relationChache.get(rs).put(RelSetType.ReverseOrder,sizeOfRelation);
		if (relation.equals(RelSetType.ReverseOrder))
			relationChache.get(rs).put(RelSetType.Order,sizeOfRelation);
		
		return sizeOfRelation;
	}
	
	/**
	 * Resets the internal cache that stores the sizes of relations for relation sets.
	 */
	public void invalidateCache() {
		relationChache = new HashMap<RelSet,Map<RelSetType,Integer>>();		
	}
	
}
