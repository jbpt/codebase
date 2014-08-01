package org.jbpt.petri.untangling.pss;

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
 * An interface to a Process System. 
 * A process system is composed of a net system ({@link INetSystem}) S  and a process ({@link IProcess}) of S. 
 * A process system restricts the behavior of its net system to that encoded in its process. 
 * 
 * @author Artem Polyvyanyy
 */
public interface IProcessSystem<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, 
									F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>>
{
	/**
	 * Get enabled transitions of this process system.
	 * 
	 * @return Set of enabled transitions.
	 */
	public Set<T> getEnabledTransitions();
	
	/**
	 * Get marking of this process system.
	 * 
	 * @return Marking of this net system 
	 */
	public M getMarking();
	
	/**
	 * Fire a given transition, i.e., trigger an occurrence of a given transition.
	 * @param transition A transition to fire.
	 * 
	 * @return <tt>true</tt> if transition fired; <tt>false</tt> otherwise.
	 */
	public boolean fire(T transition);
	
	/**
	 * Get process.
	 * 
	 * @return Process of this process system.
	 */
	public IProcess<BPN,C,E,F,N,P,T,M> getProcess();
	
	/**
	 * Get net system.
	 * 
	 * @return Net system of this process system.
	 */
	public INetSystem<F,N,P,T,M> getNetSystem();
	
	/**
	 * Set net system.
	 * 
	 * @param sys A net system to use for this process system. 
	 */
	public void setSystem(INetSystem<F,N,P,T,M> sys);
	
	/**
	 * Set process.
	 * 
	 * @param pi A process to use for this process system. 
	 */
	public void setProcess(IProcess<BPN,C,E,F,N,P,T,M> pi);
}
