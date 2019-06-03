package org.jbpt.petri.mc;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;

/**
 * An interface to a model checker.
 * 
 * @author Artem Polyvyanyy
 */
public interface IModelChecker<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> {
	
	/**
	 * Check if a transition in a given net system is live.
	 * 
	 * @param sys A net system.
	 * @param t A transition.
	 * 
	 * @return <code>true</code> if transition <code>t</code> is live in net system <code>sys</code>; <code>false</code> otherwise.
	 */
	public boolean isLive(INetSystem<F,N,P,T,M> sys, T t);

	/**
	 * Check if a given net system is live.
	 * 
	 * @param sys A net system.
	 * 
	 * @return <code>true</code> if net system <code>sys</code> is live; <code>false</code> otherwise.
	 */
	public boolean isLive(INetSystem<F,N,P,T,M> sys);

	/**
	 * Check if a marking is reachable in a given net system. 
	 * The marking is specified as a collection of places, where the multiplicity of a place in the collection denotes the number of tokens at that place. 
	 * 
	 * @param sys A net system.
	 * @param marking A marking (specified as a collection of places).
	 * 
	 * @return <code>true</code> if <code>marking</code> is reachable in net system <code>sys</code>; <code>false</code> otherwise.
	 */
	public boolean isReachable(INetSystem<F,N,P,T,M> sys, Collection<P> marking);

	/**
	 * Check if a place in a given net system is bounded.
	 * 
	 * @param sys A net system.
	 * @param p A place.
	 * 
	 * @return <code>true</code> if place <code>p</code> is bounded in net system <code>sys</code>; <code>false</code> otherwise.
	 */
	public boolean isBounded(INetSystem<F,N,P,T,M> sys, P p);
	
	/**
	 * Check if a given net system is bounded.
	 * 
	 * @param sys A net system.
	 * 
	 * @return <code>true</code> if net system <code>sys</code> is bounded; <code>false</code> otherwise.
	 */
	public boolean isBounded(INetSystem<F,N,P,T,M> sys);

	/**
	 * Check if a given net system is a sound workflow net.
	 * 
	 * @param sys A net system.
	 * 
	 * @return <code>true</code> if net system <code>sys</code> is a sound workflow net; <code>false</code> otherwise.
	 */
	public boolean isSoundWorkflowNet(INetSystem<F,N,P,T,M> sys);
	
	/**
	 * Check if a marking that put at least one token at each of the given places is reachable
	 * 
	 * @param sys A net system.
	 * @param places A set of places.
	 * 
	 * @return <code>true</code> if a marking that puts at least one token at each of the places in set <code>places</code> is reachable; <code>false</code> otherwise.
	 */
	public boolean canReachMarkingWithAtLeastOneTokenAtEachPlace(INetSystem<F,N,P,T,M> sys, Set<P> places);
	
	/**
	 * Compute statistics for the state space (a.k.a reachability graph) of a given net system.
	 *   
	 * @param sys A net system.
	 * 
	 * @return {@link StateSpaceStatistics} object that contains information on the number of reachable states and state transitions of the reachability graph of net system <code>sys</code>. 
	 */
	public StateSpaceStatistics getStateSpaceStatistics(INetSystem<F,N,P,T,M> sys);
	
	/**
	 * Check a property in temporal logic (LTL or CTL).
	 * 
	 * @param sys A net system.
	 * @param property An LTL or CTL property in the LoLA 2.0 format.
	 * 
	 * @return <code>true</code> if <code>property</code> holds for net system <code>sys</code>; <code>false</code> otherwise.
	 */
	public boolean check(INetSystem<F,N,P,T,M> sys, String property);
	
	
	// TODO: The below methods are querying specific and must be removed from this interface. 
	
	public boolean canReachMarkingWithAtLeastOneTokenAtEachPlace(INetSystem<F,N,P,T,M> sys, Set<P> places, Set<Process> p); //A.P.
	
	public boolean isReachable(INetSystem<F,N,P,T,M> sys, Collection<P> marking, Set<Process> p); //A.P.
	
	public boolean isIndexable(INetSystem<F,N,P,T,M> sys);
	
	public boolean isIndexable(INetSystem<F,N,P,T,M> sys, Set<Process> p); //A.P.
	
	public void setLoLAActive(boolean active); //A.P.
	
	public AtomicBoolean isLoLAActive(); //A.P.
	
}
