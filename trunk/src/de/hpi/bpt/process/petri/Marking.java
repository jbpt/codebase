package de.hpi.bpt.process.petri;

import java.util.HashMap;

/**
 * Stores the current marking of a {@link PetriNet}.
 * 
 * @author Christian Wiggert
 *
 */
public class Marking extends HashMap<Place, Integer> {

	private static final long serialVersionUID = 1L;
	
	private PetriNet net;
	
	public Marking(PetriNet net) {
		this.net = net;
	}
	
	/**
	 * Applies the marking to the according {@link PetriNet}.
	 */
	public void apply() {
		net.setMarking(this);
	}

}
