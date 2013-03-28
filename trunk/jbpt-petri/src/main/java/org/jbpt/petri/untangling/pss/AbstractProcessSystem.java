package org.jbpt.petri.untangling.pss;

import java.util.HashSet;
import java.util.Set;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;
import org.jbpt.petri.unfolding.IBPNode;
import org.jbpt.petri.unfolding.ICondition;
import org.jbpt.petri.unfolding.ICut;
import org.jbpt.petri.unfolding.IEvent;
import org.jbpt.petri.untangling.IProcess;

/**
 * An abstract implementation of the {@link IProcessSystem} interface.
 * 
 * @author Artem Polyvyanyy
 */
public abstract class AbstractProcessSystem<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, 
											F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>>
											implements IProcessSystem<BPN,C,E,F,N,P,T,M>
{
	private INetSystem<F,N,P,T,M> system = null;
	private IProcess<BPN,C,E,F,N,P,T,M> process = null;
	
	protected AbstractProcessSystem() {};
			
	public AbstractProcessSystem(INetSystem<F,N,P,T,M> sys, IProcess<BPN,C,E,F,N,P,T,M> pi) {
		if (sys==null) return;
		if (pi==null) return;
		
		this.system = sys;
		this.process = pi;
	}	
	
	@Override
	public Set<T> getEnabledTransitions() {
		Set<T> result = new HashSet<T>();
		
		// get cuts that refer to the current marking
		Set<ICut<BPN,C,E,F,N,P,T,M>> cuts = this.process.getCuts(this.system.getMarking().toMultiSet());
		
		for (E e : this.process.getEvents()) {
			for (ICut<BPN,C,E,F,N,P,T,M> cut : cuts) {
				if (cut.containsAll(e.getPreConditions())) {
					result.add(e.getTransition());
				}
			}
		}
		
		return result;
	}
	
	@Override
	public boolean fire(T transition) {
		if (this.getEnabledTransitions().contains(transition))
			return this.system.fire(transition);
		else
			return false;
	}

	@Override
	public M getMarking() {
		return this.system.getMarking();
	}
	
	@Override
	public IProcess<BPN,C,E,F,N,P,T,M> getProcess() {
		return this.process;
	}
	
	@Override
	public INetSystem<F,N,P,T,M> getNetSystem() {
		return this.system;
	}

	@Override
	public void setSystem(INetSystem<F,N,P,T,M> sys) {
		if (sys==null) return;
		this.system = sys;
	}
	
	@Override
	public void setProcess(IProcess<BPN,C,E,F,N,P,T,M> pi) {
		if (pi==null) return;
		this.process = pi;
	}
}
