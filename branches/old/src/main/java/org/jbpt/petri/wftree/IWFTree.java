package org.jbpt.petri.wftree;

import java.util.Set;

import org.jbpt.algo.tree.rpst.IRPST;
import org.jbpt.algo.tree.rpst.IRPSTNode;
import org.jbpt.petri.IFlow;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;

/**
 * Interface to a WF-tree.
 * 
 * WF-tree was proposed in:
 * Matthias Weidlich, Artem Polyvyanyy, Jan Mendling, and Mathias Weske.
 * Causal Behavioural Profiles - Efficient Computation, Applications, and Evaluation. 
 * Fundamenta Informaticae (FUIN) 113(3-4): 399-435 (2011)
 *
 * @param <F> Flow template.
 * @param <N> Node template.
 * @param <P> Place template.
 * @param <T> Transition template.
 *
 * @author Artem Polyvyanyy
 */
public interface IWFTree<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition> extends IRPST<F,N> {

	/**
	 * Get refined type of a WF-tree bond node.<br/><br/>
	 * 
	 * WFTreeBondType.TRANSITION_BORDERED if entry and exit are transitions.<br/> 
	 * WFTreeBondType.PLACE_BORDERED if entry and exit are places.<br/>
	 * WFTreeBondType.LOOP if entry and exit are places and entry of some child is an exit of another child.<br/> 
	 * WFTreeBondType.UNDEFINED none of the above.<br/>
	 * 
	 * @param node Node of this WF-tree.
	 * @return Refined type of this WF-tree bond node; WFTreeBondType.UNDEFINED if the type cannot be determined.
	 */
	public WFTreeBondType getRefinedBondType(IRPSTNode<F,N> node);

	/**
	 * Get loop orientation type of a WF-tree node.<br/><br/>
	 * Loop orientation type is defined if parent of the given node is of type loop.<br/><br/>
	 * 
	 * WFTreeLoopOrientationType.FORWAD if entry of the given node is equal to the entry of the parent loop node.<br/>
	 * WFTreeLoopOrientationType.BACKWARD if if entry of the given node is equal to the exit of the parent loop node.<br/>
	 * 
	 * @param node Node of this WF-tree.
	 * @return Loop orientation type of the given node; WFTreeLoopOrientationType.UNDEFINED if the type cannot be determined.
	 */
	public WFTreeLoopOrientationType getLoopOrientationType(IRPSTNode<F,N> node);

	/**
	 * Get RPST nodes of a given {@link WFTreeBondType} type.
	 * 
	 * @param type WF-tree bond type.
	 * @return Set of RPST nodes of the given {@link WFTreeBondType} type.
	 */
	public Set<IRPSTNode<F,N>> getRPSTNodes(WFTreeBondType type);
}