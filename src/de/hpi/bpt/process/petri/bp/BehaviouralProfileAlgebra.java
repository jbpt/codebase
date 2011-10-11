package de.hpi.bpt.process.petri.bp;

import java.util.ArrayList;
import java.util.List;

import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;


/**
 * This class implements set-algebraic operations and relations for 
 * behavioural profiles. Currently, it is limited to behaviuoral profiles
 * of Petri nets. It comprises only those operations and relations
 * that are defined for pairs of aligned profiles. The emptiness check and 
 * the complement operation are defined in <code>BehaviouralProfile</code>.
 * 
 * All methods are implemented for an alignment that is captured by 
 * <code>BPAlignment</code>. All methods require the alignment to be 
 * non-overlapping, functional, and injective. If this is not the case,
 * an <code>IllegalArgumentException</code> is thrown.
 * 
 * @author matthias.weidlich
 *
 */
public class BehaviouralProfileAlgebra {
	
	/**
	 * Checks equivalence of the behavioural profiles under the given alignment. That is,
	 * it checks whether all behavioural relations coincide for pairs of aligned nodes. The 
	 * alignment is required to comprise only non-overlapping, functional, and injective.
	 * 
	 * @param alignment, defined between two Petri nets and their behavioural profiles
	 * @return true, if the aligned profiles show equal relations
	 * @throws IllegalArgumentException, if alignment is overlapping, not functional, or not injective 
	 */
	public static boolean isEqual(RelSetAlignment alignment) throws IllegalArgumentException {
		if (alignment.isOverlapping() || !alignment.isFunctional() || !alignment.isInjective())
			throw new IllegalArgumentException("Alignment does not satisfy assumptions of set algebra.");
		
		for (Node v1 : alignment.getAlignedVerticesOfFirstGraph()) {
			for (Node v2 : alignment.getAlignedVerticesOfFirstGraph()) {
				RelSetType relation1 = alignment.getFirstRelationSet().getRelationForEntities(v1, v2);
				RelSetType relation2 = alignment.getSecondRelationSet().getRelationForEntities(
						alignment.getCorrespondingVerticesForVertexOfFirstGraph(v1).iterator().next(),
						alignment.getCorrespondingVerticesForVertexOfFirstGraph(v2).iterator().next());

				if (!relation1.equals(relation2))
					return false;
			}
		}
		
		return true;
	}

