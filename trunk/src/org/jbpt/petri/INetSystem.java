package org.jbpt.petri;

import java.util.Set;

public interface INetSystem<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<P>> extends IPetriNet<F,N,P,T> {

	/**
	 * Get marking of this net system. 
	 *  
	 * @return Marking of this net system.
	 */
	public M getMarking();

	/**
	 * Get marked places of this net system.
	 * 
	 * @return Set of all marked places of this net system.
	 */
	public Set<P> getMarkedPlaces();

	/**
	 * Get enabled transitions of this net system.
	 * 
	 * @return Enabled transitions of this net system.
	 */
	public Set<T> getEnabledTransitions();

	/**
	 * Check if a given transition is enabled.
	 * 
	 * @param t Transition.
	 * @return <tt>true</tt> if transition is enabled; otherwise <tt>false</tt>.
	 */
	public boolean isEnabled(T t);

	/**
	 * Fire a transition in this net system. 
	 * Transition fires only if it is enabled. 
	 * Firing of a transition removes one token from every input place and adds one token to every output place of the transition. 
	 * 
	 * @param transition Transition to fire.
	 * @return <tt>true</tt> if firing took place; otherwise <tt>false</tt>.
	 */
	public boolean fire(T transition);

	/**
	 * Put tokens at a given place.
	 * 
	 * @param place Place. 
	 * @param tokens Number of tokens to put.
	 * @return the previous number of tokens at the given place, or <tt>null</tt> if parameters are wrong (either are equal to <tt>null</tt> or the given place does not belong to the net system)
	 */
	public Integer putTokens(P place, Integer tokens);

	/**
	 * Get number of tokens at a place.
	 * 
	 * @param place Place.
	 * @return Number of tokens at p.
	 */
	public Integer getTokens(P place);

	/**
	 * Changes marking of the net system to its natural initial marking, i.e., 
	 * the marking which put one token at each source place of the net system and no tokens elsewhere.
	 */
	public void loadNaturalMarking();

	/**
	 * Changes marking of this net system to the given one. 
	 * Note that the new marking must be associated with this net system.
	 * 
	 * @param newMarking Marking to use for this net system.
	 */
	public void loadMarking(M newMarking);
	
	/**
	 * Check if a given place is marked, i.e., contains at least one token.
	 * @param place Place.
	 * @return <tt>true</tt> if place is marked; otherwise <tt>false</tt>.
	 */
	public boolean isMarked(P place);

}