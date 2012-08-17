package org.jbpt.petri.unfolding;

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
public interface IBranchingProcess<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> {
	
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
	 * Check if two nodes of this branching process are in the causal relation.
	 * 
	 * @param n1 Node of this branching process.
	 * @param n2 Node of this branching process.
	 * @return <tt>true</tt> if 'n1' and 'n2' are in the causal relation; otherwise <tt>false</tt>.
	 */
	public boolean areCausal(BPN n1, BPN n2);

	/**
	 * Check if two nodes of this branching process are in the inverse causal relation.
	 * 
	 * @param n1 Node of this branching process.
	 * @param n2 Node of this branching process.
	 * @return <tt>true</tt> if 'n1' and 'n2' are in the inverse causal relation; otherwise <tt>false</tt>.
	 */
	public boolean areInverseCausal(BPN n1, BPN n2);

	/**
	 * Check if two nodes of this branching process are concurrent.
	 * 
	 * @param n1 Node of this branching process.
	 * @param n2 Node of this branching process.
	 * @return <tt>true</tt> if 'n1' and 'n2' are concurrent; otherwise <tt>false</tt>.
	 */
	public boolean areConcurrent(BPN n1, BPN n2);

	/**
	 * Check if two nodes of this branching process are in conflict.
	 * 
	 * @param n1 Node of this branching process.
	 * @param n2 Node of this branching process.
	 * @return <tt>true</tt> if 'n1' and 'n2' are in conflict; otherwise <tt>false</tt>.
	 */
	public boolean areInConflict(BPN n1, BPN n2);

	/**
	 * Get ordering relation between two nodes of this branching process.
	 * 
	 * @param n1 Node of this branching process.
	 * @param n2 Node of this branching process.
	 * @return Ordering relation between 'n1' and 'n2', see {@link OrderingRelationType}.
	 */
	public OrderingRelationType getOrderingRelation(BPN n1, BPN n2);
	
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
	
	public void setNetSystem(INetSystem<F,N,P,T,M> system);
}