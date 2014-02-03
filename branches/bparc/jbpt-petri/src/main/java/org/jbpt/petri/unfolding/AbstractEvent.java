package org.jbpt.petri.unfolding;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;

public abstract class AbstractEvent<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>>
	extends AbstractBPNode<N>
	implements IEvent<BPN,C,E,F,N,P,T,M> {
	
	static private int count = 0; 

	// required to capture unfolding
	private T t = null;	// transition that corresponds to event
	private ICoSet<BPN,C,E,F,N,P,T,M> pre = null;		// preconditions of event - *e
	
	// for convenience reasons
	private ICoSet<BPN,C,E,F,N,P,T,M> post = null;						// postconditions of event - e*
	
	private ILocalConfiguration<BPN,C,E,F,N,P,T,M> lc = null;
	
	private ICompletePrefixUnfolding<BPN,C,E,F,N,P,T,M> CPU = null;
	
	protected AbstractEvent() {
		this.ID = ++AbstractEvent.count;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param unf Reference to the unfolding.
	 * @param transition Transition whose occurrence is represented by this event.
	 * @param preset Preset of conditions which caused this event to occur.
	 */
	public AbstractEvent(ICompletePrefixUnfolding<BPN,C,E,F,N,P,T,M> cpu, T transition, ICoSet<BPN,C,E,F,N,P,T,M> preset) {
		this.ID = ++AbstractEvent.count;
		
		this.CPU = cpu;
		this.t = transition;
		this.pre = preset;
	}
	
	@Override
	public ICoSet<BPN,C,E,F,N,P,T,M> getPostConditions() {
		return this.post;
	}
	
	@Override
	public T getTransition() {
		return this.t;
	}
	
	@Override
	public ICoSet<BPN,C,E,F,N,P,T,M> getPreConditions() {
		return this.pre;
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
	
	@Override
	public String getName() {
		return String.format("%s-%s",this.t.getName(),this.ID);
	}
	
	@Override
	public boolean equals(Object that) {
		if (that == null || !(that instanceof IEvent)) return false;
		if (this == that) return true;
		
		@SuppressWarnings("unchecked")
		E thatE = (E) that;
		if (this.getTransition().equals(thatE.getTransition())
				&& this.getPreConditions().equals(thatE.getPreConditions()))
			return true;
		
		return false;
	}
	
	@Override
	public int hashCode() {
		int hashCode = 7 * this.getTransition().hashCode();
		for (C c : this.getPreConditions())
			hashCode += 11 * c.getPlace().hashCode();
		
		return hashCode;
	}

	@SuppressWarnings("unchecked")
	@Override
	public N getPetriNetNode() {
		return (N)this.t;
	}

	@Override
	public boolean isEvent() {
		return true;
	}

	@Override
	public boolean isCondition() {
		return false;
	}

	@Override
	public void setTransition(T transition) {
		this.t = transition;
	}

	@Override
	public void setPreConditions(ICoSet<BPN,C,E,F,N,P,T,M> preConditions) {
		this.pre = preConditions;
	}

	@Override
	public void setPostConditions(ICoSet<BPN,C,E,F,N,P,T,M> postConditions) {
		this.post = postConditions;	
	}

	@SuppressWarnings("unchecked")
	@Override
	public ILocalConfiguration<BPN,C,E,F,N,P,T,M> getLocalConfiguration() {
		if (this.lc!=null) return this.lc; 
		
		ILocalConfiguration<BPN, C, E, F, N, P, T, M> lc = null;
		try {
			lc = (ILocalConfiguration<BPN,C,E,F,N,P,T,M>)AbstractLocalConfiguration.class.newInstance();
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		}
		lc.setEvent((E)this);
		lc.setCompletePrefixUnfolding(this.CPU);
		lc.construct();
		
		this.lc = lc;
		return this.lc;
	}

	@Override
	public void setCompletePrefixUnfolding(ICompletePrefixUnfolding<BPN, C, E, F, N, P, T, M> cpu) {
		this.CPU = cpu;
	}
}
