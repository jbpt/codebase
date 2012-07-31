package org.jbpt.petri.wft;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jbpt.algo.tree.rpst.IRPSTNode;
import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.petri.IFlow;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPetriNet;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;


/**
 * This class takes a net and computes its WF-tree.<br/><br/>
 *
 * WF-tree was proposed in:
 * Matthias Weidlich, Artem Polyvyanyy, Jan Mendling, and Mathias Weske.
 * Causal Behavioural Profiles - Efficient Computation, Applications, and Evaluation. 
 * Fundamenta Informaticae (FUIN) 113(3-4): 399-435 (2011)
 * 
 * @author Artem Polyvyanyy
 * @author Matthias Weidlich
 */
public class WFTree<F extends IFlow<N>, N extends INode,
					P extends IPlace, T extends ITransition> extends RPST<F,N> implements IWFTree<F,N,P,T> {

	private Map<IRPSTNode<F,N>,WFTreeBondType> bond2type = null;
	private Map<IRPSTNode<F,N>,WFTreeLoopOrientationType> loop2type = null;
	
	public WFTree(IPetriNet<F,N,P,T> net) {
		super(net);
		
		this.bond2type = new HashMap<IRPSTNode<F,N>,WFTreeBondType>();
		this.loop2type = new HashMap<IRPSTNode<F,N>,WFTreeLoopOrientationType>();
	}
	
	@Override
	public WFTreeBondType getRefinedBondType(IRPSTNode<F,N> node) {
		if (node.getType()!=TCType.BOND)
			return WFTreeBondType.UNDEFINED;
		else {
			WFTreeBondType type = this.bond2type.get(node);
			if (type!=null) return type;
			else {
				INode entry = node.getEntry();
				INode exit = node.getExit();
				
				if (entry==null || exit == null)
					return WFTreeBondType.UNDEFINED;
				
				for (IRPSTNode<F,N> child : this.getChildren(node)) {
					if (child.getEntry().equals(node.getExit())) {
						type = WFTreeBondType.LOOP;
						this.bond2type.put(node,type);
						return type;
					}
				}
				
				if (entry instanceof ITransition && exit instanceof ITransition) {
					type = WFTreeBondType.TRANSITION_BORDERED;
					this.bond2type.put(node,type);
					return type;
				}
				
				if (entry instanceof IPlace && exit instanceof IPlace) {
					type = WFTreeBondType.PLACE_BORDERED;
					this.bond2type.put(node,type);
					return type;
				}
				
				return WFTreeBondType.UNDEFINED;
			}
		}
	}
	
	@Override
	public WFTreeLoopOrientationType getLoopOrientationType(IRPSTNode<F,N> node) {
		if (this.isRoot(node)) return WFTreeLoopOrientationType.UNDEFINED;
		
		WFTreeLoopOrientationType type = this.loop2type.get(node);
		if (type!=null) return type;
		
		if (this.getRefinedBondType(this.getParent(node))!=WFTreeBondType.LOOP)
			return WFTreeLoopOrientationType.UNDEFINED;
		
		if (node.getEntry().equals(this.getParent(node).getEntry())) {
			type = WFTreeLoopOrientationType.FORWARD;
			this.loop2type.put(node,type);
			return type;
		}
		
		if (node.getEntry().equals(this.getParent(node).getExit())) {
			type = WFTreeLoopOrientationType.BACKWARD;
			this.loop2type.put(node,type);
			return type;
		}	
		
		return WFTreeLoopOrientationType.UNDEFINED;
	}
	
	@Override
	public Set<IRPSTNode<F,N>> getRPSTNodes(WFTreeBondType type) {
		Set<IRPSTNode<F,N>> result = new HashSet<IRPSTNode<F,N>>();
		for (IRPSTNode<F,N> node : this.getVertices())
			if (this.getRefinedBondType(node)==type)
				result.add(node);
		
		return result;
	}
}
