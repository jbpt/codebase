package de.hpi.bpt.process.petri.bp;

import java.util.ArrayList;
import java.util.List;

import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.bp.BehaviouralProfile.CharacteristicRelationType;


/**
 * This class implements set-algebraic operations and relations for 
 * behavioural profiles. It comprises only those operations and relations
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
	public static boolean isEqual(BPAlignment alignment) throws IllegalArgumentException {
		if (alignment.isOverlapping() || !alignment.isFunctional() || !alignment.isInjective())
			throw new IllegalArgumentException("Alignment does not satisfy assumptions of set algebra.");
		
		for (Node v1 : alignment.getAlignedVerticesOfFirstGraph()) {
			for (Node v2 : alignment.getAlignedVerticesOfFirstGraph()) {
				CharacteristicRelationType relation1 = alignment.getFirstProfile().getRelationForNodes(v1, v2);
				CharacteristicRelationType relation2 = alignment.getSecondProfile().getRelationForNodes(
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
	public static boolean secondSubsumesFirst(BPAlignment alignment) throws IllegalArgumentException {
		if (alignment.isOverlapping() || !alignment.isFunctional() || !alignment.isInjective())
			throw new IllegalArgumentException("Alignment does not satisfy assumptions of set algebra.");
		
		for (Node v1 : alignment.getAlignedVerticesOfSecondGraph()) {
			for (Node v2 : alignment.getAlignedVerticesOfSecondGraph()) {
				CharacteristicRelationType relation1 = alignment.getSecondProfile().getRelationForNodes(v1, v2);
				CharacteristicRelationType relation2 = alignment.getFirstProfile().getRelationForNodes(
						alignment.getCorrespondingVerticesForVertexOfSecondGraph(v1).iterator().next(),
						alignment.getCorrespondingVerticesForVertexOfSecondGraph(v2).iterator().next());

				if (relation1.equals(CharacteristicRelationType.Exclusive) && !(relation2.equals(CharacteristicRelationType.Exclusive)))
					return false;
				
				if (relation1.equals(CharacteristicRelationType.StrictOrder) && !(relation2.equals(CharacteristicRelationType.Exclusive) || relation2.equals(CharacteristicRelationType.StrictOrder)))
					return false;

				if (relation1.equals(CharacteristicRelationType.ReverseStrictOrder) && !(relation2.equals(CharacteristicRelationType.Exclusive) || relation2.equals(CharacteristicRelationType.ReverseStrictOrder)))
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
	public static boolean firstSubsumesSecond(BPAlignment alignment) throws IllegalArgumentException {
		if (alignment.isOverlapping() || !alignment.isFunctional() || !alignment.isInjective())
			throw new IllegalArgumentException("Alignment does not satisfy assumptions of set algebra.");
		
		for (Node v1 : alignment.getAlignedVerticesOfFirstGraph()) {
			for (Node v2 : alignment.getAlignedVerticesOfFirstGraph()) {
				CharacteristicRelationType relation1 = alignment.getFirstProfile().getRelationForNodes(v1, v2);
				CharacteristicRelationType relation2 = alignment.getSecondProfile().getRelationForNodes(
						alignment.getCorrespondingVerticesForVertexOfFirstGraph(v1).iterator().next(),
						alignment.getCorrespondingVerticesForVertexOfFirstGraph(v2).iterator().next());

				if (relation1.equals(CharacteristicRelationType.Exclusive) && !(relation2.equals(CharacteristicRelationType.Exclusive)))
					return false;
				
				if (relation1.equals(CharacteristicRelationType.StrictOrder) && !(relation2.equals(CharacteristicRelationType.Exclusive) || relation2.equals(CharacteristicRelationType.StrictOrder)))
					return false;

				if (relation1.equals(CharacteristicRelationType.ReverseStrictOrder) && !(relation2.equals(CharacteristicRelationType.Exclusive) || relation2.equals(CharacteristicRelationType.ReverseStrictOrder)))
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
	public static BehaviouralProfile intersection(BPAlignment alignment) throws IllegalArgumentException {
		if (alignment.isOverlapping() || !alignment.isFunctional() || !alignment.isInjective())
			throw new IllegalArgumentException("Alignment does not satisfy assumptions of set algebra.");

		List<Node> nodeList = new ArrayList<Node>(alignment.getAlignedVerticesOfFirstGraph());
		BehaviouralProfile profile = new BehaviouralProfile(alignment.getFirstProfile().getNet(),nodeList);
		CharacteristicRelationType[][] matrix = profile.getMatrix();
		
		for(Node v1 : nodeList) {
			int index1 = profile.getNodes().indexOf(v1);
			for(Node v2 : nodeList) {
				int index2 = profile.getNodes().indexOf(v2);
				
				/*
				 * The behavioural profile matrix is symmetric. Therefore, we 
				 * need to traverse only half of the entries.
				 */
				if (index2 > index1)
					continue;
				
				CharacteristicRelationType relation1 = alignment.getFirstProfile().getRelationForNodes(v1, v2);
				CharacteristicRelationType relation2 = alignment.getSecondProfile().getRelationForNodes(
						alignment.getCorrespondingVerticesForVertexOfFirstGraph(v1).iterator().next(),
						alignment.getCorrespondingVerticesForVertexOfFirstGraph(v2).iterator().next());
				
				
				if (relation1.equals(CharacteristicRelationType.Exclusive) ||
						relation2.equals(CharacteristicRelationType.Exclusive) ||
						(relation1.equals(CharacteristicRelationType.StrictOrder) && relation2.equals(CharacteristicRelationType.ReverseStrictOrder)) ||
						(relation1.equals(CharacteristicRelationType.ReverseStrictOrder) && relation2.equals(CharacteristicRelationType.StrictOrder))) {
					
					matrix[index1][index2] = CharacteristicRelationType.Exclusive;
					matrix[index2][index1] = CharacteristicRelationType.Exclusive;
				}
				else if ((relation1.equals(CharacteristicRelationType.StrictOrder) && (relation2.equals(CharacteristicRelationType.StrictOrder) || relation2.equals(CharacteristicRelationType.InterleavingOrder))) ||
						(relation2.equals(CharacteristicRelationType.StrictOrder) && (relation1.equals(CharacteristicRelationType.StrictOrder) || relation1.equals(CharacteristicRelationType.InterleavingOrder)))) {
					
					matrix[index1][index2] = CharacteristicRelationType.StrictOrder;
					matrix[index2][index1] = CharacteristicRelationType.ReverseStrictOrder;
				}
				else if (relation1.equals(CharacteristicRelationType.InterleavingOrder) && relation2.equals(CharacteristicRelationType.InterleavingOrder)) {
					matrix[index1][index2] = CharacteristicRelationType.InterleavingOrder;
					matrix[index2][index1] = CharacteristicRelationType.InterleavingOrder;
				}
				else {
					matrix[index1][index2] = CharacteristicRelationType.ReverseStrictOrder;
					matrix[index2][index1] = CharacteristicRelationType.StrictOrder;
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
	public static BehaviouralProfile union(BPAlignment alignment) throws IllegalArgumentException {
		if (alignment.isOverlapping() || !alignment.isFunctional() || !alignment.isInjective())
			throw new IllegalArgumentException("Alignment does not satisfy assumptions of set algebra.");

		List<Node> nodeList = new ArrayList<Node>(alignment.getAlignedVerticesOfFirstGraph());
		BehaviouralProfile profile = new BehaviouralProfile(alignment.getFirstProfile().getNet(),nodeList);
		CharacteristicRelationType[][] matrix = profile.getMatrix();
		
		for(Node v1 : nodeList) {
			int index1 = profile.getNodes().indexOf(v1);
			for(Node v2 : nodeList) {
				int index2 = profile.getNodes().indexOf(v2);
				
				/*
				 * The behavioural profile matrix is symmetric. Therefore, we 
				 * need to traverse only half of the entries.
				 */
				if (index2 > index1)
					continue;
				
				CharacteristicRelationType relation1 = alignment.getFirstProfile().getRelationForNodes(v1, v2);
				CharacteristicRelationType relation2 = alignment.getSecondProfile().getRelationForNodes(
						alignment.getCorrespondingVerticesForVertexOfFirstGraph(v1).iterator().next(),
						alignment.getCorrespondingVerticesForVertexOfFirstGraph(v2).iterator().next());
				
				
				if (relation1.equals(CharacteristicRelationType.InterleavingOrder) ||
						relation2.equals(CharacteristicRelationType.InterleavingOrder) ||
						(relation1.equals(CharacteristicRelationType.StrictOrder) && relation2.equals(CharacteristicRelationType.ReverseStrictOrder)) ||
						(relation1.equals(CharacteristicRelationType.ReverseStrictOrder) && relation2.equals(CharacteristicRelationType.StrictOrder))) {
					
					matrix[index1][index2] = CharacteristicRelationType.InterleavingOrder;
					matrix[index2][index1] = CharacteristicRelationType.InterleavingOrder;
				}
				else if ((relation1.equals(CharacteristicRelationType.StrictOrder) && (relation2.equals(CharacteristicRelationType.StrictOrder) || relation2.equals(CharacteristicRelationType.Exclusive))) ||
						(relation2.equals(CharacteristicRelationType.StrictOrder) && (relation1.equals(CharacteristicRelationType.StrictOrder) || relation1.equals(CharacteristicRelationType.Exclusive)))) {
					
					matrix[index1][index2] = CharacteristicRelationType.StrictOrder;
					matrix[index2][index1] = CharacteristicRelationType.ReverseStrictOrder;
				}
				else if (relation1.equals(CharacteristicRelationType.Exclusive) && relation2.equals(CharacteristicRelationType.Exclusive)) {
					matrix[index1][index2] = CharacteristicRelationType.Exclusive;
					matrix[index2][index1] = CharacteristicRelationType.Exclusive;
				}
				else {
					matrix[index1][index2] = CharacteristicRelationType.ReverseStrictOrder;
					matrix[index2][index1] = CharacteristicRelationType.StrictOrder;
				}
			}
		}
		return profile;
	}
	
}
