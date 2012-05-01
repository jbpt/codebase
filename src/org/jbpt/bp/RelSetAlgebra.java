package org.jbpt.bp;

import java.util.ArrayList;
import java.util.List;

import org.jbpt.alignment.Alignment;
import org.jbpt.alignment.IEntity;
import org.jbpt.alignment.IEntityModel;



/**
 * This class implements set-algebraic operations and relations for 
 * relation sets. It comprises only those operations and relations
 * that are defined for pairs of aligned relation sets. The emptiness check and 
 * the complement operation are defined in <code>RelSet</code>.
 * 
 * All methods are implemented for an alignment that is captured by 
 * <code>RelSetAlignment</code>. All methods require the alignment to be 
 * non-overlapping, functional, and injective. If this is not the case,
 * an <code>IllegalArgumentException</code> is thrown.
 * 
 * @author matthias.weidlich
 *
 */
public class RelSetAlgebra {
	
	/**
	 * Checks equivalence of the relation sets under the given alignment. That is,
	 * it checks whether all relations coincide for pairs of aligned nodes. The 
	 * alignment is required to comprise only non-overlapping, functional, and injective.
	 * 
	 * @param alignment, defined between two models and their relation sets
	 * @return true, if the aligned relation sets show equal relations
	 * @throws IllegalArgumentException, if alignment is overlapping, not functional, or not injective 
	 */
	public static <R extends RelSet<M, N>, M extends IEntityModel<N>, N extends IEntity> boolean isEqual(Alignment<R,N> alignment) throws IllegalArgumentException {
		if (alignment.isOverlapping() || !alignment.isFunctional() || !alignment.isInjective())
			throw new IllegalArgumentException("Alignment does not satisfy assumptions of set algebra.");
		
		for (N v1 : alignment.getAlignedEntitiesOfFirstModel()) {
			for (N v2 : alignment.getAlignedEntitiesOfFirstModel()) {
				RelSetType relation1 = alignment.getFirstModel().getRelationForEntities(v1, v2);
				RelSetType relation2 = alignment.getSecondModel().getRelationForEntities(
						alignment.getCorrespondingEntitiesForEntityOfFirstModel(v1).iterator().next(),
						alignment.getCorrespondingEntitiesForEntityOfFirstModel(v2).iterator().next());

				if (!relation1.equals(relation2))
					return false;
			}
		}
		
		return true;
	}

	/**
	 * Checks subsumption between the second and the first relation set of the 
	 * given alignment. That is, it checks whether all relations of the second
	 * relation set subsume those of the first set. The alignment is required to comprise 
	 * only non-overlapping, functional, and injective.
	 * 
	 * @param alignment, defined between two models and their relation sets
	 * @return true, if the second relation set subsumes the first set
	 * @throws IllegalArgumentException, if alignment is overlapping, not functional, or not injective 
	 */
	public static <R extends RelSet<M, N>, M extends IEntityModel<N>, N extends IEntity> boolean secondSubsumesFirst(Alignment<R,N> alignment) throws IllegalArgumentException {
		if (alignment.isOverlapping() || !alignment.isFunctional() || !alignment.isInjective())
			throw new IllegalArgumentException("Alignment does not satisfy assumptions of set algebra.");
		
		for (N v1 : alignment.getAlignedEntitiesOfSecondModel()) {
			for (N v2 : alignment.getAlignedEntitiesOfSecondModel()) {
				RelSetType relation1 = alignment.getSecondModel().getRelationForEntities(v1, v2);
				RelSetType relation2 = alignment.getFirstModel().getRelationForEntities(
						alignment.getCorrespondingEntitiesForEntityOfSecondModel(v1).iterator().next(),
						alignment.getCorrespondingEntitiesForEntityOfSecondModel(v2).iterator().next());

				if (relation1.equals(RelSetType.Exclusive) && !(relation2.equals(RelSetType.Exclusive)))
					return false;
				
				if (relation1.equals(RelSetType.Order) && !(relation2.equals(RelSetType.Exclusive) || relation2.equals(RelSetType.Order)))
					return false;

				if (relation1.equals(RelSetType.ReverseOrder) && !(relation2.equals(RelSetType.Exclusive) || relation2.equals(RelSetType.ReverseOrder)))
					return false;

			}
		}
		
		return true;

	}	

