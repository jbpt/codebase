package de.hpi.bpt.process.petri;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores the current marking of a {@link PetriNet}.
 * 
 * @author Christian Wiggert, Artem Polyvyanyy
 */
public class Marking extends HashMap<Place, Integer> {

	private static final long serialVersionUID = 1L;
	
	private PetriNet net = null;
	
	public Marking(PetriNet net) {
		this.net = net;
	}
	
	/**
	 * Applies the marking to the according {@link PetriNet}.
	 */
	public void apply() {
		this.net.setMarking(this);
	}

	@Override
	public Integer put(Place key, Integer value) {
		if (value <= 0) {
			return this.remove(key);
		}
		
		return super.put(key, value);
	}

	@Override
	public void putAll(Map<? extends Place, ? extends Integer> m) {
		for (Map.Entry<? extends Place, ? extends Integer> entry : m.entrySet()) {
			this.put(entry.getKey(), entry.getValue());
		}
	}
	
	@Override
	public Integer get(Object key) {
		Integer i = super.get(key);
		if (i != null) return i;
		if (key instanceof Place) return 0;
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
}
