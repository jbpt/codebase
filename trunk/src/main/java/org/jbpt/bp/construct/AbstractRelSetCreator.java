package org.jbpt.bp.construct;

import org.jbpt.bp.RelSetType;


/**
 * Abstract super class for all computations of relation sets.
 * 
 * @author matthias.weidlich
 *
 */
public abstract class AbstractRelSetCreator {

	/**
	 * As the matrix of a relation set is symmetric for
	 * the exclusive and interleaving relation, we use this procedure 
	 * to set these dependency between two nodes.
	 * 
	 * @param i
	 * @param j
	 * @param behavioural relation
	 */
	protected void setMatrixEntry(RelSetType[][] matrix, int i, int j, RelSetType type) {
		assert(type.equals(RelSetType.Interleaving)||type.equals(RelSetType.Exclusive));
		matrix[i][j] = type;
		matrix[j][i] = type;
	}
	
	/**
	 * Sets the matrix entry for two indices and an order relation of the 
	 * relation set.
	 * 
	 * @param i
	 * @param j
	 * @param behavioural relation
	 */
	protected void setMatrixEntryOrder(RelSetType[][] matrix, int from, int to) {
		matrix[from][to] = RelSetType.Order;
		matrix[to][from] = RelSetType.ReverseOrder;
	}
	

}
