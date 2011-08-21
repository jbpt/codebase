package de.hpi.bpt.process.petri.unf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hpi.bpt.process.petri.Marking;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;

/**
 * Simple unfolding of a net system (maximal branching process)
 * Note that unfolding of live system is infinite!
 * 
 * Javier Esparza, Stefan Roemer, Walter Vogler: An Improvement of McMillan's Unfolding Algorithm. Formal Methods in System Design (FMSD) 20(3):285-310 (2002)
 * 
 * @author Artem Polyvyanyy
 */
public class Unfolding {
	// originative net system
	protected PetriNet net = null;
	protected UnfoldingSetup setup = null;

	// unfolding
	private Set<Event> events		= new HashSet<Event>();			// events of the unfolding
	private Set<Condition> conds	= new HashSet<Condition>();	// conditions of the unfolding
	
	// map a condition to a set of cuts that contain the condition
	private Map<Condition,Collection<Cut>> c2cut = new HashMap<Condition,Collection<Cut>>();
	
	// maps of transitions/places to sets of events/conditions (occurrences of transitions/places)
	protected Map<Transition,Set<Event>> t2es	= new HashMap<Transition,Set<Event>>();
	protected Map<Place,Set<Condition>> p2cs	= new HashMap<Place,Set<Condition>>();
	
	// ordering relations
	protected Map<BPNode,Set<BPNode>> co	= new HashMap<BPNode,Set<BPNode>>(); // concurrent
	protected Map<BPNode,Set<BPNode>> ca	= new HashMap<BPNode,Set<BPNode>>(); // causal
	protected Map<BPNode,Set<BPNode>> ica	= new HashMap<BPNode,Set<BPNode>>(); // inverse causal
	
	// event counter
	private int countEvent = 0;
	
	// map of cutoff events to corresponding events
	protected Map<Event,Event> cutoff2corr = new HashMap<Event,Event>();
	
	// initial branching process
	protected Cut initialBP = new Cut();
	
	/**
	 * Dummy constructor
	 */
	protected Unfolding() {}
	
	/**
	 * Constructor - constructs unfolding of a net system
	 * 
	 * @param pn net system to unfold
	 */
	public Unfolding(PetriNet pn) {
		this(pn,new UnfoldingSetup());
	}
	
	/**
	 * Constructor - constructs unfolding of a net system
	 * 
	 * @param pn net system to unfold
	 * @param setup unfolding configuration
	 */
	public Unfolding(PetriNet pn, UnfoldingSetup setup) {
		this.net = pn;
		this.setup = setup;
		
		// construct unfolding
		this.construct();
	}

	/**
	 * Construct unfolding
	 * @throws  
	 */
	protected void construct() {
		if (this.net==null) return;

		// CONSTRUCT INITIAL BRANCHING PROCESS
		Marking M0 = this.net.getMarking();
		for (Place p : this.net.getPlaces()) {
			Integer n = M0.get(p);
			for (int i = 0; i<n; i++) {
				Condition c = new Condition(p,null);
				this.addCondition(c);
				initialBP.add(c);
			}
		}
		if (!this.addCut(initialBP)) return;
		
		// CONSTRUCT UNFOLDING
		// get possible extensions of initial branching process
		Collection<Event> pe = getPossibleExtensions();
		while (pe.size()>0) { 											// while extensions exist
			Event e = this.setup.ADEQUATE_ORDER.getMininmal(this,pe);	// event to use for extending unfolding
			
			if (!this.overlap(cutoff2corr.keySet(),e.getLocalConfiguration())) {
				this.addEvent(e);										// add event to unfolding

				if (++this.countEvent>=this.setup.MAX_EVENTS) return;	// track number of events in unfolding
				
				pe = getPossibleExtensions();							// get possible extensions of unfolding
				
				Event corr = this.checkCutoff(e);						// check for cutoff event 
				if (corr!=null) this.cutoff2corr.put(e,corr);			// e is cutoff event
			}
			else {
				pe.remove(e);
			}
			
			if (pe.size() == 0) pe = this.getMorePossibleExtensions();	// extension point
		}
	}

