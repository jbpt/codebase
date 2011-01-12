/**
 * Copyright (c) 2009 Matthias Weidlich
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.hpi.bpt.process.petri.bp;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
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
	public static String getSymbolForRelation(CharacteristicRelationType rel) {
		String s = BehaviouralProfile.getSymbolForRelation(rel);
		return (s.isEmpty()) ? ">>" : s;
	}

	
	/**
	 * Checks equality for two causal behavioural profiles
	 * 
	 * Returns false, if both matrices are not based on the same
	 * Petri net.
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

