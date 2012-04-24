package org.jbpt.petri2;

import java.util.HashMap;
import java.util.Map;

/**
 * Petri net marking implementation
 * 
 * Stores the current marking of a {@link PetriNet}.
 * 
 * @author Christian Wiggert, Artem Polyvyanyy
 */
public class Marking extends HashMap<Place, Integer> {
	private static final long serialVersionUID = 1L;
	private PetriNet net = null;
	
	protected Marking(PetriNet net) {
		this.net = net;
	}

	@Override
	// TODO check if place is in the net
	public Integer put(Place p, Integer tokens) {
		if (tokens <= 0)
			return this.remove(p);
		
		return super.put(p, tokens);
	}

	@Override
	public void putAll(Map<? extends Place, ? extends Integer> m) {
		for (Map.Entry<? extends Place, ? extends Integer> entry : m.entrySet()) {
			this.put(entry.getKey(), entry.getValue());
		}
	}
	
	@Override
	public Integer get(Object p) {
		Integer i = super.get(p);
		if (i != null) return i;
		if (p instanceof Place) return 0;
		return null;
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
		for (Place p : this.net.getPlaces()) {
			result += 17 * p.hashCode() * this.get(p);
		}
		return result;
	}
	
	/**
	 * Get Petri net
	 * @return Petri net
	 */
	public PetriNet getPetriNet() {
		return this.net;
	}
}
