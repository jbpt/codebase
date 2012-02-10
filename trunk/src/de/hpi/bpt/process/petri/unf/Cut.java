package de.hpi.bpt.process.petri.unf;

import java.util.Collection;

import de.hpi.bpt.process.petri.PetriNet;

/**
 * Cut - maximal set of mutually concurrent conditions
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
