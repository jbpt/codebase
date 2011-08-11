package de.hpi.bpt.process.petri.bp;

import java.util.ArrayList;
import java.util.List;

import de.hpi.bpt.process.petri.Transition;
import de.hpi.bpt.process.petri.bp.BehaviouralProfile;
import de.hpi.bpt.process.petri.bp.BehaviouralProfile.CharacteristicRelationType;

/**
 * Captures the behavioural profile over labels of a Petri net. It is 
 * derived directly from a behavioural profile and considers all labels
 * of all transitions of the Petri net.
 * 
 *  Note that the behavioural profile used as the basis for computation
 *  is assumed to be defined over all transitions (or all nodes).
 * 
 * @author matthias.weidlich
 *
 */
public class BehaviouralProfileOverLabels {

	/**
	 * The base behavioural profile over transitions.
	 */
	protected BehaviouralProfile bp;
	
	/**
	 * The labels considered for the behavioural profile over labels.
	 */
	protected List<String> labels;
	
	/**
	 * The matrix to capture the profile over labels.
	 */
	protected CharacteristicRelationType[][] labelMatrix;

	/**
	 * Construct the behavioural profile over labels based on 
	 * a behavioural profile.
	 * 
	 * @param bp, the behavioural profile used as basis
	 */
	public BehaviouralProfileOverLabels(BehaviouralProfile bp) {
		this.bp = bp;
		this.labels = new ArrayList<String>();
		for (Transition t : this.bp.getNet().getTransitions())
			if (!this.labels.contains(t.getName()))
				this.labels.add(t.getName());
		
		deriveLabelMatrix();
	}

	/**
	 * Returns the profile relation for two given labels.
	 * 
	 * @param s1, a label
	 * @param s2, a label
	 * @return the profile relation for both labels
	 */
	public CharacteristicRelationType getRelationForLabels(String s1, String s2) {
		int index1 = this.labels.indexOf(s1);
		int index2 = this.labels.indexOf(s2);
		return this.labelMatrix[index1][index2];
	}
	
	/**
	 * Derive the profile relations over labels from the profile relations
	 * over transitions.
	 */
	protected void deriveLabelMatrix() {
		this.labelMatrix = new CharacteristicRelationType[this.labels.size()][this.labels.size()];
		
		for (Transition t1 : this.bp.getNet().getTransitions()) {
			String s1 = t1.getName();
			int index1 = this.labels.indexOf(s1);
			for (Transition t2 : this.bp.getNet().getTransitions()) {
				String s2 = t2.getName();
				int index2 = this.labels.indexOf(s2);
				
				CharacteristicRelationType rel = bp.getRelationForNodes(t1, t2);
				
				if (this.labelMatrix[index1][index2] == null) {
					this.labelMatrix[index1][index2] = rel;
				}
				else if (this.labelMatrix[index1][index2].equals(CharacteristicRelationType.Exclusive)) {
					this.labelMatrix[index1][index2] = rel;
				}
				else if (this.labelMatrix[index1][index2].equals(CharacteristicRelationType.StrictOrder)) {
					if (rel.equals(CharacteristicRelationType.ReverseStrictOrder))
						this.labelMatrix[index1][index2] = CharacteristicRelationType.InterleavingOrder;
					else if (rel.equals(CharacteristicRelationType.InterleavingOrder))
						this.labelMatrix[index1][index2] = CharacteristicRelationType.InterleavingOrder;
				}
				else if (this.labelMatrix[index1][index2].equals(CharacteristicRelationType.ReverseStrictOrder)) {
					if (rel.equals(CharacteristicRelationType.StrictOrder))
						this.labelMatrix[index1][index2] = CharacteristicRelationType.InterleavingOrder;
					else if (rel.equals(CharacteristicRelationType.InterleavingOrder))
						this.labelMatrix[index1][index2] = CharacteristicRelationType.InterleavingOrder;
				}
			}
		}
	}
	
	/**
	 * Get all considered labels.
	 * 
	 * @return the set of labels over which the profile is defined
	 */
	public List<String> getLabels() {
		return this.labels;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("------------------------------------------\n");
		sb.append("Behavioural Profile Over Labels Matrix\n");
		sb.append("------------------------------------------\n");
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
