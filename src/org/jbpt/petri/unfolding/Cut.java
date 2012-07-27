package org.jbpt.petri.unfolding;

import java.util.Collection;

import org.jbpt.petri.PetriNet;


/**
 * Cut - a maximal set of mutually concurrent conditions.
 * 
 * @author Artem Polyvyanyy
 */
public class Cut extends Coset {
	private static final long serialVersionUID = 1L;
	
	public Cut(PetriNet net) {
		super(net);
	}
	
	public Cut(PetriNet net, Collection<Condition> cut) {
		super(net);
		this.addAll(cut);
	}
}
