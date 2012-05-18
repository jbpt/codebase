package org.jbpt.petri;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Marking of a Petri net.
 * 
 * @author Christian Wiggert
 * @author Artem Polyvyanyy
 */
public class Marking extends HashMap<Place,Integer> {
	private static final long serialVersionUID = 1L;
	
	// associated net
	private PetriNet net = null;
	
	/**
	 * Construct a marking and associate it with a given net.
	 * 
	 * @param net A net to associate marking with.
	 * @throws IllegalArgumentException if a given net is set to <tt>null</tt>.
	 */
	public Marking(PetriNet net) {
		if (net==null) throw new IllegalArgumentException("PetriNet object expected!");
		this.net = net;
	}

	/**
	 * Put specified number of tokens at a given place of the associated net.
	 * 
	 * @param p Place of the associated net.
	 * @param tokens Number of tokens to put at the given place.
	 * @return Previous number of tokens at the given place. Returns <tt>0</tt> if place is set to <tt>null</tt>. 
	 * Attempts to remove all tokens from the given place if tokens is negative, equals to <tt>0</tt>, or is set to <tt>null</tt>.   
	 * @throws IllegalArgumentException if the given place is not part of the associated net.
	 */
	@Override
	public Integer put(Place p, Integer tokens) {
		if (p==null) return 0;
		if (!this.net.contains(p)) throw new IllegalArgumentException("Proposed place is not part of the associated net!");
		
		Integer result = null;
		if (tokens==null) result = super.remove(p);
		else {
			if (tokens<=0) result = super.remove(p); 
			else result = super.put(p,tokens);	
		}
		
		return result==null ? 0 : result;
	}
	
	/**
	 * Copies all of the marking from the specified map to this marking.
     * These operation will replace any info that this marking had for any of the places currently in the specified map.
     *
     * @param m Mapping to be stored in this marking.
     * @throws NullPointerException if the specified map is null.
	 */
	@Override
	public void putAll(Map<? extends Place, ? extends Integer> m) {
		for (Map.Entry<? extends Place, ? extends Integer> entry : m.entrySet()) {
			this.put(entry.getKey(), entry.getValue());
		}
	}
	
	/**
	 * Get associated net.
	 * @return Associated net.
	 */
	public PetriNet getPetriNet() {
		return this.net;
	}
	
	@Override
	public Integer get(Object p) {
		Integer i = super.get(p);
		if (i != null) return i;
		if (p instanceof Place) return 0;
		return null;
	}
	
	/**
	 * Get number of tokens at a place.
	 * 
	 * @param p Place
	 * @return Number of tokens at p
	 */
	public Integer get(Place p) {
		Integer i = super.get(p);
		if (i != null) return i;
		return 0;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof Marking)) return false;
		Marking that = (Marking) o;
		if (this.size()!=that.size()) return false;
		
		for (Map.Entry<Place, Integer> i : this.entrySet()) {
			Integer value = that.get(i.getKey());
			if (value == null) return false;
			if (!i.getValue().equals(value)) return false;
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		int result = 0;
		
		result -= this.net.hashCode();
		
		for (Place p : this.net.getPlaces())
			result += 17 * p.hashCode() * this.get(p);
		
		return result;
	}
	
	/**
	 * Check if place is marked, i.e., contains at least one token.
	 * @param p Place
	 * @return <code>true</code> if p is marked; <code>false</code> otherwise.
	 */
	public boolean isMarked(Place p) {
		return this.get(p) > 0;
	}
	
	@Override
	public Marking clone() {
		Marking clone = (Marking)super.clone();
		clone.net = this.net;
		return clone;
	}
	
	/**
	 * Get marking as a multi-set of places 
	 * @return Marking as a multi-set of places
	 */
	public Collection<Place> toMultiSet() {
		Collection<Place> result = new ArrayList<Place>();
		
		for (Map.Entry<Place, Integer> entry : this.entrySet()) {
			for (int i = 0; i < entry.getValue(); i++) {
				result.add(entry.getKey());
			}
		}
		
		return result;
	}
}
