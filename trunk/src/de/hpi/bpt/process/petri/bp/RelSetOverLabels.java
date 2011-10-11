package de.hpi.bpt.process.petri.bp;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import de.hpi.bpt.process.petri.Transition;

/**
 * Captures a relation set over labels of a Petri net. It is 
 * derived directly from a relation set and considers all labels
 * of all transitions of the Petri net.
 * 
 *  Note that the relation set used as the basis for computation
 *  is assumed to be defined over all transitions (or all nodes).
 * 
 * @author matthias.weidlich
 *
 */
public class RelSetOverLabels {

	/**
	 * The base relation set over transitions.
	 */
	protected RelSet rs;
	
	/**
	 * The labels considered for the relation set over labels.
	 */
	protected List<String> labels;
	
	/**
	 * The matrix to capture the relation set over labels.
	 */
	protected RelSetType[][] labelMatrix;

	/**
	 * Construct the relation set over labels based on 
	 * a relation set.
	 * 
	 * @param rs, the relation set used as basis
	 */
	public RelSetOverLabels(RelSet rs) {
		this.rs = rs;
		this.labels = new ArrayList<String>();
		for (Transition t : this.rs.getNet().getTransitions())
			if (!this.labels.contains(t.getName()))
				this.labels.add(t.getName());
		
		deriveLabelMatrix();
	}

	/**
	 * Returns the behavioural relation for two given labels.
	 * 
	 * @param s1, a label
	 * @param s2, a label
	 * @return the type fo the behavioural relation for both labels
	 */
	public RelSetType getRelationForLabels(String s1, String s2) {
		int index1 = this.labels.indexOf(s1);
		int index2 = this.labels.indexOf(s2);
		if (index1 == -1 || index2 == -1)
			throw new InvalidParameterException("The structure is not defined for the respective nodes.");
		return this.labelMatrix[index1][index2];
	}
	
	/**
	 * Derive the relation set over labels from the relations
	 * over transitions.
	 */
	protected void deriveLabelMatrix() {
		this.labelMatrix = new RelSetType[this.labels.size()][this.labels.size()];
		
		for (Transition t1 : this.rs.getNet().getTransitions()) {
			String s1 = t1.getName();
			int index1 = this.labels.indexOf(s1);
			for (Transition t2 : this.rs.getNet().getTransitions()) {
				String s2 = t2.getName();
				int index2 = this.labels.indexOf(s2);
				
				RelSetType rel = rs.getRelationForNodes(t1, t2);
				
				if (this.labelMatrix[index1][index2] == null) {
					this.labelMatrix[index1][index2] = rel;
				}
				else if (this.labelMatrix[index1][index2].equals(RelSetType.Exclusive)) {
					this.labelMatrix[index1][index2] = rel;
				}
				else if (this.labelMatrix[index1][index2].equals(RelSetType.Order)) {
					if (rel.equals(RelSetType.ReverseOrder))
						this.labelMatrix[index1][index2] = RelSetType.Interleaving;
					else if (rel.equals(RelSetType.Interleaving))
						this.labelMatrix[index1][index2] = RelSetType.Interleaving;
				}
				else if (this.labelMatrix[index1][index2].equals(RelSetType.ReverseOrder)) {
					if (rel.equals(RelSetType.Order))
						this.labelMatrix[index1][index2] = RelSetType.Interleaving;
					else if (rel.equals(RelSetType.Interleaving))
						this.labelMatrix[index1][index2] = RelSetType.Interleaving;
				}
			}
		}
	}
	
	/**
	 * Get all considered labels.
	 * 
	 * @return the set of labels over which the relalation set is defined
	 */
	public List<String> getLabels() {
		return this.labels;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("------------------------------------------------------\n");
		sb.append("Relation Set Matrix Over Labels (Lookahead: "+this.rs.lookAhead+") \n");
		sb.append("------------------------------------------------------\n");
		for (int k = 0; k < labelMatrix.length; k++) {
			for (int row = 0; row < labelMatrix.length; row++) {
				sb.append(labelMatrix[row][k] + " , ");
			}
			sb.append("\n");
		}
		sb.append("------------------------------------------\n");
		return sb.toString();
	}

	
}
