package de.hpi.bpt.process.petri.unf;

import java.util.Comparator;
import java.util.TreeSet;

/**
 * Coset - set of mutually concurrent conditions
 * 
 * @author Artem Polyvyanyy
 */
public class Coset extends TreeSet<Condition> {
	private static final long serialVersionUID = 1L;
	private final static CosetComparator coset_comparator = new CosetComparator();
	
	@Override
	public Comparator<? super Condition> comparator() {
		return Coset.coset_comparator;
	}
	
	@Override
	public int hashCode() {
		int code = 0;
		for (Condition c : this) {
			code += c.hashCode();
		}
		
		return code;
	}
}
