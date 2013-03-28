package org.jbpt.petri.untangling;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPetriNet;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;
import org.jbpt.petri.unfolding.IBPNode;
import org.jbpt.petri.unfolding.IBranchingProcess;
import org.jbpt.petri.unfolding.ICondition;
import org.jbpt.petri.unfolding.IEvent;

/**
 * An interface to a Process of a net system ({@link INetSystem}). Extends {@link IBranchingProcess}. 
 * A process of a net system is composed of a conflict-free Petri net ({@link IPetriNet}) and a mapping from nodes of the net to nodes of the net system 
 * that allows interpreting the net as a concurrent trace of the net system.
 * 
 * @author Artem Polyvyanyy
 */
public interface IProcess<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> 
		extends IBranchingProcess<BPN,C,E,F,N,P,T,M>, Cloneable {
}
