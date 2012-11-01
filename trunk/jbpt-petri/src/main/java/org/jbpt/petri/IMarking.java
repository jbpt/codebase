package org.jbpt.petri;

import java.util.Collection;
import java.util.Map;

/**
 * Interface to a Petri net marking.
 *
 * @author Artem Polyvyanyy
 */
public interface IMarking<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition>
		extends Map<P,Integer> {
	/**
	 * Put specified number of tokens at a given place of the associated net.
	 * 
	 * @param place Place of the associated net.
	 * @param tokens Number of tokens to put at the given place.
	 * @return Previous number of tokens at the given place. Returns <tt>0</tt> if place is set to <tt>null</tt>. 
	 * Attempts to remove all tokens from the given place if tokens is negative, equals to <tt>0</tt>, or is set to <tt>null</tt>.   
	 * @throws IllegalArgumentException if the given place is not part of the associated net.
	 */
	public Integer put(P place, Integer tokens) throws IllegalArgumentException;
	
	/**
	 * Check if place is marked, i.e., contains at least one token.
	 * 
	 * @param place Place of the associated net.
	 * @return <tt>true</tt> if p is marked; <tt>false</tt> otherwise.
	 */
	public boolean isMarked(P place);

	/**
	 * Get marking as a multi-set of places. 
	 * The multi-set contains each place as many times as there are tokens at the place.
	 * 
	 * @return Marking as a multi-set of places.
	 */
	public Collection<P> toMultiSet();
	
	/**
	 * Construct this marking from the given multi-set of places.
	 * The multi-set contains each place as many times as there are tokens at the place.
	 * 
	 * @param places Multi-set of places.
	 */
	public void fromMultiSet(Collection<P> places);

	/**
	 * Clear this marking.
	 * 
	 * After a call to this procedure, this marking describes a situation when no place of the associated net contains a token.
	 */
	public void clear();
	
	/**
	 * Removes all tokens from a given place of the associated net.
	 * 
	 * @param place Place of the associated net.
	 * @return The number of tokens previously contained in the given place, or <tt>null</tt> there was no token at the given place. 
	 */
	public Integer remove(P place);
	
	/**
	 * Get number of tokens at a place.
	 * 
	 * @param place Place of the associated net.
	 * @return Number of tokens at the place.
	 */
	public Integer get(P place);
	
	/**
	 * Returns <tt>true</tt> if this marking does not mark any place.
	 * 
	 * @return <tt>true</tt> if this marking does not mark any place; <tt>false</tt> otherwise.
	 */
	public boolean isEmpty();
	
	/**
	 * Get Petri net which this marking is associated with.
	 * 
	 * @return Petri net which this marking is associated with.
	 */
	public IPetriNet<F,N,P,T> getPetriNet();
	
	/**
	 * Create fresh marking and associate it with a given net.
	 * 
	 * @param net Petri net.
	 * @return Fresh marking associated wuth the given 'net'.
	 */
	public IMarking<F,N,P,T> createMarking(IPetriNet<F,N,P,T> net);
	
	/**
	 * Set net to associate this marking with.
	 * 
	 * @param net Petri net to associate this marking with.
	 */
	public void setPetriNet(IPetriNet<F,N,P,T> net);
	
	public Object clone();
}