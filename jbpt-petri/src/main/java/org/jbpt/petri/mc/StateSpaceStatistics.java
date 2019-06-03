package org.jbpt.petri.mc;

/** 
 * A data structure to store number of reachable states and state transitions.
 * 
 * @author Artem Polyvyanyy
 */
public class StateSpaceStatistics {
	private long nOfStates= 0L;
	private long nOfTransitions= 0L;
	
	protected StateSpaceStatistics(long states, long transitions) {
		this.nOfStates = states;
		this.nOfTransitions = transitions;
	}
	
	/**
	 * Get number of states.
	 * 
	 * @return Number of states.
	 */
	public long getNumberOfStates() {
		return nOfStates;
	}
	
	/**
	 * Get number of state transitions.
	 * 
	 * @return Number of state transitions.
	 */
	public long getNumberOfTransitions() {
		return nOfTransitions;
	}
}
