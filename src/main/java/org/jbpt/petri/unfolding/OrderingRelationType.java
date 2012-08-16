package org.jbpt.petri.unfolding;

/**
 * Enumeration of ordering relation types.
 * 
 * @author Artem Polyvyanyy
 */
public enum OrderingRelationType {
	CAUSAL, 
	INVERSE_CAUSAL, 
	CONFLICT, 
	CONCURRENT, 
	UNDEFINED;

	@Override
	public String toString() {
		if (this == CAUSAL) return ">";
		if (this == INVERSE_CAUSAL) return "<";
		if (this == CONFLICT) return "#";
		if (this == CONCURRENT) return "@";
		
		return "-";
	}
}