	/**
	 * Checks subsumption between the first and the second relation set of the 
	 * given alignment. That is, it checks whether all relations of the first
	 * relation set subsume those of the second set. The alignment is required to comprise 
	 * only non-overlapping, functional, and injective.
	 * 
	 * @param alignment, defined between two models and their relation sets
	 * @return true, if the first relation set subsumes the second relation set
	 * @throws IllegalArgumentException, if alignment is overlapping, not functional, or not injective 
	 */
	public static <R extends RelSet<M, N>, M extends IEntityModel<N>, N extends IEntity> boolean firstSubsumesSecond(Alignment<R,N> alignment) throws IllegalArgumentException {
		if (alignment.isOverlapping() || !alignment.isFunctional() || !alignment.isInjective())
			throw new IllegalArgumentException("Alignment does not satisfy assumptions of set algebra.");
		
		for (N v1 : alignment.getAlignedEntitiesOfFirstModel()) {
			for (N v2 : alignment.getAlignedEntitiesOfFirstModel()) {
				RelSetType relation1 = alignment.getFirstModel().getRelationForEntities(v1, v2);
				RelSetType relation2 = alignment.getSecondModel().getRelationForEntities(
						alignment.getCorrespondingEntitiesForEntityOfFirstModel(v1).iterator().next(),
						alignment.getCorrespondingEntitiesForEntityOfFirstModel(v2).iterator().next());

				if (relation1.equals(RelSetType.Exclusive) && !(relation2.equals(RelSetType.Exclusive)))
					return false;
				
				if (relation1.equals(RelSetType.Order) && !(relation2.equals(RelSetType.Exclusive) || relation2.equals(RelSetType.Order)))
					return false;

				if (relation1.equals(RelSetType.ReverseOrder) && !(relation2.equals(RelSetType.Exclusive) || relation2.equals(RelSetType.ReverseOrder)))
					return false;

			}
		}
		
		return true;
	}

	/**
	 * Constructs the intersection of the relation sets under the 
	 * given alignment. That is, it returns a relation set that combines the strictest
	 * relations of both sets used as input for all pairs of aligned entities. 
	 * The alignment is required to comprise only non-overlapping, functional, 
	 * and injective.
	 * 
	 * @param alignment, defined between two models and their relation sets
	 * @throws IllegalArgumentException, if alignment is overlapping, not functional, or not injective 
	 */
	public static <R extends RelSet<M, N>, M extends IEntityModel<N>, N extends IEntity> void fillIntersection(Alignment<R,N> alignment, R relSet) throws IllegalArgumentException {
		if (alignment.isOverlapping() || !alignment.isFunctional() || !alignment.isInjective())
			throw new IllegalArgumentException("Alignment does not satisfy assumptions of set algebra.");

		List<N> entityList = new ArrayList<N>(alignment.getAlignedEntitiesOfFirstModel());
		RelSetType[][] matrix = relSet.getMatrix();
		
		for(N v1 : entityList) {
			int index1 = relSet.getEntities().indexOf(v1);
			for(N v2 : entityList) {
				int index2 = relSet.getEntities().indexOf(v2);
				
				/*
				 * The behavioural profile matrix is symmetric. Therefore, we 
				 * need to traverse only half of the entries.
				 */
				if (index2 > index1)
					continue;
				
				RelSetType relation1 = alignment.getFirstModel().getRelationForEntities(v1, v2);
				RelSetType relation2 = alignment.getSecondModel().getRelationForEntities(
						alignment.getCorrespondingEntitiesForEntityOfFirstModel(v1).iterator().next(),
						alignment.getCorrespondingEntitiesForEntityOfFirstModel(v2).iterator().next());
				
				
				if (relation1.equals(RelSetType.Exclusive) ||
						relation2.equals(RelSetType.Exclusive) ||
						(relation1.equals(RelSetType.Order) && relation2.equals(RelSetType.ReverseOrder)) ||
						(relation1.equals(RelSetType.ReverseOrder) && relation2.equals(RelSetType.Order))) {
					
					matrix[index1][index2] = RelSetType.Exclusive;
					matrix[index2][index1] = RelSetType.Exclusive;
				}
				else if ((relation1.equals(RelSetType.Order) && (relation2.equals(RelSetType.Order) || relation2.equals(RelSetType.Interleaving))) ||
						(relation2.equals(RelSetType.Order) && (relation1.equals(RelSetType.Order) || relation1.equals(RelSetType.Interleaving)))) {
					
					matrix[index1][index2] = RelSetType.Order;
					matrix[index2][index1] = RelSetType.ReverseOrder;
				}
				else if (relation1.equals(RelSetType.Interleaving) && relation2.equals(RelSetType.Interleaving)) {
					matrix[index1][index2] = RelSetType.Interleaving;
					matrix[index2][index1] = RelSetType.Interleaving;
				}
				else {
					matrix[index1][index2] = RelSetType.ReverseOrder;
					matrix[index2][index1] = RelSetType.Order;
				}
			}
		}
	}
	
