package org.jbpt.petri;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of a run of a net system.
 * 
 * @author Artem Polyvyanyy
 */
public class AbstractRun<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> 
		extends ArrayList<T> 
		implements IRun<F,N,P,T,M> {

	private static final long serialVersionUID = -4466441471941425047L;
	
	INetSystem<F,N,P,T,M> sys = null;
	M currentMarking = null;
	M initialMarking = null;
	Set<T> possibleExtensions = null;
	List<M> markings = null;
	
	@SuppressWarnings("unchecked")
	private void reset() {
		this.currentMarking = (M) this.initialMarking.clone();
		this.possibleExtensions = new HashSet<T>(this.sys.getEnabledTransitionsAtMarking(this.currentMarking));
		this.markings = new ArrayList<M>();
		this.markings.add(currentMarking);
	}
	
	public AbstractRun() {
		this.markings = new ArrayList<M>();
	};
	
	@SuppressWarnings("unchecked")
	public AbstractRun(INetSystem<F,N,P,T,M> sys) {
		this.sys = sys;
		this.initialMarking = (M) sys.getMarking().clone();
		this.reset();
	}
	
	@Override
	public Set<T> getPossibleExtensions() {		
		return this.possibleExtensions;
	}

	@Override
	public void add(int arg0, T arg1) {
		throw new UnsupportedOperationException("Cannot modify runs by adding transitions at arbitrary position.");
	}

	@Override
	public boolean add(T arg0) {
		if (this.possibleExtensions.contains(arg0)) {
			@SuppressWarnings("unchecked")
			M marking = (M) this.currentMarking.clone();
			marking.fire(arg0);
			this.markings.add(marking);
			this.currentMarking = marking;
			this.possibleExtensions.clear();
			this.possibleExtensions.addAll(this.sys.getEnabledTransitionsAtMarking(marking));
			return super.add(arg0);
		}
		else
			return false;
	}
	
	@Override
	public boolean append(T transition) {
		return this.add(transition);
	}
	
	@Override
	public List<M> getMarkings() {
		return this.markings;
	}

	@Override
	public boolean addAll(Collection<? extends T> arg0) {
		boolean result = false;
		for (T t : arg0) {
			result |= this.add(t);
		}
		return result;
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends T> arg1) {
		throw new UnsupportedOperationException("Cannot modify runs by adding transitions at arbitrary position.");
	}

	@Override
	public void clear() {
		super.clear();
		this.reset();
	}

	@SuppressWarnings("unchecked")
	@Override
	public IRun<F,N,P,T,M> clone() {
		AbstractRun<F,N,P,T,M> run = null;
		try {
			run = AbstractRun.class.newInstance();
		} catch (InstantiationException | IllegalAccessException exception) {
			return null;
		}
		
		run.initialMarking = (M) this.initialMarking.clone();
		run.currentMarking = (M) this.currentMarking.clone();
		run.possibleExtensions = new HashSet<T>(this.possibleExtensions);
		run.sys = this.sys;
		run.copyTransitions(this);
		run.markings.addAll(this.markings);
		
		return run;
	}
	
	private void copyTransitions(IRun<F,N,P,T,M> run) {
		super.addAll(run);
	}

	@Override
	public T remove(int arg0) {
		throw new UnsupportedOperationException("Cannot remove transitions from runs.");
	}

	@Override
	public boolean remove(Object arg0) {
		throw new UnsupportedOperationException("Cannot remove transitions from runs.");
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		throw new UnsupportedOperationException("Cannot remove transitions from runs.");
	}

	@Override
	protected void removeRange(int arg0, int arg1) {
		throw new UnsupportedOperationException("Cannot remove transitions from runs.");
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		throw new UnsupportedOperationException("Cannot modify runs at arbitrary positions.");
	}

	@Override
	public T set(int arg0, T arg1) {
		throw new UnsupportedOperationException("Cannot modify runs at arbitrary positions.");
	}

	@Override
	public List<T> subList(int arg0, int arg1) {
		throw new UnsupportedOperationException();
	}

}
