package de.hpi.bpt.process.petri.unf;

import java.util.HashSet;

import de.hpi.bpt.process.petri.Marking;

/**
 * Local configuration of unfolding event - causality closed set of events  
 * 
 * @author Artem Polyvyanyy
 */
public class LocalConfiguration extends HashSet<Event> {
	private static final long serialVersionUID = 1L;
	
	private Unfolding unf = null;	// reference to unfolding
	private Event e = null;			// event
	private Cut C = null;			// cut
	private Marking M = null;		// marking of cut
	
	/**
	 * Constructor
	 * 
	 * @param unf reference to unfolding
	 * @param e event
	 */
	public LocalConfiguration(Unfolding unf, Event e) {
		this.unf = unf;
		this.e = e;
		
		this.add(this.e);
		
		for (Condition c : this.e.getPreConditions()) {
			for (BPNode n : this.unf.ica.get(c)) {
				if (n instanceof Event)
					this.add((Event) n);
			}
		}
	}
	
	/**
	 * Get cut
	 * @return cut
	 */
	public Cut getCut() {
		if (this.C == null) {
		this.C = new Cut();
			this.C.addAll(this.unf.initialBP);
			for (Event e : this) this.C.addAll(e.getPostConditions());
			for (Event e : this) this.C.removeAll(e.getPreConditions());
		}
		
		return this.C;
	}
	
	/**
	 * Get marking
	 * @return marking
	 */
	public Marking getMarking() {
		if (this.M == null) {
			this.M = new Marking(this.unf.getNet());

			for (Condition c : this.getCut()) {
				if (c.getPlace() == null) this.M.put(c.getPlace(), 1);
				this.M.put(c.getPlace(), this.M.get(c.getPlace())+1);
			}
		}
		
		return this.M;
	}
}
