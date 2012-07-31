package org.jbpt.petri.unfolding;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jbpt.petri.Marking;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;


/**
 * Coset - a set of mutually concurrent conditions.
 * 
 * @author Artem Polyvyanyy
 */
public class Coset extends TreeSet<Condition> {
	private static final long serialVersionUID = 1L;
	private final static CosetComparator coset_comparator = new CosetComparator();
	
	private PetriNet net = null;
	
	public Coset(PetriNet net) {
		this.net = net;
	}
	
	private Map<Place,Set<Condition>> p2cs = new HashMap<Place,Set<Condition>>(); 
	
	@Override
	public Comparator<? super Condition> comparator() {
		return Coset.coset_comparator;
	}
	
	@Override
	public int hashCode() {
		int code = 0;
		for (Condition c : this) {
			code += c.hashCode();
		}
		
		return code;
	}
	
	@Override
	public boolean add(Condition c) {
		if (this.p2cs.get(c.getPlace())==null) {
			Set<Condition> cs = new HashSet<Condition>();
			cs.add(c);
			this.p2cs.put(c.getPlace(),cs);
		}
		else
			this.p2cs.get(c.getPlace()).add(c);
			
		return super.add(c);
	}

	@Override
	public boolean addAll(Collection<? extends Condition> cs) {
		boolean result=false;
		for (Condition c : cs) result |= this.add(c);
		return result;
	}

	@Override
	public boolean remove(Object c) {
		if (this.p2cs.get(((Condition) c).getPlace()) != null)
			this.p2cs.get(((Condition) c).getPlace()).remove(c);
		return super.remove(c);
	}

	@Override
	public boolean removeAll(Collection<?> cs) {
		this.p2cs = new HashMap<Place,Set<Condition>>(); 
		return super.removeAll(cs);
	}
	
	public Set<Condition> getConditions(Place p) {
		return this.p2cs.get(p);
	}
	
	public Set<Place> getPlaces() {
		return this.p2cs.keySet();
	}
	
	public PetriNet getPetriNet() {
		return this.net;
	}
	
	public Marking getMarking() {
		Marking result = new Marking(this.net);
		
		for (Condition c : this) {
			Place p = c.getPlace();
			result.put(p, result.get(p) + 1);
		}
		
		return result;
	}
}