	/**
	 * Get possible extensions of the unfolding
	 * @return collection of events suitable to extend unfolding
	 */
	protected Collection<Event> getPossibleExtensions() {
		Collection<Event> result = new ArrayList<Event>();
		
		// iterate over all transitions of the originative net
		for (Transition t : this.net.getTransitions()) {
			// iterate over all places in the preset
			Collection<Place> pre = this.net.getPreset(t);
			for (Place p : pre) {
				// get cuts that contain conditions that correspond to the place
				Collection<Cut> cuts = this.getCutsWithPlace(p);
				// there must be at least one cut
				if (cuts.size()==0) break;
				// iterate over cuts
				for (Cut cut : cuts) {
					// get co-set of conditions that correspond to places in the preset (contained in the cut)
					Cut coset = this.contains(cut,pre);
					if (coset!=null) { // if there exists such a co-set
						// check if there already exists an event that corresponds to the transition with the preset of conditions which equals to coset 
						boolean flag = false;
						if (t2es.get(t)!=null) {
							for (Event e : t2es.get(t)) {
								if (this.equal(e.getPreConditions(),coset)) {
									flag = true;
									break;
								}
								if (flag) break;
							}
						}
						if (!flag) { // we found possible extension !!!
							Event e = new Event(this,t,coset);
							result.add(e);
						}
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Get possible extensions when unfolding is constructed (extension point)
	 * @return collection of events suitable to extend unfolding
	 */
	protected Collection<Event> getMorePossibleExtensions() {
		return new ArrayList<Event>();
	}
	
	/**
	 * Check whether event is cutoff
	 * @param e event
	 * @return corresponding event; null if event is not cutoff
	 */
	protected Event checkCutoff(Event e) {
		LocalConfiguration lce = e.getLocalConfiguration();
		for (Event f : this.getEvents()) {
			if (f.equals(e)) continue;
			LocalConfiguration lcf = f.getLocalConfiguration();	
			if (lce.getMarking().equals(lcf.getMarking()) && this.setup.ADEQUATE_ORDER.isSmaller(lcf, lce))
				return f;
		}
		
		return null;
	}
	
	/**************************************************************************
	 * Manage ordering relations
	 **************************************************************************/
	
	/**
	 * Update concurrency relation based on a cut
	 * @param cut cut
	 */
	private void updateConcurrency(Cut cut) {
		for (Condition c1 : cut) {
			if (this.co.get(c1)==null) this.co.put(c1, new HashSet<BPNode>());
			Event e1 = c1.getPreEvent();
			if (e1 != null && this.co.get(e1)==null) this.co.put(e1, new HashSet<BPNode>());
			for (Condition c2 : cut) {
				if (this.co.get(c2)==null) this.co.put(c2, new HashSet<BPNode>());
				this.co.get(c1).add(c2);
				
				Event e2 = c2.getPreEvent();
				if (e1!=null && e2!=null && !this.ca.get(e1).contains(e2) && !this.ca.get(e2).contains(e1)) this.co.get(e1).add(e2);
				if (!c1.equals(c2) && e1!=null && !this.ca.get(c2).contains(e1) && !this.ca.get(e1).contains(c2)) {
					this.co.get(c2).add(e1);
					this.co.get(e1).add(c2);
				}
			}
		}
	}
	
	/**
	 * Update causality relation based on a condition
	 * @param c condition
	 */
	private void updateCausalityCondition(Condition c) {
		this.ica.put(c, new HashSet<BPNode>());
		this.ca.put(c, new HashSet<BPNode>());
		
		Event e = c.getPreEvent();
		if (e==null) return;
		
		this.ica.get(c).addAll(this.ica.get(e));
		this.ica.get(c).add(e);
		
		for (BPNode n : this.ica.get(c)) {
			this.ca.get(n).add(c);
		}
	}
	
	/**
	 * Update causality relation based on an event
	 * @param e event
	 */
	private void updateCausalityEvent(Event e) {
		this.ica.put(e, new HashSet<BPNode>());
		this.ca.put(e, new HashSet<BPNode>());
		
		Collection<Condition> cs = e.getPreConditions();		
		for (Condition c : cs)  this.ica.get(e).addAll(this.ica.get(c));
		this.ica.get(e).addAll(cs);
		
		for (BPNode n : this.ica.get(e)) {
			this.ca.get(n).add(e);
		}
	}
	
	/**************************************************************************
	 * Useful methods
	 **************************************************************************/
	
	/**
	 * Get cuts that contain conditions that correspond to the place
	 * @param p place
	 * @return collection of cuts that contain conditions that correspond to the place
	 */
	protected Collection<Cut> getCutsWithPlace(Place p) {
		Collection<Cut> result = new ArrayList<Cut>();
		
		Collection<Condition> cs = p2cs.get(p);
		if (cs==null) return result;
		for (Condition c : cs)
			result.addAll(c2cut.get(c));
		
		return result;
	}

	/**
	 * Check if two sets of conditions are equal
	 * @param cs1
	 * @param cs2
	 * @return true if sets are equal; otherwise false
	 */
	protected boolean equal(Set<Condition> cs1, Set<Condition> cs2) {
		if (cs1.size()!= cs2.size()) return false;
		
		for (Condition c1 : cs1) {
			boolean flag = false;
			for (Condition c2 : cs2) {
				if (c1.equals(c2)) {
					flag = true;
					break;
				}
			}
			if (!flag) return false;
		}
		
		return true;
	}

	/**
	 * Check if cut contains conditions that correspond to places in a collection
	 * @param cut cut
	 * @param ps collection of places
	 * @return co-set of conditions that correspond to places in the collection; null if not every place has a corresponding condition 
	 */
	protected Cut contains(Cut cut, Collection<Place> ps) {
		Cut result = new Cut();
		
		for (Place p : ps) {
			boolean flag = false;
			for (Condition c : cut) {
				if (c.getPlace().equals(p)) {
					flag = true;
					result.add(c);
					break;
				}
			}
			if (!flag) return null;
		}

		return result;
	}
	
	/**
	 * Check if cut contains conditions
	 * @param cut cut
	 * @param cs conditions
	 * @return true if cut contains conditions; otherwise false
	 */
	protected boolean contains(Collection<Condition> cut, Collection<Condition> cs) {
		for (Condition c1 : cs) {
			boolean flag = false;
			for (Condition c2 : cut) {
				if (c1.equals(c2)) {
					flag = true;
					break;
				}
			}
			if (!flag) return false;
		}
		
		return true;
	}
	
	protected boolean overlap(Set<Event> es1, Set<Event> es2) {
		for (Event e1 : es1) {
			for (Event e2 : es2) {
				if (e1.equals(e2)) return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Add condition to all housekeeping data structures 
	 * @param c condition
	 */
	protected void addCondition(Condition c) {
		this.conds.add(c);
		this.updateCausalityCondition(c);
		
		if (p2cs.get(c.getPlace())!=null)
			p2cs.get(c.getPlace()).add(c);
		else {
			Set<Condition> cs = new HashSet<Condition>();
			cs.add(c);
			p2cs.put(c.getPlace(), cs);
		}
	}
	
	/**
	 * Add event to all housekeeping data structures 
	 * @param e event
	 */
	protected void addEvent(Event e) {
		this.events.add(e);
		this.updateCausalityEvent(e);
		
		if (t2es.get(e.getTransition())!=null)
			t2es.get(e.getTransition()).add(e);
		else {
			Set<Event> es = new HashSet<Event>();
			es.add(e);
			t2es.put(e.getTransition(), es);
		}
		
		// add conditions that correspond to post-places of transition that corresponds to new event
		Set<Condition> postConds = new HashSet<Condition>(); // collection of new conditions
		for (Place s : this.net.getPostset(e.getTransition())) {	// iterate over places in the postset
			Condition c = new Condition(s,e);	 					// construct new condition
			postConds.add(c);
			this.addCondition(c);									// add condition to unfolding
		}
		e.setPostConditions(postConds);								// set post conditions of event
		
		// compute new cuts of unfolding
		for (Cut cut : c2cut.get(e.getPreConditions().iterator().next())) {
			if (contains(cut,e.getPreConditions())) {
				Cut newCut = new Cut(cut);
				newCut.removeAll(e.getPreConditions());
				newCut.addAll(postConds);
				if (!this.addCut(newCut)) return;
			}
		}
	}

	/**
	 * Add cut to all housekeeping data structures 
	 * @param cut cut
	 * @return true is cut was added successfully; otherwise false;
	 */
	protected boolean addCut(Cut cut) {
		this.updateConcurrency(cut);
		
		Map<Place,Integer> p2i = new HashMap<Place,Integer>();
		
		for (Condition c : cut) {
			// check bound
			Integer i = p2i.get(c.getPlace());
			if (i==null) p2i.put(c.getPlace(),1);
			else {
				if (i == this.setup.MAX_BOUND) return false;
				else p2i.put(c.getPlace(),i+1);
			}
			
			if (c2cut.get(c)!=null) c2cut.get(c).add(cut);
			else {
				Collection<Cut> cuts = new ArrayList<Cut>();
				cuts.add(cut);
				c2cut.put(c,cuts);
			}
		}
		
		return true;
	}
	
	/**************************************************************************
	 * Public interface
	 **************************************************************************/
	
	/**
	 * Get configuration of this unfolding
	 */
	public UnfoldingSetup getSetup() {
		return this.setup;
	}
	
	/**
	 * Get conditions
	 * @return conditions of unfolding
	 */
	public Set<Condition> getConditions() {
		return this.conds;
	}
	
	/**
	 * Get conditions that correspond to place
	 * @return conditions of unfolding that correspond to place
	 */
	public Set<Condition> getConditions(Place p) {
		return this.p2cs.get(p);
	}
	
	/**
	 * Get events
	 * @return conditions of unfolding
	 */
	public Set<Event> getEvents() {
		return this.events;
	}
	
	/**
	 * Get events that correspond to transition
	 * @return events of unfolding that correspond to transition
	 */
	public Set<Event> getEvents(Transition t) {
		return this.t2es.get(t);
	}
	
	/**
	 * Get originative net system
	 * @return originative net system
	 */
	public PetriNet getNet() {
		return this.net;
	}
	
	/**
	 * Check if two nodes are in causal relation
	 * @param n1 node
	 * @param n2 node
	 * @return true if n1 and n2 are in causal relation; otherwise false
	 */
	public boolean areCausal(BPNode n1, BPNode n2) {
		return this.ca.get(n1).contains(n2);
	}
	
	/**
	 * Check if two nodes are in inverse causal relation
	 * @param n1 node
	 * @param n2 node
	 * @return true if n1 and n2 are in inverse causal relation; otherwise false
	 */
	public boolean areInverseCausal(BPNode n1, BPNode n2) {
		return this.ica.get(n1).contains(n2);
	}
	
	/**
	 * Check if two nodes are concurrent
	 * @param n1 node
	 * @param n2 node
	 * @return true if n1 and n2 are concurrent; otherwise false
	 */
	public boolean areConcurrent(BPNode n1, BPNode n2) {
		if (this.co.get(n1)==null) return false;
		return this.co.get(n1).contains(n2);
	}
	
	/**
	 * Check if two nodes are in conflict
	 * @param n1 node
	 * @param n2 node
	 * @return true if n1 and n2 are in conflict; otherwise false
	 */
	public boolean areInConflict(BPNode n1, BPNode n2) {
		return !this.areCausal(n1,n2) && !this.areInverseCausal(n1,n2) && !this.areConcurrent(n1,n2);
	}
	
	/**
	 * Get ordering relation between two nodes
	 * @param n1 node
	 * @param n2 node
	 * @return ordering relation between n1 and n2
	 */
	public OrderingRelation getOrderingRelation(BPNode n1, BPNode n2) {
		if (this.areCausal(n1,n2)) return OrderingRelation.CAUSAL;
		if (this.areInverseCausal(n1,n2)) return OrderingRelation.INVERSE_CAUSAL;
		if (this.areConcurrent(n1,n2)) return OrderingRelation.CONCURRENT;
		if (this.areInConflict(n1,n2)) return OrderingRelation.CONFLICT;
		
		return OrderingRelation.NONE;
	}
	
	/**
	 * Get occurrence net that captures this unfolding
	 * @return occurrence net
	 */
	public OccurrenceNet getOccurrenceNet() {
		return new OccurrenceNet(this);
	}
	
	/**
	 * Print ordering relations to System.out - for debugging 
	 */
	public void printOrderingRelations() {
		List<BPNode> ns = new ArrayList<BPNode>();
		ns.addAll(this.getConditions());
		ns.addAll(this.getEvents());
		
		System.out.println(" \t");
		for (BPNode n : ns) System.out.print("\t"+n.getName());
		System.out.println();
		
		for (BPNode n1 : ns) {
			System.out.print(n1.getName()+"\t");
			for (BPNode n2 : ns) {
				String rel = "";
				if (this.areCausal(n1,n2)) rel = ">";
				if (this.areInverseCausal(n1,n2)) rel = "<";
				if (this.areConcurrent(n1,n2)) rel = "@";
				if (this.areInConflict(n1,n2)) rel = "#";
				System.out.print(rel + "\t");
			}
			System.out.println();
		}
	}
	
	/**
	 * Get all cutoff events
	 * @return all cutoff events
	 */
	public Set<Event> getCutoffEvents() {
		return this.cutoff2corr.keySet();
	}
	
	/**
	 * Check if event is cutoff event
	 * @param e event
	 * @return true if e is cutoff event; otherwise false
	 */
	public boolean isCutoffEvent(Event e) {
		return this.cutoff2corr.containsKey(e);
	}
	
	/**
	 * Get corresponding event
	 * @param e event
	 * @return corresponding event of e; null if e is not cutoff event
	 */
	public Event getCorrespondingEvent(Event e) {
		return this.cutoff2corr.get(e);
	}
}
