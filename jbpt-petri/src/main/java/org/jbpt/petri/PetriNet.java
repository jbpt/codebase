package org.jbpt.petri;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.jbpt.algo.graph.DirectedGraphAlgorithms;
import org.jbpt.petri.structure.PetriNetStructuralChecks;
import org.jbpt.petri.structure.PetriNetTransformations;


/**
 * An implementation of IPetriNet interface.
 * 
 * TODO create and extend a bipartite graph.
 * 
 * @author Artem Polyvyanyy
 * @author Matthias Weidlich
 * @author Andreas Meyer
 */
public class PetriNet extends AbstractPetriNet<Flow,Node,Place,Transition> {
	
	public static DirectedGraphAlgorithms<Flow,Node>					DIRECTED_GRAPH_ALGORITHMS = new DirectedGraphAlgorithms<Flow,Node>();
	public static PetriNetStructuralChecks<Flow,Node,Place,Transition>	STRUCTURAL_CHECKS = new PetriNetStructuralChecks<Flow,Node,Place,Transition>();
	public static PetriNetTransformations<Flow,Node,Place,Transition>	TRANSFORMATIONS = new PetriNetTransformations<Flow,Node,Place,Transition>();
	
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
