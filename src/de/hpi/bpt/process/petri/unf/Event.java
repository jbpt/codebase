package de.hpi.bpt.process.petri.unf;

import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.Transition;

/**
 * Unfolding event
 * 
 * @author Artem Polyvyanyy
 */
public class Event extends BPNode {
	
	static private int count = 0; 
	private int ID = 0;

	// required to capture unfolding
	private Transition t = null;	// transition that corresponds to event
	private Coset pre = null;		// preconditions of event - *e
	
	// for convenience reasons
	private Unfolding unf = null;					// reference to unfolding
	private Coset post = null;						// postconditions of event - e*
	private LocalConfiguration localConf = null;	// local configuration - [e]
	
	/**
	 * Constructor - expects required fields only
	 * 
	 * @param unf reference to unfolding
	 * @param t transition which occurrence is represented by this event 
	 * @param pre preset of conditions which caused event to occur
	 */
	public Event(Unfolding unf, Transition t, Coset pre) {
		this.ID = ++Event.count;
		
		this.unf = unf;
		this.t = t;
		this.pre = pre;
	}
	
	/**
	 * Get local configuration
	 * 
	 * @return local configuration
	 */
	public LocalConfiguration getLocalConfiguration() {
		if (this.localConf == null) 
			this.localConf = new LocalConfiguration(this.unf, this);
		
		return this.localConf;
	}
	
	/**
	 * Set post conditions of event
	 * @param post post conditions
	 */
	public void setPostConditions(Coset post) {
		this.post = post;
	}
	
	/**
	 * Get post conditions of event
	 * @return post conditions
	 */
	public Coset getPostConditions() {
		return this.post;
	}
	
	/**
	 * Get transition that corresponds to event
	 * @return corresponding transition
	 */
	public Transition getTransition() {
		return this.t;
	}
	
	/**
	 * Get pre conditions of event
	 * @return pre conditions
	 */
	public Coset getPreConditions() {
		return this.pre;
	}
	
	@Override
	public String toString() {
		return "["+this.getTransition().getName()+","+this.getPreConditions()+"]";
	}
	
	@Override
	public String getName() {
		return this.t.getName();
		//return this.t.getName()+"-"+this.ID;
	}
	
	@Override
	public boolean equals(Object that) {
		if (that == null || !(that instanceof Event)) return false;
		if (this == that) return true;
		
		Event thatE = (Event) that;
		if (this.getTransition().equals(thatE.getTransition())
				&& this.getPreConditions().equals(thatE.getPreConditions()))
			return true;
		
		return false;
	}
	
	@Override
	public int hashCode() {
		int hashCode = 7 * this.getTransition().hashCode();
		for (Condition c : this.getPreConditions())
			hashCode += 11 * c.getPlace().hashCode();
		
		return hashCode;
	}

	@Override
	public Node getNode() {
		return this.getTransition();
	}
}
