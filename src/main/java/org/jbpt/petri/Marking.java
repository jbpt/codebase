package org.jbpt.petri;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Marking of a Petri net.
 * 
 * @author Christian Wiggert
 * @author Artem Polyvyanyy
 */
public class Marking extends HashMap<Place,Integer> implements IMarking<Place> {

	private static final long serialVersionUID = -2144274745926614966L;
	
	// associated net
	private PetriNet net = null;
	
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
	 * Construct a marking and associate it with a given net.
	 * 
	 * @param net A net to associate marking with.
	 * @throws IllegalArgumentException if a given net is set to <tt>null</tt>.
	 */
	public Marking(PetriNet net) {
		if (net==null) throw new IllegalArgumentException("PetriNet object expected but was NULL!");
		this.net = net;
	}
	
	public PetriNet getPetriNet() {
		return this.net;
	}

	@Override
	public boolean isMarked(Place place) {
		return this.get(place) > 0;
	}

	@Override
	public Collection<Place> toMultiSet() {
		Collection<Place> result = new ArrayList<Place>();
		
		for (Map.Entry<Place, Integer> entry : this.entrySet()) {
			for (int i = 0; i < entry.getValue(); i++) {
				result.add(entry.getKey());
			}
		}
		
		return result;
	}

	@Override
	public Integer remove(Place place) {
		return super.remove(place);
	}

	@Override
	public Integer get(Place place) {
		Integer i = super.get(place);
		return i == null ? 0 : i; 
	}
	
	@Override
	public void clear() {
		super.clear();
	}
	
	@Override
	public boolean isEmpty() {
		return super.isEmpty();
	}
	
	/**
	 * Removes all tokens from a given place of the associated net.
	 * 
	 * @param place Place of the associated net.
	 * @return The number of tokens previously contained in the given place, or <tt>null</tt> there was no token at the given place. 
	 */
	@Override
	public Integer remove(Object place) {
		return super.remove(place);
	}

	/**
	 * Copies all of the marking from the specified map to this marking.
	 * These operation will replace any info that this marking had for any of the places currently in the specified map.
	 *
	 * @param map Mapping to be stored in this marking.
	 * @throws NullPointerException if the specified map is null.
	 */
	@Override
	public void putAll(Map<? extends Place, ? extends Integer> m) {
		for (Map.Entry<? extends Place, ? extends Integer> entry : m.entrySet()) {
			this.put(entry.getKey(), entry.getValue());
		}
	}
	
	/**
	 * Get number of tokens at a place.
	 * 
	 * @param p Place of the associated net.
	 * @return Number of tokens at the place.
	 */
	@Override
	public Integer get(Object p) {
		if (!(p instanceof Place)) return 0; 
		Integer i = super.get(p);
		return i == null ? 0 : i;
	}
	
	/**
	 * Returns the number of marked places in the associated net.
	 * 
	 * @return The number of marked places in the associated net.
	 */
	@Override
	public int size() {
		return super.size();
	}
	
	/**
	 * Returns set of pairs where every pair specifies a marked place of the associated net and the number of tokens at the place.
	 * 
	 * @return The set of pairs where every pair specifies a marked place of the associated net and the number of tokens at the place.
	 */
	@Override
	public Set<Map.Entry<Place,Integer>> entrySet() {
		return super.entrySet();
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
	
	@Override
	public Marking clone() {
		Marking clone = (Marking)super.clone();
		clone.net = this.net;
		
		return clone;
	}
}
