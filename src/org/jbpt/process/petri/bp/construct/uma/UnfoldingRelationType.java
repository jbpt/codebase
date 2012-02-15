package de.hpi.bpt.process.petri.bp.construct.uma;

public enum UnfoldingRelationType {
	CONFLICT, CAUSAL, INVERSE_CAUSAL, CONCURRENCY, NONE;

	@Override
	public String toString() {
		if (this == CAUSAL) return ">";
		if (this == INVERSE_CAUSAL) return "<";
		if (this == CONFLICT) return "#";
		if (this == CONCURRENCY) return "@";
		
		return "-";
	}
}
