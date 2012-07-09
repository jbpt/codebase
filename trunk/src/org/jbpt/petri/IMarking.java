package org.jbpt.petri;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public interface IMarking {
	/**
	 * Get associated net.
	 * 
	 * @return Associated net.
	 */
	public PetriNet getPetriNet();

	/**
	 * Put specified number of tokens at a given place of the associated net.
	 * 
	 * @param place Place of the associated net.
	 * @param tokens Number of tokens to put at the given place.
	 * @return Previous number of tokens at the given place. Returns <tt>0</tt> if place is set to <tt>null</tt>. 
	 * Attempts to remove all tokens from the given place if tokens is negative, equals to <tt>0</tt>, or is set to <tt>null</tt>.   
	 * @throws IllegalArgumentException if the given place is not part of the associated net.
	 */
	public Integer put(Place place, Integer tokens);

	/**
	 * Copies all of the marking from the specified map to this marking.
	 * These operation will replace any info that this marking had for any of the places currently in the specified map.
	 *
	 * @param m Mapping to be stored in this marking.
	 * @throws NullPointerException if the specified map is null.
	 */
	public void putAll(Map<? extends Place, ? extends Integer> map);

	/**
	 * Removes all tokens from a given place of the associated net.
	 * 
	 * @param p Place of the associated net.
	 * @return The number of tokens previously contained in the given place, or <tt>null</tt> there was no token at the given place. 
	 */
	public Integer remove(Object p);

	/**
	 * Removes all tokens from a given place of the associated net.
	 * 
	 * @param p Place of the associated net.
	 * @return The number of tokens previously contained in the given place, or <tt>null</tt> there was no token at the given place. 
	 */
	public Integer remove(Place p);

	/**
	 * Get number of tokens at a place.
	 * 
	 * @param p Place of the associated net.
	 * @return Number of tokens at the place.
	 */
	public Integer get(Object p);

	/**
	 * Get number of tokens at a place.
	 * 
	 * @param p Place of the associated net.
	 * @return Number of tokens at the place.
	 */
	public Integer get(Place p);

	/**
	 * Check if place is marked, i.e., contains at least one token.
	 * 
	 * @param p Place of the associated net.
	 * @return <tt>true</tt> if p is marked; <tt>false</tt> otherwise.
	 */
	public boolean isMarked(Place p);

	/**
	 * Get marking as a multi-set of places. 
	 * The multi-set contains each place as many times as there are tokens at the place.
	 * 
	 * @return Marking as a multi-set of places.
	 */
	public Collection<Place> toMultiSet();

	/**
	 * Clear this marking.
	 * 
	 * After a call to this procedure, this marking describes a situation when no place of the associated net contains a token.
	 */
	public void clear();

	/**
	 * Returns the number of marked places in the associated net.
	 * 
	 * @return The number of marked places in the associated net.
	 */
	public int size();

	/**
	 * Returns <tt>true</tt> if this marking does not mark any place.
	 * 
	 * @return <tt>true</tt> if this marking does not mark any place; <tt>false</tt> otherwise.
	 */
	public boolean isEmpty();

	/**
	 * Returns set of pairs where every pair specifies a marked place of the associated net and the number of tokens at the place.
	 * 
	 * @return The set of pairs where every pair specifies a marked place of the associated net and the number of tokens at the place.
	 */
	public Set<Entry<Place, Integer>> entrySet();

	public boolean equals(Object o);

	public int hashCode();

	public Marking clone();

	/**
	 * Returns string representation of this marking.
	 * 
	 * @return The string representation of this marking.
	 */
	public String toString();

}