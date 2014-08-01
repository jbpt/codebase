package org.jbpt.petri.untangling;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;
import org.jbpt.petri.unfolding.AbstractBranchingProcess;
import org.jbpt.petri.unfolding.IBPNode;
import org.jbpt.petri.unfolding.ICondition;
import org.jbpt.petri.unfolding.IEvent;

/**
 * An abstract implementation of the {@link IProcess} interface.
 * 
 * @author Artem Polyvyanyy
 */
public abstract class AbstractProcess<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> 
		extends AbstractBranchingProcess<BPN,C,E,F,N,P,T,M> 
		implements IProcess<BPN,C,E,F,N,P,T,M>
{

	protected AbstractProcess() {
		super();
	}

	/**
	 * Construct a process of a given net system.
	 *  
	 * @param sys A net system.
	 */
	public AbstractProcess(INetSystem<F,N,P,T,M> sys) {
		super(sys);
	}

	/**
	 * Append event to this process (ensures that no conflicts are introduced).
	 * 
	 * @param event An event to append.
	 * @return <tt>true</tt> if 'event' was appended; <tt>false</tt> otherwise.
	 */
	@Override
	public boolean appendEvent(E event) {
		for (E e : this.getEvents()) {
			for (C c: event.getPreConditions())
				if (e.getPreConditions().contains(c))
					return false;
		}
		
		return super.appendEvent(event);
	}

	/**
	 * Check if this process is conflict free.
	 * 
	 *  @return <tt>true</tt> if this process is conflict-free; <tt>false</tt> otherwise.
	 */
	@Override
	public boolean isConflictFree() {
		return true;
	} 
}
