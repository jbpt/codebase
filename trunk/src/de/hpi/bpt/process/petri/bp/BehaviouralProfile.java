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
 * Captures the behavioural profile of a Petri net for a given
 * set of nodes. 
 * 
 * @author matthias.weidlich
 *
 */
public class BehaviouralProfile {
	
	/**
	 * The relations of the behavioural profile. All relations
	 * are mutually exclusive.
	 */
	public enum CharacteristicRelationType {
		StrictOrder,ReverseStrictOrder,InterleavingOrder,Exclusive,None
	}

	/**
	 * The Petri net for which this class captures the behavioural profile- 
	 */
	protected PetriNet pn;
	
	/** The behavioural profile is captured for a dedicated set of nodes
	 * of the Petri net, e.g., only transitions or only labelled transitions.
	 * This list defines the respective nodes.
	 */
	protected List<Node> nodes;
	
	/**
	 * The matrix that captures the actual relations of the behavioural profile
	 * for the Cartesian product of the respective nodes.
	 */
	protected CharacteristicRelationType[][] matrix;

	/**
	 * Returns the reverse relation for a profile relation, if defined. A reverse
	 * relation is defined solely for the order relations.
	 * 
	 * Strict order -> Reverse strict order
	 * Reverse strict order -> Strict order
	 * 
	 * @param behavioural profile relation
	 * @return the reverse relation, if defined, the original relation, else
	 */
	public static CharacteristicRelationType getReverseRelation(CharacteristicRelationType rel) {
		if (rel.equals(CharacteristicRelationType.StrictOrder))
			return CharacteristicRelationType.ReverseStrictOrder;
		if (rel.equals(CharacteristicRelationType.ReverseStrictOrder))
			return CharacteristicRelationType.StrictOrder;
		
		return rel;
	}
	
	/**
	 * Returns a short string representation for a profile relation.

	 * @param behavioural profile relation
	 * @return string representation for a profile relation
	 */
	public static String getSymbolForRelation(CharacteristicRelationType rel) {
		if (rel.equals(CharacteristicRelationType.StrictOrder))
			return "->";
		if (rel.equals(CharacteristicRelationType.ReverseStrictOrder))
			return "<-";
		if (rel.equals(CharacteristicRelationType.Exclusive))
			return "+";
		if (rel.equals(CharacteristicRelationType.InterleavingOrder))
			return "||";
		
		return "";
	}

	public CharacteristicRelationType[][] getMatrix() {
		return matrix;
	}

	public void setMatrix(CharacteristicRelationType[][] matrix) {
		this.matrix = matrix;
	}

	public List<Node> getNodes() {
		return this.nodes;
	}
	
	/**
	 * Creates a behavioural profile structure for a given Petri net and 
	 * a dedicated list of nodes of the Petri net.
	 * 
	 * @param the Petri net
	 * @param a list of nodes of the Petri net
	 */
	public BehaviouralProfile(PetriNet pn, List<Node> nodes) {
		this.pn = pn;
		this.nodes = nodes;
		this.matrix = new CharacteristicRelationType[this.nodes.size()][this.nodes.size()];
	}
	
	/**
	 * Creates a behavioural profile structure for a given Petri net and 
	 * a dedicated collection of nodes of the Petri net.
	 * 
	 * Wrapper method that creates a list from the given collection.
	 * 
	 * @param the Petri net
	 * @param a collection of nodes of the Petri net
	 */
	public BehaviouralProfile(PetriNet pn, Collection<Node> nodes) {
		this(pn,new ArrayList<Node>(nodes));
	}

	
	/**
	 * Creates a behavioural profile structure for a given number of nodes. Use this 
	 * constructor solely in case a behavioural profile structure that is not related 
	 * to a specific Petri net is needed. 
	 * 
	 * @param size, i.e., number of nodes of the behavioural profile structure
	 */
	public BehaviouralProfile(int size) {
		this.matrix = new CharacteristicRelationType[size][size];
	}
	
	public PetriNet getNet() {
		return this.pn;
	}
	
	/**
	 * Checks whether two given nodes are in interleaving order.
	 * 
	 * @param n1
	 * @param n2
	 * @return true, if both nodes are in interleaving order
	 */
	public boolean areInterleaving(Node n1, Node n2) {
		int index1 = this.nodes.indexOf(n1);
		int index2 = this.nodes.indexOf(n2);
		if (index1 == -1 || index2 == -1)
			throw new InvalidParameterException("The profile is not defined for the respective nodes.");
		return matrix[index1][index2].equals(CharacteristicRelationType.InterleavingOrder);
	}