	/**
	 * Constructs the union of the relation sets under the 
	 * given alignment. That is, it returns a relation set that combines the weakest
	 * relations of both sets used as input for all pairs of aligned entities. 
	 * The alignment is required to comprise only non-overlapping, functional, 
	 * and injective.
	 * 
	 * @param alignment, defined between two models and their relation sets
	 * @throws IllegalArgumentException, if alignment is overlapping, not functional, or not injective 
	 */
	public static <R extends RelSet<M, N>, M extends IEntityModel<N>, N extends IEntity> void fillUnion(Alignment<R,N> alignment, R relSet) throws IllegalArgumentException {
		if (alignment.isOverlapping() || !alignment.isFunctional() || !alignment.isInjective())
			throw new IllegalArgumentException("Alignment does not satisfy assumptions of set algebra.");

		List<N> entityList = new ArrayList<N>(alignment.getAlignedEntitiesOfFirstModel());
		RelSetType[][] matrix = relSet.getMatrix();
		
		for(N v1 : entityList) {
			int index1 = relSet.getEntities().indexOf(v1);
			for(N v2 : entityList) {
				int index2 = relSet.getEntities().indexOf(v2);
				
				/*
				 * The behavioural profile matrix is symmetric. Therefore, we 
				 * need to traverse only half of the entries.
				 */
				if (index2 > index1)
					continue;
				
				RelSetType relation1 = alignment.getFirstModel().getRelationForEntities(v1, v2);
				RelSetType relation2 = alignment.getSecondModel().getRelationForEntities(
						alignment.getCorrespondingEntitiesForEntityOfFirstModel(v1).iterator().next(),
						alignment.getCorrespondingEntitiesForEntityOfFirstModel(v2).iterator().next());
				
				
				if (relation1.equals(RelSetType.Interleaving) ||
						relation2.equals(RelSetType.Interleaving) ||
						(relation1.equals(RelSetType.Order) && relation2.equals(RelSetType.ReverseOrder)) ||
						(relation1.equals(RelSetType.ReverseOrder) && relation2.equals(RelSetType.Order))) {
					
					matrix[index1][index2] = RelSetType.Interleaving;
					matrix[index2][index1] = RelSetType.Interleaving;
				}
				else if ((relation1.equals(RelSetType.Order) && (relation2.equals(RelSetType.Order) || relation2.equals(RelSetType.Exclusive))) ||
						(relation2.equals(RelSetType.Order) && (relation1.equals(RelSetType.Order) || relation1.equals(RelSetType.Exclusive)))) {
					
					matrix[index1][index2] = RelSetType.Order;
					matrix[index2][index1] = RelSetType.ReverseOrder;
				}
				else if (relation1.equals(RelSetType.Exclusive) && relation2.equals(RelSetType.Exclusive)) {
					matrix[index1][index2] = RelSetType.Exclusive;
					matrix[index2][index1] = RelSetType.Exclusive;
				}
				else {
					matrix[index1][index2] = RelSetType.ReverseOrder;
					matrix[index2][index1] = RelSetType.Order;
				}
			}
		}
	}
	
}
