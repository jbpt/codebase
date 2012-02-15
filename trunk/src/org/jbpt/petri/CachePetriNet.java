package org.jbpt.petri;

import java.util.Map;
import java.util.Set;

import org.jbpt.graph.algo.TransitiveClosure;



/**
 * Extends the standard Petri net model by some caching routines. When using
 * this class make sure to call <code>invalidateCache</code> whenever the 
 * structure or the marking of the net changes. For the methods
 * <code>addNode</code> and <code>addFlow</code> this is done automatically.
 * 
 * @author matthias.weidlich
 *
 */
public class CachePetriNet extends PetriNet {
	
	protected int isFreeChoice = -1;
	protected int isWFNet = -1;
	protected int isExtendedFreeChoice = -1;
	protected int hasCycle = -1;
	protected int isSNet = -1;
	protected int isTNet = -1;
	
	protected TransitiveClosure<Flow, Node> closure = null;
	
	protected Map<Node,Set<Node>> dominators;
	protected Map<Node,Set<Node>> postdominators;

	
	public CachePetriNet() {
		super();
	}
	
	@Override
	public Flow addFlow(Node from, Node to) {
		invalidateCache();
		return super.addFlow(from, to);
	}
	
	@Override
	public Node addNode(Node n) {
		invalidateCache();
		return super.addNode(n);
	}

	public void invalidateCache() {
		this.isExtendedFreeChoice = -1;
		this.isFreeChoice = -1;
		this.isWFNet = -1;
		this.hasCycle = -1;
		this.isSNet = -1;
		this.isTNet = -1;
		
		this.closure = null;
	}
	
	@Override
	public boolean isFreeChoice() {
		if (this.isFreeChoice == -1)
			this.isFreeChoice = (super.isFreeChoice()) ? 1 : 0;
		return this.isFreeChoice == 1;
	}
	
	@Override
	public boolean isExtendedFreeChoice() {
		if (this.isExtendedFreeChoice == -1)
			this.isExtendedFreeChoice = (super.isExtendedFreeChoice()) ? 1 : 0;
		return this.isExtendedFreeChoice == 1;
	}
	
	@Override
	public boolean isWFNet() {
		if (this.isWFNet == -1)
			this.isWFNet = (super.isWFNet()) ? 1 : 0;
		return this.isWFNet == 1;
	}

	@Override
	public boolean hasCycle() {
		if (this.hasCycle == -1)
			this.hasCycle = (super.hasCycle()) ? 1 : 0;
		return this.hasCycle == 1;
	}

	@Override
	public boolean isSNet() {
		if (this.isSNet == -1)
			this.isSNet = (super.isSNet()) ? 1 : 0;
		return this.isSNet == 1;
	}

	@Override
	public boolean isTNet() {
		if (this.isTNet == -1)
			this.isTNet = (super.isTNet()) ? 1 : 0;
		return this.isTNet == 1;
	}
	
	@Override
	public boolean hasPath(Node from, Node to) {
		if (this.closure == null)
			this.closure = new TransitiveClosure<Flow, Node>(this);

		return this.closure.hasPath(from, to);
	}
	
	@Override
	public Map<Node, Set<Node>> getDominators() {
		if (this.dominators == null)
			this.dominators = super.getDominators();
		return this.dominators;
	}
	
	@Override
	public Map<Node, Set<Node>> getPostDominators() {
		if (this.postdominators == null)
			this.postdominators = super.getPostDominators();
		return this.postdominators;
	}
		
}
