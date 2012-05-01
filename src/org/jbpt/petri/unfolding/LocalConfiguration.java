package org.jbpt.petri.unfolding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jbpt.petri.Marking;
import org.jbpt.petri.Transition;


/**
 * Local configuration of unfolding event - causality closed set of events  
 * 
 * @author Artem Polyvyanyy
 */
public class LocalConfiguration extends HashSet<Event> {
	private static final long serialVersionUID = 1L;
	
	private Unfolding unf = null;	// reference to unfolding
	private Event e = null;			// event
	private Cut C = null;			// cut
	private Marking M = null;		// marking of cut
	private List<Transition> vec = null;	// quasi Parikh vector
	private List<Set<Event>> foata = null;	// Foata normal form
	
	/**
	 * Constructor
	 * 
	 * @param unf reference to unfolding
	 * @param e event
	 */
	public LocalConfiguration(Unfolding unf, Event e) {
		this.unf = unf;
		this.e = e;
		
		this.add(this.e);
		
		for (Condition c : this.e.getPreConditions()) {
			for (BPNode n : this.unf.ica.get(c)) {
				if (n instanceof Event)
					this.add((Event) n);
			}
		}
	}
	
	/**
	 * Get cut
	 * @return cut
	 */
	public Cut getCut() {
		if (this.C == null) {
			this.C = new Cut(this.unf.getNetSystem());
			this.C.addAll(this.unf.initialBP);
			for (Event e : this) this.C.addAll(e.getPostConditions());
			for (Event e : this) this.C.removeAll(e.getPreConditions());
		}
		
		return this.C;
	}
	
	/**
	 * Get marking
	 * @return marking
	 */
	public Marking getMarking() {
		if (this.M == null) {
			this.M = new Marking(this.unf.getPetriNet());

			for (Condition c : this.getCut()) {
				if (c.getPlace() == null) this.M.put(c.getPlace(), 1);
				this.M.put(c.getPlace(), this.M.get(c.getPlace())+1);
			}
		}
		
		return this.M;
	}
	
	class ParikhComparator implements Comparator<Transition> {
		
		private List<Transition> totalOrderTs = null;
		
		public ParikhComparator(List<Transition> totalOrderTs) {
			this.totalOrderTs = totalOrderTs;
		}
		
		@Override
		public int compare(Transition t1, Transition t2) {
			int i1 = this.totalOrderTs.indexOf(t1);
			int i2 = this.totalOrderTs.indexOf(t2);
			if (i1<i2) return -1;
			if (i1>i2) return 1;
			
			return 0;
		}
	}
	
	public List<Transition> getQuasiParikhVector() {
		if (this.vec == null) {
			this.vec = new ArrayList<Transition>();
			for (Event e : this) this.vec.add(e.getTransition());
			Collections.sort(this.vec, new ParikhComparator(this.unf.totalOrderTs));
		}
		
		return this.vec;
	}
	
	// TODO cache this
	public List<Transition> getQuasiParikhVector(Collection<Event> es) {
		List<Transition> result = new ArrayList<Transition>();
		for (Event e : es) result.add(e.getTransition());
		Collections.sort(result, new ParikhComparator(this.unf.totalOrderTs));
		return result;
	}	
	
	public List<Set<Event>> getFoataNormalForm() {
		if (this.foata == null) {
			this.foata = new ArrayList<Set<Event>>();
			Collection<Event> lc = new ArrayList<Event>(this);
			while (lc.size()>0) {
				Set<Event> min = this.getMin(lc);
				this.foata.add(min);
				lc.removeAll(min);
			}
		}
		
		return this.foata;
	}
	
	private Set<Event> getMin(Collection<Event> lc) {
		Set<Event> result = new HashSet<Event>();
		for (Event e1 : lc) {
			boolean flag = true;
			for (Event e2 : lc) {
				if (this.unf.areCausal(e2,e1)) {
					flag = false;
					break;
				}
			}
			
			if (flag) result.add(e1);
		}
		return result;
	}

	public Integer compareTransitions(Transition t1, Transition t2) {
		int i1 = this.unf.totalOrderTs.indexOf(t1);
		int i2 = this.unf.totalOrderTs.indexOf(t2);
		if (i1<0 || i2<0) return null;
		
		if (i1<i2) return -1;
		if (i1>i2) return 1;
		
		return 0;
	}
}
