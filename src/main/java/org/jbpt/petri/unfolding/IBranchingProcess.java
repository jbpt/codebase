package org.jbpt.petri.unfolding;

import java.util.Collection;
import java.util.Set;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;

/**
 * Interface to a branching process of a net system.
 * 
 * @author Artem Polyvyanyy
 */
public interface IBranchingProcess<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>>
		extends IOrderingRelationsDescriptor<BPN,N>, Cloneable
{
	/**
	 * Get conditions of this branching process.
	 * 
	 * @return Conditions of this branching process.
	 */
	public Set<C> getConditions();

	/**
	 * Get conditions of this branching process that correspond to a given place in the originative net system.
	 * 
	 * @return Conditions of this branching process that correspond to the given place.
	 */
	public Set<C> getConditions(P place);

	/**
	 * Get events of this branching process.
	 * 
	 * @return Events of this branching process.
	 */
	public Set<E> getEvents();

	/**
	 * Get events of this branching process that correspond to a given transition in the originative net system.
	 * 
	 * @return Events of this branching process that correspond to the given transition.
	 */
	public Set<E> getEvents(T transition);

	/**
	 * Get originative net system of this branching process.
	 * 
	 * @return The originative net system of this branching process. 
	 */
	public INetSystem<F,N,P,T,M> getOriginativeNetSystem();
	
	/**
	 * Factory method to provide a condition of the branching process implementation.
	 * 
	 * @param place A place.
	 * @param event An event.
	 * @return A fresh condition.
	 */
	public C createCondition(P place, E event);
	
	/**
	 * Factory method to provide an event of the branching process implementation.
	 * 
	 * @param transition A transition.
	 * @param preset The preset of 't'.
	 * @return A fresh event.
	 */
	public E createEvent(T transition, ICoSet<BPN,C,E,F,N,P,T,M> preset);
	
	/**
	 * Factory method to provide a co-set of the branching process implementation.
	 * 
	 * @return A fresh co-set.
	 */
	public ICoSet<BPN,C,E,F,N,P,T,M> createCoSet();
	
	/**
	 * Factory method to provide a cut of the branching process implementation.
	 * 
	 * @return A fresh cut.
	 */
	public ICut<BPN,C,E,F,N,P,T,M> createCut();
	
	/**
	 * Get initial cut of this branching process, i.e., the set of condition without input events.
	 * 
	 * @return Initial cut of this branching process. 
	 */
	public ICut<BPN,C,E,F,N,P,T,M> getInitialCut();	
	
	/**
	 * Check if this branching process is conflict free. 
	 * Note that a branching process contains no backward conflicts by definition. Hence, the absense of forward conflicts is checked.
	 * 
	 * @return <tt>true</tt> if this branching process is conflict free; otherwise <tt></tt>.
	 */
	public boolean isConflictFree();
	
	/**
	 * Get causal predecessors of a given node of this branching process.
	 * 
	 * @param node Node of this branching process.
	 * @return Set of all causal predecessors of the given node.
	 */
	public Set<BPN> getCausalPredecessors(BPN node);
	
	/**
	 * Set net system of this branching process. 
	 * Once the new net system is set, the old branching process is cleared and the initial branching process for the new net system is constructued. 
	 * 
	 * @param system Net system to use as the originative system of this branching process.
	 */
	public void setNetSystem(INetSystem<F,N,P,T,M> system);
	
	/**
	 * Check if this branching process is safe.
	 * 
	 * @return <tt>true</tt> if this branching process is safe; <tt>false</tt> otherwise.
	 */
	public boolean isSafe();
	
	/**
	 * Get minimum, i.e., conditions without input events, of this branching process.  
	 * Note that minimum of a branching process is a cut (maximal co-set of conditions)!
	 * 
	 * @return Set of conditions without input events. 
	 */
	public Set<C> getMin();
	
	/**
	 * Get maximum, i.e., conditions without output events, of this branching process. 
	 * Note that maximum of a conflict-free (see {@code IBranchingProcess.isConflictFree()}) branching process is a cut (maximal co-set of conditions)!
	 * 
	 * @return Set of conditions without input events. 
	 */
	public Set<C> getMax();
	
	/**
	 * Get places of the originative net system that are associated with the given conditions.
	 *  
	 * @param conditions Conditions of this branching process.
	 * @return Set of places associated with the given conditions.
	 */
	public Set<P> getPlaces(Collection<C> conditions);
	
	/**
	 * Get transitions of the originative net system that are associated with the given events.
	 *  
	 * @param events Events of this branching process.
	 * @return Set of transitions associated with the given events.
	 */
	public Set<T> getTransitions(Collection<E> events);

	/**
	 * Append condition to this branching process.
	 * 
	 * @param condition Condition to append.
	 * @return <tt>true</tt> if condition was appended.
	 */
	public boolean appendCondition(C condition);

	/**
	 * Append event to this branching process.
	 * 
	 * @param condition Event to append.
	 * @return <tt>true</tt> if event was appended.
	 */
	public boolean appendEvent(E event);

	/**
	 * Construct initial branching process (only if this branching process is empty).
	 */
	public void constructInitialBranchingProcess();
	
	/**
	 * Get occurrence net.
	 * 
	 * @return Occurrence net.
	 */
	public IOccurrenceNet<BPN,C,E,F,N,P,T,M> getOccurrenceNet();
	
	/**
	 * Check if a given collection of conditions is a cut in this branching process. 
	 * @param conditions Collection of conditions.
	 * @return <tt>true</tt> if the given collection of conditions is a cut of this branching process; otherwise <tt>false</tt>.
	 */
	public boolean isCut(Collection<C> conditions);
	
	public IBranchingProcess<BPN,C,E,F,N,P,T,M> clone();
}