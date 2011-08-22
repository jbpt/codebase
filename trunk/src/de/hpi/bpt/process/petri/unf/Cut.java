package de.hpi.bpt.process.petri.unf;

import java.util.Collection;

/**
 * Cut - maximal set of mutually concurrent conditions
 * 
 * @author Artem Polyvyanyy
 */
public class Cut extends Coset {
	private static final long serialVersionUID = 1L;
	
	public Cut() {}
	
	public Cut(Collection<Condition> cut) {
		this.addAll(cut);
	}
}
