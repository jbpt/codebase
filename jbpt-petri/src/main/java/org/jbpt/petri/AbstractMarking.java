package org.jbpt.petri;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of a Petri net marking.
 * 
 * @author Christian Wiggert
 * @author Artem Polyvyanyy
 */
public abstract class AbstractMarking<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition> 
	extends HashMap<P,Integer> 
	implements IMarking<F,N,P,T> {

	private static final long serialVersionUID = -2144274745926614966L;
	
	// associated net
	private IPetriNet<F,N,P,T> net = null;
	
	public AbstractMarking() {}
	
	/**
	 * Construct a marking and associate it with a given net.
	 * 
	 * @param net A net to associate marking with.
	 * @throws IllegalArgumentException if a given net is set to <tt>null</tt>.
	 */
	public AbstractMarking(IPetriNet<F,N,P,T> net) {
		if (net==null) throw new IllegalArgumentException("PetriNet object expected but was NULL!");
		this.net = net;
	}
	
	@Override
	public Integer put(P p, Integer tokens) {
		if (p==null) return 0;
		if (!this.net.getPlaces().contains(p)) throw new IllegalArgumentException("Proposed place is not part of the associated net!");
		
		Integer result = null;
		if (tokens==null) result = super.remove(p);
		else {
			if (tokens<=0) result = super.remove(p); 
			else result = super.put(p,tokens);	
		}
		
		return result==null ? 0 : result;
	}
	
	@Override
	public IPetriNet<F,N,P,T> getPetriNet() {
		return this.net;
	}

	@Override
	public boolean isMarked(P place) {
		return this.get(place) > 0;
	}

	@Override
	public Collection<P> toMultiSet() {
		Collection<P> result = new ArrayList<P>();
		
		for (Map.Entry<P,Integer> entry : this.entrySet()) {
			for (int i = 0; i < entry.getValue(); i++) {
				result.add(entry.getKey());
			}
		}
		
		return result;
	}
	
	@Override
	public void fromMultiSet(Collection<P> places) {
		this.clear();
		
		for (P p : places) {
			if (!this.net.getPlaces().contains(p)) continue;
			
			Integer tokens = this.get(p);
			if (tokens==null)
				this.put(p,1);
			else
				this.put(p,tokens+1);
		}
	}

	@Override
	public Integer remove(P place) {
		return super.remove(place);
	}

	@Override
	public Integer get(P place) {
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
	public void putAll(Map<? extends P, ? extends Integer> m) {
		for (Map.Entry<? extends P, ? extends Integer> entry : m.entrySet()) {
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
	public Set<Map.Entry<P,Integer>> entrySet() {
		return super.entrySet();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof IMarking)) return false;
		@SuppressWarnings("unchecked")
		IMarking<F,N,P,T> that = (IMarking<F,N,P,T>) o;
		if (this.size()!=that.size()) return false;
		
		for (Map.Entry<P,Integer> i : this.entrySet()) {
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
		
		for (P p : this.net.getPlaces())
			result += 17 * p.hashCode() * this.get(p);
		
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IMarking<F,N,P,T> createMarking(IPetriNet<F,N,P,T> net) {
		IMarking<F,N,P,T> m = null;
		try {
			m = (IMarking<F,N,P,T>) Marking.class.newInstance();
			m.setPetriNet(net);
			return m;
		} catch (InstantiationException | IllegalAccessException exception) {
			return m;
		}
	}

	@Override
	public void setPetriNet(IPetriNet<F,N,P,T> net) {
		this.clear();
		this.net = net;
	}
	
	@Override
	public boolean fire(T transition) {
		if (!this.net.getTransitions().contains(transition)) return false;
		
		for (P p : this.net.getPreset(transition)) {
			if (this.get(p)==0) return false;
		}
		
		for (P p : this.net.getPreset(transition))
			this.put(p, this.get(p)-1);
		
		for (P p : this.net.getPostset(transition))
			this.put(p, this.get(p)+1);
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public IMarking<F,N,P,T> clone() {
		AbstractMarking<F,N,P,T> cloneMarking = (AbstractMarking<F,N,P,T>) super.clone(); 
		cloneMarking.net = this.net;
		return cloneMarking;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (P p : this.net.getPlaces())
			if (this.get(p) > 0)
				sb.append(p.getId() + "^" + this.get(p) + " ");
		
		return sb.toString();
	}

}
