package org.jbpt.petri;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of a run of a net system.
 * 
 * @author Artem Polyvyanyy
 */
public class AbstractRun<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> 
		extends ArrayList<IStep<F,N,P,T,M>> 
		implements IRun<F,N,P,T,M> {

	private static final long serialVersionUID = -4466441471941425047L;
	
	INetSystem<F,N,P,T,M> sys = null;
	
	M currentMarking = null;
	M initialMarking = null;
	Set<T> possibleExtensions = null;
	
	@SuppressWarnings("unchecked")
	private void reset() {
		this.currentMarking = (M) this.initialMarking.clone();
		this.possibleExtensions = new HashSet<T>(this.sys.getEnabledTransitionsAtMarking(this.currentMarking));
	}
	
	public AbstractRun() {
	};
	
	@SuppressWarnings("unchecked")
	public AbstractRun(INetSystem<F,N,P,T,M> sys) {
		this.initialMarking = (M) sys.getMarking().clone();
		this.sys = sys;
		this.reset();
	}
	
	@Override
	public Set<T> getPossibleExtensions() {		
		return this.possibleExtensions;
	}
	
	@Override
	public boolean append(T transition) {
		if (this.possibleExtensions.contains(transition)) {
			IStep<F,N,P,T,M> step = this.createStep(this.sys,this.currentMarking,transition);
			this.currentMarking = step.getOutputMarking();
			this.possibleExtensions.clear();
			this.possibleExtensions.addAll(this.sys.getEnabledTransitionsAtMarking(this.currentMarking));
			return super.add(step);
		}
		else
			return false;
	}
	
	@Override
	public boolean add(IStep<F,N,P,T,M> arg0) {
		throw new UnsupportedOperationException("Cannot modify runs by adding transitions at arbitrary position.");
	}

	@Override
	public boolean addAll(Collection<? extends IStep<F,N,P,T,M>> arg0) {
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
		run.currentMarking = (M) this.initialMarking.clone();
		run.sys = this.sys;
		run.possibleExtensions = new HashSet<T>(run.sys.getEnabledTransitionsAtMarking(run.currentMarking));
		run.copyTransitions(this);
		
		return (IRun<F,N,P,T,M>)run;
	}
	
	private void copyTransitions(IRun<F,N,P,T,M> run) {
		for (IStep<F,N,P,T,M> step : run) {
			this.append(step.getTransition());
		}
	}

	protected IStep<F,N,P,T,M> createStep(IPetriNet<F,N,P,T> net, M inputMarking, T transition, M outputMarking) {
		AbstractStep<F,N,P,T,M> step = new AbstractStep<F,N,P,T,M>(net, inputMarking, transition, outputMarking);	
		return (IStep<F,N,P,T,M>)step;
	}
	
	protected IStep<F,N,P,T,M> createStep(IPetriNet<F,N,P,T> net, M inputMarking, T transition) {
		AbstractStep<F,N,P,T,M> step = new AbstractStep<F,N,P,T,M>(net, inputMarking, transition);	
		return (IStep<F,N,P,T,M>)step;
	}
	
	
	@Override
	public boolean addAll(int arg0, Collection<? extends IStep<F,N,P,T,M>> arg1) {
		throw new UnsupportedOperationException("Cannot modify runs by adding steps at arbitrary position.");
	}
	
	@Override
	public void add(int arg0, IStep<F,N,P,T,M> arg1) {
		throw new UnsupportedOperationException("Cannot modify runs by adding steps at arbitrary position.");
	}
	
	@Override
	public IStep<F,N,P,T,M> remove(int arg0) {
		if (arg0==this.size()-1) {
			IStep<F,N,P,T,M> result = super.remove(arg0);
			this.currentMarking = this.get(this.size()-1).getOutputMarking();
			this.possibleExtensions = new HashSet<T>(this.sys.getEnabledTransitionsAtMarking(this.currentMarking));
			return result;
		}
		else
			throw new UnsupportedOperationException("Cannot remove steps other than from the end of the run.");
	}

	@Override
	public boolean remove(Object arg0) {
		throw new UnsupportedOperationException("Cannot remove steps from runs.");
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		throw new UnsupportedOperationException("Cannot remove steps from runs.");
	}

	@Override
	protected void removeRange(int arg0, int arg1) {
		throw new UnsupportedOperationException("Cannot remove steps from runs.");
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		throw new UnsupportedOperationException("Cannot modify runs at arbitrary positions.");
	}

	@Override
	public IStep<F,N,P,T,M> set(int arg0, IStep<F,N,P,T,M> arg1) {
		throw new UnsupportedOperationException("Cannot modify runs at arbitrary positions.");
	}
}
