package org.jbpt.petri;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Marking of a Petri net.
 * 
 * @author Christian Wiggert
 * @author Artem Polyvyanyy
 */
public class Marking extends HashMap<Place,Integer> implements IMarking {
	private static final long serialVersionUID = -2144274745926614966L;
	
	// associated net
	private PetriNet net = null;
	
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
	
	/* (non-Javadoc)
	 * @see org.jbpt.petri.IMarking#getPetriNet()
	 */
	@Override
	public PetriNet getPetriNet() {
		return this.net;
	}

	/* (non-Javadoc)
	 * @see org.jbpt.petri.IMarking#put(org.jbpt.petri.Place, java.lang.Integer)
	 */
	@Override
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
	
	/* (non-Javadoc)
	 * @see org.jbpt.petri.IMarking#putAll(java.util.Map)
	 */
	@Override
	@Override
	public void putAll(Map<? extends Place, ? extends Integer> m) {
		for (Map.Entry<? extends Place, ? extends Integer> entry : m.entrySet()) {
			this.put(entry.getKey(), entry.getValue());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.jbpt.petri.IMarking#remove(java.lang.Object)
	 */
	@Override
	@Override
	public Integer remove(Object p) {
		return super.remove(p);
	}
	
	/* (non-Javadoc)
	 * @see org.jbpt.petri.IMarking#remove(org.jbpt.petri.Place)
	 */
	@Override
	public Integer remove(Place p) {
		return super.remove(p);
	}
	
	/* (non-Javadoc)
	 * @see org.jbpt.petri.IMarking#get(java.lang.Object)
	 */
	@Override
	@Override
	public Integer get(Object p) {
		if (!(p instanceof Place)) return 0; 
		Integer i = super.get(p);
		return i == null ? 0 : i;
	}
	
	/* (non-Javadoc)
	 * @see org.jbpt.petri.IMarking#get(org.jbpt.petri.Place)
	 */
	@Override
	public Integer get(Place p) {
		Integer i = super.get(p);
		return i == null ? 0 : i; 
	}
	
	/* (non-Javadoc)
	 * @see org.jbpt.petri.IMarking#isMarked(org.jbpt.petri.Place)
	 */
	@Override
	public boolean isMarked(Place p) {
		return this.get(p) > 0;
	}
	
	/* (non-Javadoc)
	 * @see org.jbpt.petri.IMarking#toMultiSet()
	 */
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
	
	/* (non-Javadoc)
	 * @see org.jbpt.petri.IMarking#clear()
	 */
	@Override
	@Override
	public void clear() {
		super.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.jbpt.petri.IMarking#size()
	 */
	@Override
	@Override
	public int size() {
		return super.size();
	}

	/* (non-Javadoc)
	 * @see org.jbpt.petri.IMarking#isEmpty()
	 */
	@Override
	@Override
	public boolean isEmpty() {
		return super.isEmpty();
	}

	/* (non-Javadoc)
	 * @see org.jbpt.petri.IMarking#entrySet()
	 */
	@Override
	@Override
	public Set<Entry<Place, Integer>> entrySet() {
		return super.entrySet();
	}
	
	/* (non-Javadoc)
	 * @see org.jbpt.petri.IMarking#equals(java.lang.Object)
	 */
	@Override
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
	
	/* (non-Javadoc)
	 * @see org.jbpt.petri.IMarking#hashCode()
	 */
	@Override
	@Override
	public int hashCode() {
		int result = 0;
		
		result -= this.net.hashCode();
		
		for (Place p : this.net.getPlaces())
			result += 17 * p.hashCode() * this.get(p);
		
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.jbpt.petri.IMarking#clone()
	 */
	@Override
	@Override
	public Marking clone() {
		Marking clone = (Marking)super.clone();
		clone.net = this.net;
		
		return clone;
	}
	
	/* (non-Javadoc)
	 * @see org.jbpt.petri.IMarking#toString()
	 */
	@Override
	@Override
	public String toString() {
		return super.toString();
	}
}
