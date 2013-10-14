package org.jbpt.petri.untangling.pss;

import java.util.ArrayList;
import java.util.Collection;
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
import org.jbpt.petri.unfolding.IEvent;
import org.jbpt.petri.untangling.IProcess;

/**
 * An abstract implementation of the {@link IProcessSetSystem} interface.
 * 
 * @author Artem Polyvyanyy
 */
public abstract class AbstractProcessSetSystem<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, 
												F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> 
		implements IProcessSetSystem<BPN,C,E,F,N,P,T,M>
{
	private INetSystem<F,N,P,T,M> system = null;
	private Collection<IProcessSystem<BPN,C,E,F,N,P,T,M>> systems = null;
	
	@SuppressWarnings("unchecked")
	public AbstractProcessSetSystem(INetSystem<F,N,P,T,M> sys, Collection<IProcess<BPN,C,E,F,N,P,T,M>> pis) {
		if (sys==null) return;
		if (pis==null) return;
		
		this.system = sys;
		
		this.systems = new ArrayList<IProcessSystem<BPN, C, E, F, N, P, T, M>>();
		for (IProcess<BPN,C,E,F,N,P,T,M> pi : pis) {
			IProcessSystem<BPN,C,E,F,N,P,T,M> pSystem = null;
			try {
				pSystem = (IProcessSystem<BPN,C,E,F,N,P,T,M>) ProcessSystem.class.newInstance();
			} catch (InstantiationException e) {
				continue;
			} catch (IllegalAccessException e) {
				continue;
			}
			pSystem.setSystem(sys);
			pSystem.setProcess(pi);
			
			this.systems.add(pSystem);
		}
	}

	@Override
	public Collection<IProcessSystem<BPN,C,E,F,N,P,T,M>> getProcessSystems() {
		return this.systems;
	}
	
	@Override
	public Set<T> getEnabledTransitions() {
		Set<T> result = new HashSet<T>();
		
		for (IProcessSystem<BPN,C,E,F,N,P,T,M> pSystem : this.systems)
			result.addAll(pSystem.getEnabledTransitions());
		
		return result;
	}

	@Override
	public M getMarking() {
		return this.system.getMarking();
	}

	@Override
	public boolean fire(T transition) {
		Collection<IProcessSystem<BPN,C,E,F,N,P,T,M>> toRemove = new ArrayList<IProcessSystem<BPN, C, E, F, N, P, T, M>>();
		
		boolean flag = false;
		for (IProcessSystem<BPN,C,E,F,N,P,T,M> sys : this.systems) {
			if (sys.getEnabledTransitions().contains(transition)) {
				flag = true;
			}
			else
				toRemove.add(sys);
		}
		
		if (!flag) return false;
		
		this.systems.removeAll(toRemove);
		
		return this.system.fire(transition);
	}

	@Override
	public INetSystem<F, N, P, T, M> getNetSystem() {
		return this.system;
	}
}
