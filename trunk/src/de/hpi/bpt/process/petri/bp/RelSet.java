package de.hpi.bpt.process.petri.bp;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;

public class RelSet {
	
	public static final int RELATION_FAR_LOOKAHEAD = 1000000000;
	
	protected int lookAhead = RELATION_FAR_LOOKAHEAD;
	
	/**
	 * The Petri net for which this class captures 
	 * the set of behavioural relations.
	 */
	protected PetriNet pn;
	
	/** 
	 * The relations are defined over a dedicated set of nodes
	 * of the Petri net, e.g., only transitions or only labelled transitions.
	 * This list defines the respective nodes.
	 */
	protected List<Node> nodes;

	/**
	 * The matrix that captures the actual relations 
	 * for the Cartesian product of the respective nodes.
	 */
	protected RelSetType[][] matrix;
	
	/**
	 * Returns the reverse relation for a relation, if defined. A reverse
	 * relation is defined solely for the order relations.
	 * 
	 * Order -> Reverse order
	 * Reverse order -> order
	 * 
	 * @param relation type
	 * @return type of the reverse relation, if defined, type of the original relation, else
	 */
	public static RelSetType getReverseRelation(RelSetType rel) {
		if (rel.equals(RelSetType.Order))
			return RelSetType.ReverseOrder;
		if (rel.equals(RelSetType.ReverseOrder))
			return RelSetType.Order;
		
		return rel;
	}

	/**
	 * Returns the complementary relation for a relation. The complement
	 * relation is defined according to the strictness hierarchy of 
	 * relations:
	 * 
	 * Order -> Reverse order
	 * Reverse order -> Order
	 * Exclusiveness -> Interleaving 
	 * Interleaving -> Exclusiveness
	 * 
	 * @param relation type
	 * @return type of the complement relation
	 */
	public static RelSetType getComplementRelation(RelSetType rel) {
		if (rel.equals(RelSetType.Order))
			return RelSetType.ReverseOrder;
		if (rel.equals(RelSetType.ReverseOrder))
			return RelSetType.Order;
		if (rel.equals(RelSetType.Interleaving))
			return RelSetType.Exclusive;

		return RelSetType.Interleaving;
	}

	public RelSetType[][] getMatrix() {
		return matrix;
	}

	public void setMatrix(RelSetType[][] matrix) {
		this.matrix = matrix;
	}

	public List<Node> getNodes() {
		return this.nodes;
	}
	
	public PetriNet getNet() {
		return this.pn;
	}

	public int getLookAhead() {
		return lookAhead;
	}

	public void setLookAhead(int lookAhead) {
		this.lookAhead = lookAhead;
	}

	/**
	 * Returns a short string representation for a behavioural relation.

	 * @param type of the behavioural relation
	 * @return string representation for a behavioural relation
	 */
	public static String getSymbolForRelation(RelSetType rel) {
		return rel.toString();
	}
	
	/**
	 * Creates a relation set for a given Petri net and 
	 * a dedicated list of nodes of the Petri net.
	 * 
	 * @param the Petri net
	 * @param a list of nodes of the Petri net
	 */
	public RelSet(PetriNet pn, List<Node> nodes) {
		this.pn = pn;
		this.nodes = nodes;
		this.matrix = new RelSetType[this.nodes.size()][this.nodes.size()];
	}
	
	/**
	 * Creates a relation set for a given Petri net and 
	 * a dedicated collection of nodes of the Petri net.
	 * 
	 * Wrapper method that creates a list from the given collection.
	 * 
	 * @param the Petri net
	 * @param a collection of nodes of the Petri net
	 */
	public RelSet(PetriNet pn, Collection<Node> nodes) {
		this(pn,new ArrayList<Node>(nodes));
	}

	public RelSet(PetriNet pn, Collection<Node> nodes, int lookAhead) {
		this(pn,new ArrayList<Node>(nodes));
		this.lookAhead = lookAhead;
	}

	
	/**
	 * Creates a relation set for a given number of nodes. Use this 
	 * constructor solely in case a relation set that is not related 
	 * to a specific Petri net is needed. 
	 * 
	 * @param size, i.e., number of nodes over which the relations are defined
	 */
	public RelSet(int size) {
		this.matrix = new RelSetType[size][size];
	}
	
	
	/**
	 * Checks whether two given nodes are interleaving.
	 * 
	 * @param n1
	 * @param n2
	 * @return true, if both nodes are interleaving 
	 */
	public boolean areInterleaving(Node n1, Node n2) {
		int index1 = this.nodes.indexOf(n1);
		int index2 = this.nodes.indexOf(n2);
		if (index1 == -1 || index2 == -1)
			throw new InvalidParameterException("The structure is not defined for the respective nodes.");
		return matrix[index1][index2].equals(RelSetType.Interleaving);
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
			throw new InvalidParameterException("The structure is not defined for the respective nodes.");
		return matrix[index1][index2].equals(RelSetType.Exclusive);
	}

