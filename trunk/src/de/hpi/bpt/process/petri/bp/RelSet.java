package de.hpi.bpt.process.petri.bp;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;


public class RelSet<M,N> {
	
	public static final int RELATION_FAR_LOOKAHEAD = 1000000000;
	
	protected int lookAhead = RELATION_FAR_LOOKAHEAD;
	
	/**
	 * The model (e.g., Petri net) for which this class captures 
	 * the set of behavioural relations.
	 */
	protected M model;
	
	/** 
	 * The relations are defined over a dedicated set of entities
	 * of the model, e.g., only transitions of a Petri net or only 
	 * labelled transitions of a Petri net.
	 * This list defines the respective entities.
	 */
	protected List<N> entities;

	/**
	 * The matrix that captures the actual relations 
	 * for the Cartesian product of the respective entities.
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

	public List<N> getEntities() {
		return this.entities;
	}
	
	public M getModel() {
		return this.model;
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
	 * Creates a relation set for a given model and 
	 * a dedicated list of entities of the model.
	 * 
	 * @param model, a model
	 * @param entities, a list of entities of the model
	 */
	public RelSet(M model, List<N> entities) {
		this.model = model;
		this.entities = entities;
		this.matrix = new RelSetType[this.entities.size()][this.entities.size()];
	}
	
	/**
	 * Creates a relation set for a given model and 
	 * a dedicated collection of entities of the model.
	 * 
	 * Wrapper method that creates a list from the given collection.
	 * 
	 * @param model, a model
	 * @param entities, a list of entities of the model
	 */
	public RelSet(M model, Collection<N> entities) {
		this(model,new ArrayList<N>(entities));
	}

	public RelSet(M model, Collection<N> entities, int lookAhead) {
		this(model,new ArrayList<N>(entities));
		this.lookAhead = lookAhead;
	}

	
	/**
	 * Creates a relation set for a given number of entities. Use this 
	 * constructor solely in case a relation set that is not related 
	 * to a specific model is needed. 
	 * 
	 * @param size, i.e., number of entities over which the relations are defined
	 */
	public RelSet(int size) {
		this.matrix = new RelSetType[size][size];
	}
	
	
	/**
	 * Checks whether two given entities are interleaving.
	 * 
	 * @param n1
	 * @param n2
	 * @return true, if both entities are interleaving 
	 */
	public boolean areInterleaving(N n1, N n2) {
		int index1 = this.entities.indexOf(n1);
		int index2 = this.entities.indexOf(n2);
		if (index1 == -1 || index2 == -1)
			throw new InvalidParameterException("The structure is not defined for the respective entities.");
		return matrix[index1][index2].equals(RelSetType.Interleaving);
	}

	/**
	 * Checks whether two given entities are exclusive.
	 * 
	 * @param n1
	 * @param n2
	 * @return true, if both entities are exclusive
	 */
	public boolean areExclusive(N n1, N n2) {
		int index1 = this.entities.indexOf(n1);
		int index2 = this.entities.indexOf(n2);
		if (index1 == -1 || index2 == -1)
			throw new InvalidParameterException("The structure is not defined for the respective entities.");
		return matrix[index1][index2].equals(RelSetType.Exclusive);
	}

	/**
	 * Checks whether two given entities are ordered.
	 * 
	 * @param n1
	 * @param n2
	 * @return true, if both entities are ordered
	 */
	public boolean areInOrder(N n1, N n2) {
		int index1 = this.entities.indexOf(n1);
		int index2 = this.entities.indexOf(n2);
		if (index1 == -1 || index2 == -1)
			throw new InvalidParameterException("The structure is not defined for the respective entities.");
		return matrix[index1][index2].equals(RelSetType.Order);
	}

	/**
	 * Returns the behavioural relation for two given entities.
	 * 
	 * @param n1
	 * @param n2
	 * @return the relation of the behavioural profile for the entities
	 */
	public RelSetType getRelationForEntities(N n1, N n2) {
		int index1 = this.entities.indexOf(n1);
		int index2 = this.entities.indexOf(n2);
		if (index1 == -1 || index2 == -1)
			throw new InvalidParameterException("The structure is not defined for the respective entities.");
		return matrix[index1][index2];
	}
	
