package org.jbpt.petri.untangling.pss;

import java.util.Collection;
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
 * An interface to a Process Set System. 
 * A process set system is composed of a net system ({@link INetSystem}) S  and a set of processes ({@link IProcess}) of S. 
 * A process set system restricts the behavior of its net system to that encoded in its processes. 
 * 
 * @author Artem Polyvyanyy
 */
public interface IProcessSetSystem<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>>
{
	/**
	 * Get a set of enabled transitions.
	 * 
	 * @return A set of enabled transitions.
	 */
	public Set<T> getEnabledTransitions();
	
	/**
	 * Get marking.
	 * 
	 * @return Get marking of this process set system.
	 */
	public M getMarking();
	
	public boolean fire(T transition);
	
	public Collection<IProcessSystem<BPN,C,E,F,N,P,T,M>> getProcessSystems();
	
	public INetSystem<F,N,P,T,M> getNetSystem();
}
