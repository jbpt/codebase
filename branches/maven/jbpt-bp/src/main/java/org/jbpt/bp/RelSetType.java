package org.jbpt.bp;

/**
 * The relations of one of the relation sets. 
 * All relations are mutually exclusive.
 */
public enum RelSetType {
	
	Order,ReverseOrder,Interleaving,Exclusive,None;

	@Override
	public String toString() {
		if (this == Order) return "->";
		if (this == ReverseOrder) return "<-";
		if (this == Exclusive) return " +";
		if (this == Interleaving) return "||";
		
		return "";
	}

}
