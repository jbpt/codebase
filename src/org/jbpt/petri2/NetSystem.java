package org.jbpt.petri2;

/**
 * TODO
 * Net system implementation
 * 
 * @author Artem Polyvyanyy
 */
public class NetSystem {
	// Petri net
	private PetriNet net = null;
	// Marking
	private Marking M = null;
	
	public NetSystem() {
		this.net = new PetriNet();
		this.M = new Marking(this.net);
	}
	
	public NetSystem(PetriNet net) {
		this.net = net;
		this.M = new Marking(this.net);
	}
	
	/**
	 * Get Petri net
	 * @return Petri net of the net system
	 */
	public PetriNet getPetriNet() {
		return this.net;
	}

	/**
	 * Get marking
	 * @return Marking of the net system
	 */
	public Marking getMarking() {
		return this.M;
	}

}