	/**
	 * Checks whether two given nodes are ordered.
	 * 
	 * @param n1
	 * @param n2
	 * @return true, if both nodes are ordered
	 */
	public boolean areInOrder(Node n1, Node n2) {
		int index1 = this.nodes.indexOf(n1);
		int index2 = this.nodes.indexOf(n2);
		if (index1 == -1 || index2 == -1)
			throw new InvalidParameterException("The structure is not defined for the respective nodes.");
		return matrix[index1][index2].equals(RelSetType.Order);
	}

	/**
	 * Returns the behavioural relation for two given nodes.
	 * 
	 * @param n1
	 * @param n2
	 * @return the relation of the behavioural profile for the nodes
	 */
	public RelSetType getRelationForNodes(Node n1, Node n2) {
		int index1 = this.nodes.indexOf(n1);
		int index2 = this.nodes.indexOf(n2);
		if (index1 == -1 || index2 == -1)
			throw new InvalidParameterException("The structure is not defined for the respective nodes.");
		return matrix[index1][index2];
	}
	
	/**
	 * Returns the type of the behavioural relation for the two nodes that are identified 
	 * by their index in the list of nodes for which the relation set is defined.
	 * 
	 * @param index1
	 * @param index2
	 * @return the relation type of the relation set for the nodes identified by the indices
	 */
	public RelSetType getRelationForIndex(int index1, int index2) {
		return matrix[index1][index2];
	}

	/**
	 * Returns all nodes that are in a given behavioural relation with a given node.
	 * 
	 * @param a node
	 * @param a behavioural relation type
	 * @return all nodes in the respective behavioural relation with the given node
	 */
	public Collection<Node> getNodesInRelation(Node n, RelSetType relationType) {
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
	 * @param a behavioural relation type
	 */
	public void printAllNodes(RelSetType relationType) {
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
		sb.append("------------------------------------------------------\n");
		sb.append("Relation Set Matrix (Lookahead: "+this.lookAhead+") \n");
		sb.append("------------------------------------------------------\n");
		for (int k = 0; k < matrix.length; k++) {
			for (int row = 0; row < matrix.length; row++) {
				sb.append(matrix[row][k] + " , ");
			}
			sb.append("\n");
		}
		sb.append("------------------------------------------------------\n");
		return sb.toString();
	}
	
	/**
	 * Checks equality for two relation sets.
	 * 
	 * Returns false, if both matrices are not based on the same
	 * Petri net or on the same set of nodes.
	 * 
	 * @param profile that should be compared
	 * @return true, if the given relation set is equivalent to this relation set 
	 */
	public boolean equals(RelSet relationSet) {
		if (!this.pn.equals(relationSet.getNet()))
			return false;
		
		if (!this.getNodes().containsAll(relationSet.getNodes()) || !relationSet.getNodes().containsAll(this.getNodes()))
			return false;
		
		boolean equal = true;
		
		for(Node n1 : this.nodes) {
			for(Node n2 : this.nodes) {
				equal &= this.getRelationForNodes(n1, n2).equals(relationSet.getRelationForNodes(n1, n2));
			}
		}
		return equal;
	}
	
	/**
	 * Checks equality for two relation sets only for the
	 * shared nodes. That is, we assess whether the structures define
	 * equal relations for all nodes for which both structures are
	 * defined.
	 * 
	 * Returns false, if both matrices are not based on the same
	 * Petri net.
	 * 
	 * @param profile that should be compared
	 * @return true, if the given relation set is equivalent to this relation set for shared nodes
	 */
	public boolean equalsForSharedNodes(RelSet relationSet) {
		if (!this.pn.equals(relationSet.getNet()))
			return false;
		
		boolean equal = true;
		
		HashSet<Node> sharedNodes = new HashSet<Node>(this.getNodes());
		sharedNodes.retainAll(relationSet.getNodes()); 
		
		for(Node n1 : sharedNodes) {
			for(Node n2 : sharedNodes) {
				equal &= this.getRelationForNodes(n1, n2).equals(relationSet.getRelationForNodes(n1, n2));
//				if (!this.getRelationForNodes(n1, n2).equals(profile.getRelationForNodes(n1, n2))) {
//					System.out.println(n1);
//					System.out.println(n2);
//					System.out.println(this.getRelationForNodes(n1, n2));
//					System.out.println(profile.getRelationForNodes(n1, n2));
//					
//				}
			}
		}
		return equal;
	}
	
	/**
	 * Checks emptiness of a relation set. It is empty, if it defines 
	 * exclusiveness for all pairs of nodes.
	 * 
	 * @return true, if the relation set is empty
	 */
	public boolean isEmpty() {
		for (Node n1 : getNodes()) 
			for (Node n2 : getNodes()) 
				if (!getRelationForNodes(n1, n2).equals(RelSetType.Exclusive))
						return false;
		
		return true;
	}

	/**
	 * Returns the complement of the relation set . It is defined as the relation set
	 * that comprises the complement relations for all pairs of nodes.
	 * 
	 * @return 
	 */
	public RelSet getComplement() {
		RelSet cProfile = new RelSet(getNet(),getNodes());
		RelSetType[][] cMatrix = cProfile.getMatrix();
		
		for (int i = 0; i < matrix.length; i++) 
			for (int j = 0; j < matrix.length; j++)
				cMatrix[i][j] = getComplementRelation(matrix[i][j]);

		return cProfile;
	}

}
