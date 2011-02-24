package de.hpi.bpt.process.petri.bp.construct;

import de.hpi.bpt.process.petri.bp.BehaviouralProfile.CharacteristicRelationType;

/**
 * Abstract super class for all computations of behavioural profiles.
 * 
 * @author matthias.weidlich
 *
 */
public abstract class AbstractBPCreator {

	/**
	 * As the matrix of the behavioral profile is symmetric for
	 * the exclusive and concurrency relation, we use this procedure 
	 * to set these dependency between two nodes.
	 * 
	 * @param i
	 * @param j
	 * @param behavioural relation
	 */
	protected void setMatrixEntry(CharacteristicRelationType[][] matrix, int i, int j, CharacteristicRelationType type) {
		assert(type.equals(CharacteristicRelationType.InterleavingOrder)||type.equals(CharacteristicRelationType.Exclusive));
		matrix[i][j] = type;
		matrix[j][i] = type;
	}
	
	/**
	 * Sets the matrix entry for two indices and an order relation of the 
	 * behavioural profile.
	 * 
	 * @param i
	 * @param j
	 * @param behavioural relation
	 */
	protected void setMatrixEntryOrder(CharacteristicRelationType[][] matrix, int from, int to) {
		matrix[from][to] = CharacteristicRelationType.StrictOrder;
		matrix[to][from] = CharacteristicRelationType.ReverseStrictOrder;
	}
	

}
