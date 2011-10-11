package de.hpi.bpt.process.petri.bp;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;

/**
 * Captures the causal behavioural profile for a Petri net. The causal profile
 * adds the co-occurrence relation to the behavioural profile.
 * 
 * @author matthias.weidlich
 *
 */
public class CausalBehaviouralProfile extends BehaviouralProfile {

	/**
	 * Matrix that captures co-occurrence for the Cartesian product of nodes 
	 * over which the profile is defined. Those are defined by a list member of 
	 * the behavioral profile.
	 */
	protected boolean[][] cooccurrenceMatrix;
	
	/**
	 * Create a causal behavioural profile structure for a given Petri net and a 
	 * given list of nodes.
	 * 
	 * @param pn
	 * @param nodes
	 */
	public CausalBehaviouralProfile(PetriNet pn, List<Node> nodes) {
		super(pn,nodes);
		this.cooccurrenceMatrix = new boolean[super.nodes.size()][super.nodes.size()];
	}
	
	/**
	 * Creates a causal behavioural profile structure for a given Petri net and 
	 * a dedicated collection of nodes of the Petri net.
	 * 
	 * Wrapper method that creates a list from the given collection.
	 * 
	 * @param the Petri net
	 * @param a collection of nodes of the Petri net
	 */
	public CausalBehaviouralProfile(PetriNet pn, Collection<Node> nodes) {
		this(pn,new ArrayList<Node>(nodes));
	}

	
	/**
	 * Checks whether two given nodes are co-occurring.
	 * 
	 * @param n1
	 * @param n2
	 * @return true, if both nodes are co-occurring
	 */
	public boolean areCooccurring(Node n1, Node n2) {
		int index1 = this.nodes.indexOf(n1);
		int index2 = this.nodes.indexOf(n2);
		if (index1 == -1 || index2 == -1)
			throw new InvalidParameterException("The profile is not defined for the respective nodes.");
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
	 * Petri net or on the same set of nodes. This check is done
	 * when equivalence is determined for the relations of the 
	 * behavioural profile.
	 * 
	 * @param profile that should be compared
	 * @return true, if the given profile is equivalent to this profile
	 */
	public boolean equals (CausalBehaviouralProfile profile) {
		boolean equal = super.equals(profile);
		if (!equal)
			return equal;
		
		for(Node n1 : super.nodes) {
			for(Node n2 : super.nodes) {
				equal &= (this.areCooccurring(n1, n2) == profile.areCooccurring(n1, n2));
			}
		}
		return equal;
	}
	
	/**
	 * Checks equality for two causal behavioural profiles only for the
	 * shared nodes. That is, we assess whether the profiles define
	 * equal relations (including co-occurrence) for all nodes for 
	 * which both profiles are defined.
	 * 
	 * Returns false, if both matrices are not based on the same
	 * Petri net. This check is done
	 * when equivalence is determined for the relations of the 
	 * behavioural profile.
	 * 
	 * @param profile that should be compared
	 * @return true, if the given profile is equivalent to this profile
	 */
	public boolean equalsForSharedNodes (CausalBehaviouralProfile profile) {
		boolean equal = super.equals(profile);
		if (!equal)
			return equal;
		
		HashSet<Node> sharedNodes = new HashSet<Node>(this.getNodes());
		sharedNodes.retainAll(profile.getNodes()); 
		
		for(Node n1 : sharedNodes) {
			for(Node n2 : sharedNodes) {
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

