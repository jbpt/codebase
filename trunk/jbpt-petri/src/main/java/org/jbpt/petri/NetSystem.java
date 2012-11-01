package org.jbpt.petri;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;


/**
 * Implementation of a net system.
 * 
 * TODO lift to interfaces
 * 
 * @author Artem Polyvyanyy
 */
public class NetSystem extends AbstractNetSystem<Flow,Node,Place,Transition,Marking> {

	public NetSystem() {
		super();
		
		try {
			this.marking = Marking.class.newInstance();
			this.marking.setPetriNet(this);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public NetSystem(PetriNet petriNet) {
		this();
		for (Node n : petriNet.getNodes())
			this.addNode(n);
		for (Flow f : petriNet.getFlow())
			this.addFlow(f.getSource(), f.getTarget());
	}
	
	@Override
	public Flow addFlow(Node from, Node to) {
		if (from == null || to == null) return null;
		
		if ((from instanceof Place && to instanceof Transition) ||
				from instanceof Transition && to instanceof Place) {
			
			Collection<Node> ss = new ArrayList<Node>(); ss.add(from);
			Collection<Node> ts = new ArrayList<Node>(); ts.add(to);
			
			if (!this.checkEdge(ss,ts)) return null;
			
			return new Flow(this, from, to);
		}
		
		return null;
	}

	@Override
	public Set<Node> getSourceNodes() {
		return PetriNet.DIRECTED_GRAPH_ALGORITHMS.getSources(this);
	}
	
	@Override
	public Set<Node> getSinkNodes() {
		return PetriNet.DIRECTED_GRAPH_ALGORITHMS.getSinks(this);
	}
}