	/**
	 * Checks whether two given nodes are exclusive.
	 * 
	 * @param n1
	 * @param n2
	 * @return true, if both nodes are exclusive
	 */
	public boolean areExclusive(Node n1, Node n2) {
		int index1 = this.nodes.indexOf(n1);
		int index2 = this.nodes.indexOf(n2);
		if (index1 == -1 || index2 == -1)
			throw new InvalidParameterException("The profile is not defined for the respective nodes.");
		return matrix[index1][index2].equals(CharacteristicRelationType.Exclusive);
	}

	/**
	 * Checks whether two given nodes are in strict order.
	 * 
	 * @param n1
	 * @param n2
	 * @return true, if both nodes are in strict order
	 */
	public boolean areInStrictOrder(Node n1, Node n2) {
		int index1 = this.nodes.indexOf(n1);
		int index2 = this.nodes.indexOf(n2);
		if (index1 == -1 || index2 == -1)
			throw new InvalidParameterException("The profile is not defined for the respective nodes.");
		return matrix[index1][index2].equals(CharacteristicRelationType.StrictOrder);
	}

	/**
	 * Returns the behavioural relation for two given nodes.
	 * 
	 * @param n1
	 * @param n2
	 * @return the relation of the behavioural profile for the nodes
	 */
	public CharacteristicRelationType getRelationForNodes(Node n1, Node n2) {
		int index1 = this.nodes.indexOf(n1);
		int index2 = this.nodes.indexOf(n2);
		if (index1 == -1 || index2 == -1)
			throw new InvalidParameterException("The profile is not defined for the respective nodes.");
		return matrix[index1][index2];
	}
	
	/**
	 * Returns the behavioural relation for the two nodes that are identified 
	 * by their index in the list of nodes for which the profile is defined.
	 * 
	 * @param index1
	 * @param index2
	 * @return the relation of the behavioural profile for the nodes identified by the indices
	 */
	public CharacteristicRelationType getRelationForIndex(int index1, int index2) {
		return matrix[index1][index2];
	}

	/**
	 * Returns all nodes that are in a given behavioural relation with a given node.
	 * 
	 * @param a node
	 * @param a behavioural relation
	 * @return all nodes in the respective behavioural relation with the given node
	 */
	public Collection<Node> getNodesInRelation(Node n, CharacteristicRelationType relationType) {
		Collection<Node> nodes = new ArrayList<Node>();
		int index = this.nodes.indexOf(n);
		
		for (int i = 0; i < matrix.length; i++) {
			if (matrix[index][i].equals(relationType)) {
				nodes.add(this.nodes.get(i));
			}
		}
		return nodes;
	}
	
	/**
	 * Dumps all nodes in a given behavioural relation to the standard output.
	 * 
	 * @param a behaivoural relation
	 */
	public void printAllNodes(CharacteristicRelationType relationType) {
		for(Node n1 : this.nodes) {
			int index1 = this.nodes.indexOf(n1);
			for(Node n2 : this.nodes) {
				int index2 = this.nodes.indexOf(n2);
				if (index2 > index1)
					continue;
				if (matrix[index1][index2].equals(relationType))
					System.out.println(relationType + " -- " + n1 + " : " + n2);
			}
		}
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("------------------------------------------\n");
		sb.append("Behavioural Profile Matrix\n");
		sb.append("------------------------------------------\n");
		for (int k = 0; k < matrix.length; k++) {
			for (int row = 0; row < matrix.length; row++) {
				sb.append(matrix[row][k] + " , ");
			}
			sb.append("\n");
		}
		sb.append("------------------------------------------\n");
		return sb.toString();
	}
	
	/**
	 * Checks equality for two behavioural profiles
	 * 
	 * Returns false, if both matrices are not based on the same
	 * Petri net.
	 * 
	 * @param profile that should be compared
	 * @return true, if the given profile is equivalent to this profile
	 */
	public boolean equals (BehaviouralProfile profile) {
		if (!this.pn.equals(profile.getNet()))
			return false;
		
		boolean equal = true;
		
		for(Node n1 : this.nodes) {
			for(Node n2 : this.nodes) {
				equal &= this.getRelationForNodes(n1, n2).equals(profile.getRelationForNodes(n1, n2));
			}
		}
		return equal;
	}

}