	/**
	 * Checks subsumption between the second and the first behavioural profile of the 
	 * given alignment. That is, it checks whether all behavioural relations of the second
	 * profile subsume those of the first profile. The alignment is required to comprise 
	 * only non-overlapping, functional, and injective.
	 * 
	 * @param alignment, defined between two Petri nets and their behavioural profiles
	 * @return true, if the second profile subsumes the first profile
	 * @throws IllegalArgumentException, if alignment is overlapping, not functional, or not injective 
	 */
	public static boolean secondSubsumesFirst(RelSetAlignment alignment) throws IllegalArgumentException {
		if (alignment.isOverlapping() || !alignment.isFunctional() || !alignment.isInjective())
			throw new IllegalArgumentException("Alignment does not satisfy assumptions of set algebra.");
		
		for (Node v1 : alignment.getAlignedVerticesOfSecondGraph()) {
			for (Node v2 : alignment.getAlignedVerticesOfSecondGraph()) {
				RelSetType relation1 = alignment.getSecondRelationSet().getRelationForEntities(v1, v2);
				RelSetType relation2 = alignment.getFirstRelationSet().getRelationForEntities(
						alignment.getCorrespondingVerticesForVertexOfSecondGraph(v1).iterator().next(),
						alignment.getCorrespondingVerticesForVertexOfSecondGraph(v2).iterator().next());

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
	 * Checks subsumption between the first and the second behavioural profile of the 
	 * given alignment. That is, it checks whether all behavioural relations of the first
	 * profile subsume those of the second profile. The alignment is required to comprise 
	 * only non-overlapping, functional, and injective.
	 * 
	 * @param alignment, defined between two Petri nets and their behavioural profiles
	 * @return true, if the first profile subsumes the second profile
	 * @throws IllegalArgumentException, if alignment is overlapping, not functional, or not injective 
	 */
	public static boolean firstSubsumesSecond(RelSetAlignment alignment) throws IllegalArgumentException {
		if (alignment.isOverlapping() || !alignment.isFunctional() || !alignment.isInjective())
			throw new IllegalArgumentException("Alignment does not satisfy assumptions of set algebra.");
		
		for (Node v1 : alignment.getAlignedVerticesOfFirstGraph()) {
			for (Node v2 : alignment.getAlignedVerticesOfFirstGraph()) {
				RelSetType relation1 = alignment.getFirstRelationSet().getRelationForEntities(v1, v2);
				RelSetType relation2 = alignment.getSecondRelationSet().getRelationForEntities(
						alignment.getCorrespondingVerticesForVertexOfFirstGraph(v1).iterator().next(),
						alignment.getCorrespondingVerticesForVertexOfFirstGraph(v2).iterator().next());

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
	 * Constructs the intersection of the behavioural profiles under the 
	 * given alignment. That is, it returns a profile that combines the strictest
	 * relations of both profiles used as input for all pairs of aligned nodes. 
	 * The alignment is required to comprise only non-overlapping, functional, 
	 * and injective.
	 * 
	 * @param alignment, defined between two Petri nets and their behavioural profiles
	 * @return behavioural profile that represents the intersection of the two profiles given as input
	 * @throws IllegalArgumentException, if alignment is overlapping, not functional, or not injective 
	 */
	public static BehaviouralProfile<PetriNet, Node> intersection(RelSetAlignment alignment) throws IllegalArgumentException {
		if (alignment.isOverlapping() || !alignment.isFunctional() || !alignment.isInjective())
			throw new IllegalArgumentException("Alignment does not satisfy assumptions of set algebra.");

		List<Node> nodeList = new ArrayList<Node>(alignment.getAlignedVerticesOfFirstGraph());
		BehaviouralProfile<PetriNet, Node> profile = new BehaviouralProfile<PetriNet, Node>(alignment.getFirstRelationSet().getModel(),nodeList);
		RelSetType[][] matrix = profile.getMatrix();
		
		for(Node v1 : nodeList) {
			int index1 = profile.getEntities().indexOf(v1);
			for(Node v2 : nodeList) {
				int index2 = profile.getEntities().indexOf(v2);
				
				/*
				 * The behavioural profile matrix is symmetric. Therefore, we 
				 * need to traverse only half of the entries.
				 */
				if (index2 > index1)
					continue;
				
				RelSetType relation1 = alignment.getFirstRelationSet().getRelationForEntities(v1, v2);
				RelSetType relation2 = alignment.getSecondRelationSet().getRelationForEntities(
						alignment.getCorrespondingVerticesForVertexOfFirstGraph(v1).iterator().next(),
						alignment.getCorrespondingVerticesForVertexOfFirstGraph(v2).iterator().next());
				
				
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
		return profile;
	}
	
	/**
	 * Constructs the union of the behavioural profiles under the 
	 * given alignment. That is, it returns a profile that combines the weakest
	 * relations of both profiles used as input for all pairs of aligned nodes. 
	 * The alignment is required to comprise only non-overlapping, functional, 
	 * and injective.
	 * 
	 * @param alignment, defined between two Petri nets and their behavioural profiles
	 * @return behavioural profile that represents the union of the two profiles given as input
	 * @throws IllegalArgumentException, if alignment is overlapping, not functional, or not injective 
	 */
	public static BehaviouralProfile<PetriNet, Node> union(RelSetAlignment alignment) throws IllegalArgumentException {
		if (alignment.isOverlapping() || !alignment.isFunctional() || !alignment.isInjective())
			throw new IllegalArgumentException("Alignment does not satisfy assumptions of set algebra.");

		List<Node> nodeList = new ArrayList<Node>(alignment.getAlignedVerticesOfFirstGraph());
		BehaviouralProfile<PetriNet, Node> profile = new BehaviouralProfile<PetriNet, Node>(alignment.getFirstRelationSet().getModel(),nodeList);
		RelSetType[][] matrix = profile.getMatrix();
		
		for(Node v1 : nodeList) {
			int index1 = profile.getEntities().indexOf(v1);
			for(Node v2 : nodeList) {
				int index2 = profile.getEntities().indexOf(v2);
				
				/*
				 * The behavioural profile matrix is symmetric. Therefore, we 
				 * need to traverse only half of the entries.
				 */
				if (index2 > index1)
					continue;
				
				RelSetType relation1 = alignment.getFirstRelationSet().getRelationForEntities(v1, v2);
				RelSetType relation2 = alignment.getSecondRelationSet().getRelationForEntities(
						alignment.getCorrespondingVerticesForVertexOfFirstGraph(v1).iterator().next(),
						alignment.getCorrespondingVerticesForVertexOfFirstGraph(v2).iterator().next());
				
				
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
		return profile;
	}
	
}
