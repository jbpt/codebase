package org.jbpt.bp.sim;

import org.jbpt.alignment.Alignment;
import org.jbpt.bp.RelSet;
import org.jbpt.bp.RelSetType;
import org.jbpt.hypergraph.abs.IEntity;
import org.jbpt.hypergraph.abs.IEntityModel;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;

/**
 * Abstract class for similarities for relation sets
 * that contains utility functions for the concrete similarity 
 * implementations. Those mainly relate to the computation of 
 * the size of a behavioural relation and the computation
 * of the intersection of two behavioural relations.
 * 
 * @author matthias.weidlich
 *
 */
public abstract class AbstractRelSetSimilarity<R extends RelSet<M, N>, M extends IEntityModel<N>, N extends IEntity> implements RelSetSimilarity<R,M,N> {
	
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
	protected int getSizeOfIntersectionOfRelation(Alignment<R,N> alignment, RelSetType relation) {
		return getSizeOfIntersectionOfTwoRelations(alignment,relation,relation);
	}

	/**
	 * Computes the size of the intersection of two given relations of two aligned relation sets.
	 * 
	 * @param alignment, establishes the relation between two relation sets
	 * @param relation1, the type of the considered relation in the first model
	 * @param relation2, the type of the considered relation in the second model
	 * @return the size of the intersection of the two relations of given type 
	 */
	protected int getSizeOfIntersectionOfTwoRelations(Alignment<R,N> alignment, RelSetType relation1, RelSetType relation2) {
		int sizeOfIntersection = 0;
		
		for (N n1 : alignment.getFirstModel().getEntities()) {
			if (n1 instanceof Place) continue;
			if (((Transition)n1).isSilent()) continue;
			
			for (N n2 : alignment.getFirstModel().getEntities()) {
				if (n2 instanceof Place) continue;
				if (((Transition)n2).isSilent()) continue;
				
				if (!alignment.getFirstModel().getRelationForEntities(n1, n2).equals(relation1)) continue;
				
				if (alignment.getAlignedEntitiesOfFirstModel().contains(n1) && alignment.getAlignedEntitiesOfFirstModel().contains(n2)) {
					if (alignment.getSecondModel().getRelationForEntities(alignment.getCorrespondingEntitiesForEntityOfFirstModel(n1).iterator().next(), alignment.getCorrespondingEntitiesForEntityOfFirstModel(n2).iterator().next()).equals(relation2)) {
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
	protected int getSizeOfRelation(R rs, RelSetType relation) {

		if (RelSetSizeCache.getInstance().containsEntry(rs,relation))
			return RelSetSizeCache.getInstance().getRelationSize(rs,relation);

		int sizeOfRelation = 0;
		
		for (N n1 : rs.getEntities()) {
			if (n1 instanceof Place) continue;
			if (((Transition)n1).isSilent()) continue;
			
			for (N n2 : rs.getEntities()) {
				if (n2 instanceof Place) continue;
				if (((Transition)n2).isSilent()) continue;

				if (rs.getRelationForEntities(n1,n2).equals(relation))
					sizeOfRelation++;
			}
		}
		// put into cache
		RelSetSizeCache.getInstance().addEntry(rs,relation,sizeOfRelation);
		
		// leverage symmetries to fill more details into the cache 
		if (relation.equals(RelSetType.Order))
			RelSetSizeCache.getInstance().addEntry(rs,RelSetType.ReverseOrder,sizeOfRelation);
		if (relation.equals(RelSetType.ReverseOrder))
			RelSetSizeCache.getInstance().addEntry(rs,RelSetType.Order,sizeOfRelation);
		
		return sizeOfRelation;
	}
	
	
}
