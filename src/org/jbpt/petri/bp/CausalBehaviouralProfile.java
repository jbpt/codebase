package org.jbpt.petri.bp;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.jbpt.alignment.IEntity;
import org.jbpt.alignment.IEntityModel;


/**
 * Captures the causal behavioural profile for a model (e.g., a Petri net). 
 * The causal behavioural profile adds the co-occurrence relation to the 
 * behavioural profile.
 * 
 * @author matthias.weidlich
 *
 */
public class CausalBehaviouralProfile<M extends IEntityModel<N>, N extends IEntity> extends BehaviouralProfile<M, N> {

	/**
	 * Matrix that captures co-occurrence for the Cartesian product of entities 
	 * over which the profile is defined. Those are defined by a list member of 
	 * the behavioural profile.
	 */
	protected boolean[][] cooccurrenceMatrix;
	
	/**
	 * Create a causal behavioural profile structure for a given Petri net and a 
	 * given list of entities.
	 * 
	 * @param pn
	 * @param entities
	 */
	public CausalBehaviouralProfile(M model, List<N> entities) {
		super(model,entities);
		this.cooccurrenceMatrix = new boolean[super.entities.size()][super.entities.size()];
	}
	
	/**
	 * Creates a causal behavioural profile structure for a given Petri net and 
	 * a dedicated collection of entities of the Petri net.
	 * 
	 * Wrapper method that creates a list from the given collection.
	 * 
	 * @param the Petri net
	 * @param a collection of entities of the Petri net
	 */
	public CausalBehaviouralProfile(M model, Collection<N> entities) {
		this(model,new ArrayList<N>(entities));
	}

	
	/**
	 * Checks whether two given entities are co-occurring.
	 * 
	 * @param n1
	 * @param n2
	 * @return true, if both entities are co-occurring
	 */
	public boolean areCooccurring(N n1, N n2) {
		int index1 = this.entities.indexOf(n1);
		int index2 = this.entities.indexOf(n2);
		if (index1 == -1 || index2 == -1)
			throw new InvalidParameterException("The profile is not defined for the respective entities.");
		return cooccurrenceMatrix[index1][index2];
	}
	
	/**
	 * Returns a string representation for the behavioural profile relation or
	 * co-occurrence relation, respectively.
	 * @param a relation of the causal behavioural profile
	 * @return a string representation of the relation
	 */
	public static String getSymbolForRelation(RelSetType rel) {
		String s = BehaviouralProfile.getSymbolForRelation(rel);
		return (s.isEmpty()) ? ">>" : s;
	}

	
	/**
	 * Checks equality for two causal behavioural profiles
	 * 
	 * Returns false, if both matrices are not based on the same
	 * Petri net or on the same set of entities. This check is done
	 * when equivalence is determined for the relations of the 
	 * behavioural profile.
	 * 
	 * @param profile that should be compared
	 * @return true, if the given profile is equivalent to this profile
	 */
	public boolean equals (CausalBehaviouralProfile<M, N> profile) {
		boolean equal = super.equals(profile);
		if (!equal)
			return equal;
		
		for(N n1 : super.entities) {
			for(N n2 : super.entities) {
				equal &= (this.areCooccurring(n1, n2) == profile.areCooccurring(n1, n2));
			}
		}
		return equal;
	}
	
	/**
	 * Checks equality for two causal behavioural profiles only for the
	 * shared entities. That is, we assess whether the profiles define
	 * equal relations (including co-occurrence) for all entities for 
	 * which both profiles are defined.
	 * 
	 * Returns false, if both matrices are not based on the same
	 * model. This check is done
	 * when equivalence is determined for the relations of the 
	 * behavioural profile.
	 * 
	 * @param profile that should be compared
	 * @return true, if the given profile is equivalent to this profile
	 */
	public boolean equalsForSharedNodes (CausalBehaviouralProfile<M, N> profile) {
		boolean equal = super.equals(profile);
		if (!equal)
			return equal;
		
		HashSet<N> sharedNodes = new HashSet<N>(this.getEntities());
		sharedNodes.retainAll(profile.getEntities()); 
		
		for(N n1 : sharedNodes) {
			for(N n2 : sharedNodes) {
				equal &= (this.areCooccurring(n1, n2) == profile.areCooccurring(n1, n2));
			}
		}
		return equal;
	}



	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		for (int k = 0; k < cooccurrenceMatrix.length; k++) {
			for (int row = 0; row < cooccurrenceMatrix.length; row++) {
				sb.append(cooccurrenceMatrix[row][k] + " , ");
			}
			sb.append("\n");
		}
		sb.append("------------------------------------------\n");
		return sb.toString();
	}



	public boolean[][] getCooccurrenceMatrix() {
		return cooccurrenceMatrix;
	}



	public void setCooccurrenceMatrix(boolean[][] cooccurrenceMatrix) {
		this.cooccurrenceMatrix = cooccurrenceMatrix;
	}


}

