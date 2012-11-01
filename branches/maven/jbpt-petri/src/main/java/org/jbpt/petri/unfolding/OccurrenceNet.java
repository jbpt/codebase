package org.jbpt.petri.unfolding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.jbpt.petri.Flow;
import org.jbpt.petri.Marking;
import org.jbpt.petri.Node;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;

public class OccurrenceNet extends
		AbstractOccurrenceNet<BPNode, Condition, Event, Flow, Node, Place, Transition, Marking> {

	protected OccurrenceNet() {
		super();
	}

	protected OccurrenceNet(ICompletePrefixUnfolding<BPNode, Condition, Event, Flow, Node, Place, Transition, Marking> unf) {
		super(unf);
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
