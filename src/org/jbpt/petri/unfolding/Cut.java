package org.jbpt.petri.unfolding;

import java.util.Collection;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPetriNet;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;


/**
 * Cut - maximal set of mutually concurrent conditions
 * 
 * @author Artem Polyvyanyy
 */
public class Cut extends Coset {
	private static final long serialVersionUID = 1L;
	
	public Cut(IPetriNet<IFlow<INode>, INode, IPlace, ITransition> net) {
		super(net);
	}
	
	public Cut(IPetriNet<IFlow<INode>, INode, IPlace, ITransition> net, Collection<Condition> cut) {
		super(net);
		this.addAll(cut);
	}
}