	/**
	 * Returns the type of the behavioural relation for the two entities that are identified 
	 * by their index in the list of entities for which the relation set is defined.
	 * 
	 * @param index1
	 * @param index2
	 * @return the relation type of the relation set for the entities identified by the indices
	 */
	public RelSetType getRelationForIndex(int index1, int index2) {
		return matrix[index1][index2];
	}

	/**
	 * Returns all entities that are in a given behavioural relation with a given entity.
	 * 
	 * @param an entity
	 * @param a behavioural relation type
	 * @return all entities in the respective behavioural relation with the given entity
	 */
	public Collection<N> getEntitiesInRelation(N n, RelSetType relationType) {
		Collection<N> entities = new ArrayList<N>();
		int index = this.entities.indexOf(n);
		
		for (int i = 0; i < matrix.length; i++) {
			if (matrix[index][i].equals(relationType)) {
				entities.add(this.entities.get(i));
			}
		}
		return entities;
	}
	
	/**
	 * Dumps all entities in a given behavioural relation to the standard output.
	 * 
	 * @param a behavioural relation type
	 */
	public void printAllEntities(RelSetType relationType) {
		for(N n1 : this.entities) {
			int index1 = this.entities.indexOf(n1);
			for(N n2 : this.entities) {
				int index2 = this.entities.indexOf(n2);
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
	 * model or on the same set of entities.
	 * 
	 * @param profile that should be compared
	 * @return true, if the given relation set is equivalent to this relation set 
	 */
	public boolean equals(RelSet<M, N> relationSet) {
		if (!this.model.equals(relationSet.getModel()))
			return false;
		
		if (!this.getEntities().containsAll(relationSet.getEntities()) || !relationSet.getEntities().containsAll(this.getEntities()))
			return false;
		
		boolean equal = true;
		
		for(N n1 : this.entities) {
			for(N n2 : this.entities) {
				equal &= this.getRelationForEntities(n1, n2).equals(relationSet.getRelationForEntities(n1, n2));
			}
		}
		return equal;
	}
	
	/**
	 * Checks equality for two relation sets only for the
	 * shared entities. That is, we assess whether the structures define
	 * equal relations for all entities for which both structures are
	 * defined.
	 * 
	 * Returns false, if both matrices are not based on the same
	 * model.
	 * 
	 * @param profile that should be compared
	 * @return true, if the given relation set is equivalent to this relation set for shared entities
	 */
	public boolean equalsForSharedEntities(RelSet<M, N> relationSet) {
		if (!this.model.equals(relationSet.getModel()))
			return false;
		
		boolean equal = true;
		
		HashSet<N> sharedEntities = new HashSet<N>(this.getEntities());
		sharedEntities.retainAll(relationSet.getEntities()); 
		
		for(N n1 : sharedEntities) {
			for(N n2 : sharedEntities) {
				equal &= this.getRelationForEntities(n1, n2).equals(relationSet.getRelationForEntities(n1, n2));
//				if (!this.getRelationForEntities(n1, n2).equals(profile.getRelationForEntities(n1, n2))) {
//					System.out.println(n1);
//					System.out.println(n2);
//					System.out.println(this.getRelationForEntities(n1, n2));
//					System.out.println(profile.getRelationForEntities(n1, n2));
//					
//				}
			}
		}
		return equal;
	}
	
	/**
	 * Checks emptiness of a relation set. It is empty, if it defines 
	 * exclusiveness for all pairs of entities.
	 * 
	 * @return true, if the relation set is empty
	 */
	public boolean isEmpty() {
		for (N n1 : getEntities()) 
			for (N n2 : getEntities()) 
				if (!getRelationForEntities(n1, n2).equals(RelSetType.Exclusive))
						return false;
		
		return true;
	}

	/**
	 * Returns the complement of the relation set . It is defined as the relation set
	 * that comprises the complement relations for all pairs of entities.
	 * 
	 * @return 
	 */
	public RelSet<M, N> getComplement() {
		RelSet<M, N> cProfile = new RelSet<M, N>(getModel(),getEntities());
		RelSetType[][] cMatrix = cProfile.getMatrix();
		
		for (int i = 0; i < matrix.length; i++) 
			for (int j = 0; j < matrix.length; j++)
				cMatrix[i][j] = getComplementRelation(matrix[i][j]);

		return cProfile;
	}

}
