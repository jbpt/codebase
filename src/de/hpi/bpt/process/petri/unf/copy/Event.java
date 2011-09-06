package de.hpi.bpt.process.petri.unf.copy;

import java.util.Set;

import de.hpi.bpt.process.petri.Transition;

/**
 * Unfolding event
 * 
 * @author Artem Polyvyanyy
 */
public class Event extends BPNode {
	
	// required to capture unfolding
	private Transition t = null;		// transition that corresponds to event
	private Set<Condition> pre = null;	// preconditions of event - *e
	
	// for convenience reasons
	private Unfolding unf = null;					// reference to unfolding
	private Set<Condition> post = null;				// postconditions of event - e*
	private LocalConfiguration localConf = null;	// local configuration - [e]
	
	/**
	 * Constructor - expects required fields only
	 * 
	 * @param unf reference to unfolding
	 * @param t transition which occurrence is represented by this event 
	 * @param pre preset of conditions which caused event to occur
	 */
	public Event(Unfolding unf, Transition t, Set<Condition> pre) {
		this.unf = unf;
		this.pre = pre;
		this.t = t;
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
	protected void setPostConditions(Set<Condition> post) {
		this.post = post;
	}
	
	/**
	 * Get post conditions of event
	 * @return post conditions
	 */
	public Set<Condition> getPostConditions() {
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
	public Set<Condition> getPreConditions() {
		return this.pre;
	}
	
	@Override
	public String toString() {
		return "["+this.getTransition().getName()+","+this.getPreConditions()+"]";
	}
	
	@Override
	public String getName() {
		return this.t.getName();
	}
}
