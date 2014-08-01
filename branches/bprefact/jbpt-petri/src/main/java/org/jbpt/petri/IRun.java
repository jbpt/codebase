package org.jbpt.petri;

import java.util.List;
import java.util.Set;

/**
 * Interface to a run (occurrence sequence) of an ({@link INetSystem}).
 * 
 * @author Artem Polyvyanyy
 */
public interface IRun<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> 
		extends List<IStep<F,N,P,T,M>> {
	
	/**
	 * Get transitions that can fire and extend this run.
	 * 
	 * @return Set of possible extensions of this run.
	 */
	public Set<T> getPossibleExtensions();
	
	/**
	 * Append transition to this run.
	 * 
	 * @param transition Transition to append.
	 * @return <tt>true</tt> if transition was appended; otherwise <tt>false</tt>.
	 */
	public boolean append(T transition);
	
	/**
	 * Clone this run.
	 * 
	 * @return Clone of this run.
	 */
	public IRun<F,N,P,T,M> clone();
	
	/**
	 * Set net system of this run.  
	 * 
	 * @param system Net system to use as the originative system of this run.
	 */
	public void setNetSystem(INetSystem<F,N,P,T,M> system);
}
