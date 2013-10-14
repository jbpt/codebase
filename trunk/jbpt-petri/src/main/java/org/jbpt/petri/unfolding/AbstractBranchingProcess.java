package org.jbpt.petri.unfolding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpt.algo.ListCombinationGenerator;
import org.jbpt.algo.MultiSetUtils;
import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;

/**
 * Abstract implementation of a branching process of a net system.
 * 
 * @author Artem Polyvyanyy
 */
public abstract class AbstractBranchingProcess<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, 
												F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> 
		implements IBranchingProcess<BPN,C,E,F,N,P,T,M> 
{
	// originative net system
	protected INetSystem<F,N,P,T,M> sys = null;
	
	protected List<E> log = null;
	
	protected Set<E> events	= null;	
	protected Set<C> conds	= null;
	protected ICut<BPN,C,E,F,N,P,T,M> iniBP = null;
	
	// causality: maps node of unfolding to a set of preceding nodes
	protected Map<BPN,Set<BPN>> ca = null;
	// concurrency: maps node of unfolding to a set of concurrent nodes
	protected Map<BPN,Set<BPN>> co = null;
	
	// indexes for conflict and concurrency relations 
	private Map<BPN,Set<BPN>> EX    = null;
	private Map<BPN,Set<BPN>> notEX = null;
	private Map<BPN,Set<BPN>> CO    = null;
	private Map<BPN,Set<BPN>> notCO = null;
	
	protected AbstractBranchingProcess() {
		this.initialize();
	}
	
	protected AbstractBranchingProcess(INetSystem<F,N,P,T,M> sys) {
		this.setNetSystem(sys);
	}
	
	protected void initialize() {
		this.log = new ArrayList<E>();
		this.events	= new HashSet<E>();
		this.conds	= new HashSet<C>();
		this.iniBP	= this.createCut();
		this.ca		= new HashMap<BPN,Set<BPN>>();
		this.co		= new HashMap<BPN,Set<BPN>>();
		this.EX		= new HashMap<BPN,Set<BPN>>();
		this.notEX	= new HashMap<BPN,Set<BPN>>();
		this.CO		= new HashMap<BPN,Set<BPN>>();
		this.notCO	= new HashMap<BPN,Set<BPN>>();
	}

	@Override
	public void constructInitialBranchingProcess() {
		if (!this.conds.isEmpty()) return;
		for (P p : this.sys.getMarking().toMultiSet()) {
			C c = this.createCondition(p,null);
			this.appendCondition(c);
			this.iniBP.add(c);
		}
	}

	@Override
	public Set<C> getConditions() {
		return this.conds;
	}

	@Override
	public Set<C> getConditions(P place) {
		Set<C> result = new HashSet<C>();
		for (C c : this.conds) {
			if (c.getPlace().equals(place))
				result.add(c);
		}
		return result;
	}

	@Override
	public Set<E> getEvents() {
		return this.events;
	}

	@Override
	public Set<E> getEvents(T transition) {
		Set<E> result = new HashSet<E>();
		for (E e : this.events) {
			if (e.getTransition().equals(transition))
				result.add(e);
		}
		return result;
	}

	@Override
	public INetSystem<F,N,P,T,M> getOriginativeNetSystem() {
		return this.sys;
	}

	@Override
	public boolean areCausal(BPN n1, BPN n2) {
		if (this.ca.get(n2)==null) {
			if (n2 instanceof AbstractEvent) {
				@SuppressWarnings("unchecked")
				E e = (E) n2;
				if (e.getPreConditions().contains(n1)) return true;
				for (C c : e.getPreConditions())
					if (this.ca.get(c).contains(n1))
						return true;
				
				return false;
			}
			else {
				@SuppressWarnings("unchecked")
				C c = (C) n2;
				if (c.getPreEvent().equals(n1)) return true;
				if (this.ca.get(c.getPreEvent()).contains(n1)) return true;
				
				return false;
			}
		}
		
		return this.ca.get(n2).contains(n1);
	}

	@Override
	public boolean areInverseCausal(BPN n1, BPN n2) {
		return this.areCausal(n2,n1);
	}

	@Override
	public boolean areConcurrent(BPN n1, BPN n2) {
		Set<BPN> co = this.CO.get(n1);
		if (co!=null)
			if (co.contains(n2)) return true;
		
		Set<BPN> notCo = this.notCO.get(n1);
		if (notCo!=null)
			if (notCo.contains(n2)) return false;
		
		boolean result = !this.areCausal(n1,n2) && !this.areInverseCausal(n1,n2) && !this.areInConflict(n1,n2);
		
		if (result)
			this.index(this.CO,n1,n2);
		else
			this.index(this.notCO,n1,n2);
		
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean areInConflict(BPN n1, BPN n2) {
		Set<BPN> ex = this.EX.get(n1);
		if (ex!=null)
			if (ex.contains(n2)) return true;
		
		Set<BPN> notEx = this.notEX.get(n1);
		if (notEx!=null)
			if (notEx.contains(n2)) return false;
		
		if (n1.equals(n2)) {
			this.index(this.notEX,n1,n2);
			return false;
		}
		
		Set<BPN> ca1 = new HashSet<BPN>(this.ca.get(n1));
		ca1.add(n1);
		Set<BPN> ca2 = new HashSet<BPN>(this.ca.get(n2));
		ca2.add(n2);
		
		for (BPN nn1 : ca1) {
			if (nn1.isCondition()) continue;
			E e1 = (E) nn1;
			for (BPN nn2 : ca2) {
				if (nn2.isCondition()) continue;
				E e2 = (E) nn2;
				if (e1.equals(e2)) continue;				
				if (!this.overlap(e1.getPreConditions(),e2.getPreConditions())) continue;
				
				this.index(this.EX,n1,n2);
				return true;
			}
		}
		
		this.index(this.notEX,n1,n2);
		return false;
	}

	@Override
	public OrderingRelationType getOrderingRelation(BPN n1, BPN n2) {
		if (this.areCausal(n1,n2)) return OrderingRelationType.CAUSAL;
		if (this.areInverseCausal(n1,n2)) return OrderingRelationType.INVERSE_CAUSAL;
		if (this.areInConflict(n1,n2)) return OrderingRelationType.CONFLICT;
		return OrderingRelationType.CONCURRENT;
	}

	@SuppressWarnings("unchecked")
	@Override
	public C createCondition(P place, E event) {
		C c = null;
		try {
			c = (C) Condition.class.newInstance();
			c.setPlace(place);
			c.setPreEvent(event);
			return c;
		} catch (InstantiationException exception) {
			exception.printStackTrace();
		} catch (IllegalAccessException exception) {
			exception.printStackTrace();
		}
		
		return c;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E createEvent(T transition, ICoSet<BPN,C,E,F,N,P,T,M> preConditions) {
		E e = null;
		try {
			e = (E) Event.class.newInstance();
			e.setTransition(transition);
			e.setPreConditions(preConditions);
			return e;
		} catch (InstantiationException exception) {
			exception.printStackTrace();
		} catch (IllegalAccessException exception) {
			exception.printStackTrace();
		}
		
		return e;
	}
	
	@Override
	public boolean appendCondition(C condition) {
		this.conds.add(condition);
		this.updateCausalityCondition(condition);
		
		return true;
	}
		
	@SuppressWarnings("unchecked")
	protected void updateCausalityCondition(C c) {
		if (this.ca.get(c)==null)
			this.ca.put((BPN)c,new HashSet<BPN>());
		
		E e = c.getPreEvent();
		if (e==null) return;
		
		this.ca.get(c).addAll(this.ca.get(e));
		this.ca.get(c).add((BPN)e);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean appendEvent(E event) {
		this.events.add(event);		
		this.updateCausalityEvent(event);
		
		// add conditions that correspond to post-places of transition that corresponds to new event
		ICoSet<BPN,C,E,F,N,P,T,M> postConditions = null;
		try {
			postConditions = (ICoSet<BPN,C,E,F,N,P,T,M>) AbstractCoSet.class.newInstance();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		
		for (P s : this.sys.getPostset(event.getTransition())) {
			C c = this.createCondition(s,event);
			postConditions.add(c);
			this.appendCondition(c);
		}
		event.setPostConditions(postConditions);

		this.log.add(event);
		return true;
	}
	
	@Override
	public boolean appendTransition(T transition) {
		ICoSet<BPN,C,E,F,N,P,T,M> preset = this.createCoSet();
		
		Set<C> max = this.getMax();
		for (P p : this.getOriginativeNetSystem().getPreset(transition)) {
			boolean flag = false;
			for (C c : this.getMax()) {
				if (c.getPlace().equals(p) && max.contains(c)) {
					preset.add(c);
					max.remove(c);
					flag = true;
				}
			}
			
			if (!flag) return false;
		}
		
		E e = this.createEvent(transition, preset);
		return this.appendEvent(e);
	}
	
	@SuppressWarnings("unchecked")
	private void updateCausalityEvent(E e) {
		if (this.ca.get(e)==null)
			this.ca.put((BPN)e, new HashSet<BPN>());
		
		for (C c : e.getPreConditions()) {
			this.ca.get(e).addAll(this.ca.get(c));
		}
		
		for (C c : e.getPreConditions())
			this.ca.get(e).add((BPN)c);
	}
	
	private void index(Map<BPN,Set<BPN>> map, BPN n1, BPN n2) {
		Set<BPN> s1 = map.get(n1);
		if (s1==null) {
			Set<BPN> ss1 = new HashSet<BPN>();
			ss1.add(n2);
			map.put(n1,ss1);
		}
		else
			s1.add(n2);
		
		Set<BPN> s2 = map.get(n2);
		if (s2==null) {
			Set<BPN> ss2 = new HashSet<BPN>();
			ss2.add(n1);
			map.put(n2,ss2);
		}
		else
			s2.add(n1);
	}
	
	private boolean overlap(ICoSet<BPN,C,E,F,N,P,T,M> s1, ICoSet<BPN,C,E,F,N,P,T,M> s2) {
		for (C n : s1)
			if (s2.contains(n))
				return true;
		
		return false;
	}

	@Override
	public ICut<BPN,C,E,F,N,P,T,M> getInitialCut() {
		return this.iniBP;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ICoSet<BPN,C,E,F,N,P,T,M> createCoSet() {
		try {
			return (ICoSet<BPN,C,E,F,N,P,T,M>) CoSet.class.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ICut<BPN,C,E,F,N,P,T,M> createCut() {
		try {
			return (ICut<BPN,C,E,F,N,P,T,M>) Cut.class.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Set<BPN> getCausalPredecessors(BPN node) {
		return this.ca.get(node);
	}

	@Override
	public boolean isConflictFree() {
		Map<C,E> c2e = new HashMap<C,E>();
		for (E e : this.events) {
			for (C c : e.getPreConditions()) {
				if (c2e.get(c)!=null) return false;
				c2e.put(c,e);
			}
		}
		
		return true;
	}
	
	@Override
	public void setNetSystem(INetSystem<F,N,P,T,M> system) {
		if (system==null) return;
		this.sys = system;
		this.initialize();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean isSafe() {
		for (C c1 : this.conds) {
			for (C c2 : this.conds) {
				if (c1.equals(c2)) continue;
				if (c1.getPlace().equals(c2.getPlace()) && this.areConcurrent((BPN)c1,(BPN)c2))
					return false;
			}	
		}
		return true;
	}
	
	@Override
	public Set<C> getMin() {
		return this.iniBP;
	}

	@Override
	public Set<C> getMax() {
		Set<C> result = new HashSet<C>(this.conds);
		for (E e : this.events) {
			result.removeAll(e.getPreConditions());
		}
		return result;
	}
	
	@Override
	public Set<P> getPlaces(Collection<C> conditions) {
		Set<P> result = new HashSet<P>();
		if (conditions==null) return result;
		
		for (C c : conditions) {
			result.add(c.getPlace());
		}
		
		return result;
	}

	@Override
	public Set<T> getTransitions(Collection<E> events) {
		Set<T> result = new HashSet<T>();
		if (events==null) return result;
		
		for (E e : events) {
			result.add(e.getTransition());
		}
		
		return result;
	}
	
	@Override
	public IOccurrenceNet<BPN,C,E,F,N,P,T,M> getOccurrenceNet() {		
		try {
			@SuppressWarnings("unchecked")
			IOccurrenceNet<BPN,C,E,F,N,P,T,M> occ = (IOccurrenceNet<BPN,C,E,F,N,P,T,M>) OccurrenceNet.class.newInstance();
			occ.setBranchingProcess(this);
			return occ;
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public IBranchingProcess<BPN,C,E,F,N,P,T,M> clone() {
		IBranchingProcess<BPN,C,E,F,N,P,T,M> clone = null;
		try {
			clone = (IBranchingProcess<BPN,C,E,F,N,P,T,M>)BranchingProcess.class.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		clone.setNetSystem(this.sys);
		
		for (C c : this.conds) {
			if (c.getPreEvent()!=null) continue;
			C newC = this.createCondition(c.getPlace(), null);
			clone.appendCondition(newC);
		}
		
		for (E e : this.log) {
			E newE = this.createEvent(e.getTransition(), null);
			ICoSet<BPN,C,E,F,N,P,T,M> preset = this.createCoSet();
			
			Set<C> max = new HashSet<C>(clone.getMax());
			for (C c2 : e.getPreConditions()) {
				for (C c1 : max) {
					if (c1.getPlace().equals(c2.getPlace())) {
						preset.add(c1);
						max.remove(c1);
						break;
					}
				}
			}
			
			newE.setPreConditions(preset);
			clone.appendEvent(newE);
		}
		
		return clone;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean isCut(Collection<C> conditions) {
		for (C c1 : conditions) {
			Collection<C> cs = new ArrayList<C>(conditions);
			cs.remove(c1);
			for (C c2 : cs) {
				if (c1.equals(c2)) return false;
				
				if (!this.areConcurrent((BPN)c1,(BPN)c2))
					return false;
			}
		}
		
		for (C c : this.getConditions()) {
			if (conditions.contains(c)) continue; 
			
			boolean flag = true;			
			for (C cc : conditions) {
				if (!this.areConcurrent((BPN)c,(BPN)cc)) {
					flag = false;
					break;
				}
			}
			
			if (flag)
				return false;
			
		}
		
		return true;
	}
	
	@Override
	public Set<ICut<BPN,C,E,F,N,P,T,M>> getCuts(Collection<P> places) {
		Set<ICut<BPN,C,E,F,N,P,T,M>> result = new HashSet<ICut<BPN, C, E, F, N, P, T, M>>();
		List<List<C>> conds = new ArrayList<List<C>>();
		
		for (P p : places) {
			conds.add(new ArrayList<C>(this.getConditions(p)));
		}
		
		ListCombinationGenerator<C> lcg = new ListCombinationGenerator<C>(conds);
		
		while(lcg.hasMoreCombinations()) {
			List<C> cs = lcg.getNextCombination();
			if (!MultiSetUtils.isSet(cs)) continue;
			if (this.isCut(cs)) {
				ICut<BPN,C,E,F,N,P,T,M> cut = this.createCut();
				cut.addAll(cs);
				result.add(cut);
			}
		}
		
		return result;
	}
	
	@Override
	public List<E> getLog() {
		return this.log;
	}
	
	/*protected boolean appendEvent2(Event e) {
		this.events.add(e);
		this.updateCausalityEvent(e);
		
		if (t2es.get(e.getTransition())!=null) t2es.get(e.getTransition()).add(e);
		else {
			Set<Event> es = new HashSet<Event>();
			es.add(e);
			t2es.put(e.getTransition(), es);
		}
		
		// add conditions that correspond to post-places of transition that corresponds to new event
		CoSet postConds = new CoSet(this.sys);						// collection of new post conditions
		for (Place s : this.sys.getPostset(e.getTransition())) {	// iterate over places in the postset
			Condition c = new Condition(s,e);	 					// construct new condition
			postConds.add(c);
			this.addCondition(c);									// add condition to unfolding
		}
		e.setPostConditions(postConds);								// set post conditions of event
		
		// compute new cuts of unfolding
		for (Cut cut : c2cut.get(e.getPreConditions().iterator().next())) {
			if (contains(cut,e.getPreConditions())) {
				Cut newCut = new Cut(this.sys,cut);
				newCut.removeAll(e.getPreConditions());
				newCut.addAll(postConds);
				if (!this.addCut(newCut)) return false;
			}
		}
		
		this.countEvents++;
		return true;
	}*/
}
