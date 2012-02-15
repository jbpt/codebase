package org.jbpt.petri.unf;

/**
 * Enumeration of ordering relation classes
 * 
 * @author Artem Polyvyanyy
 */
public enum OrderingRelation {
	CONFLICT, CAUSAL, INVERSE_CAUSAL, CONCURRENT, NONE;

	@Override
	public String toString() {
		if (this == CAUSAL) return ">";
		if (this == INVERSE_CAUSAL) return "<";
		if (this == CONFLICT) return "#";
		if (this == CONCURRENT) return "@";
		
		return "-";
	}
}
