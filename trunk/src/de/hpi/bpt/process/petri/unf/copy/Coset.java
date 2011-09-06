package de.hpi.bpt.process.petri.unf.copy;

import java.util.HashSet;

/**
 * Coset - set of mutually concurrent conditions
 * 
 * @author Artem Polyvyanyy
 */
public class Coset extends HashSet<Condition> {
	private static final long serialVersionUID = 1L;
	
	@Override
	public int hashCode() {
		int code = 0;
		for (Condition c : this) {
			code += c.hashCode();
		}
		
		return code;
	}

}
