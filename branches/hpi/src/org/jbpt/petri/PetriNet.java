package org.jbpt.petri;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jbpt.algo.graph.DirectedGraphAlgorithms;
import org.jbpt.graph.abs.AbstractDirectedGraph;
import org.jbpt.petri.structure.PetriNetStructuralChecks;
import org.jbpt.petri.structure.PetriNetTransformations;

/**
 * An implementation of IPetriNet interface. 
 * 
 * @author Artem Polyvyanyy
 * @author Matthias Weidlich
 * @author Andreas Meyer
 */
public class PetriNet extends AbstractDirectedGraph<Flow,Node> implements IPetriNet<Flow,Node,Place,Transition> {
	
	/**
	 * Singleton for performing directed graph algorithms on PetriNet objects.
	 */
	public static DirectedGraphAlgorithms<Flow,Node> DIRECTED_GRAPH_ALGORITHMS = new DirectedGraphAlgorithms<Flow,Node>();
	
	/**
	 * Singleton for performing structural class checks on PetriNet objects.
	 */
	public static PetriNetStructuralChecks<Flow,Node,Place,Transition> STRUCTURAL_CHECKS = new PetriNetStructuralChecks<Flow,Node,Place,Transition>();
	
	/**
	 * Singleton for performing transformations of PetriNet objects.
	 */
	public static PetriNetTransformations<Flow,Node,Place,Transition> TRANSFORMATIONS = new PetriNetTransformations<Flow,Node,Place,Transition>();
	
	/**
	 * Empty constructor.
	 */
	public PetriNet(){}
	
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
	public Flow addFlow(Place place, Transition transition) {
		return this.addFlow((Node)place,(Node)transition);
	}
	
	@Override
	public Flow addFlow(Transition transition, Place place) {
		return this.addFlow((Node)transition,(Node)place);
	}
	
	@Override
	public Node addNode(Node node) {
		return this.addVertex(node);
	}
	
	@Override
	public Collection<Node> addNodes(Collection<Node> nodes) {
		Collection<Node> result = this.addVertices(nodes);
		return result==null ? new ArrayList<Node>() : result;
	}

	@Override
	public Place addPlace(Place place) {
		return this.addVertex(place)==null ? null : place;
	}
	
	@Override
	public Collection<Place> addPlaces(Collection<Place> places) {
		Collection<Place> result = new ArrayList<Place>();
		if (places == null) return result;
		
		for (Place place : places)
			if (this.addVertex(place) != null)
				result.add(place);
		
		return result;
	}
	
	@Override
	public Transition addTransition(Transition transition) {
		return this.addVertex(transition)==null ? null : transition;
	}
	
	@Override
	public Collection<Transition> addTransitions(Collection<Transition> transitions) {
		Collection<Transition> result = new ArrayList<Transition>();
		if (transitions == null) return result;
		
		for (Transition transition : transitions)
			if (this.addVertex(transition) != null)
				result.add(transition);
		
		return result;
	}
	
	@Override
	public Node removeNode(Node node) {
		return this.removeVertex(node);
	}
	
	@Override
	public Collection<Node> removeNodes(Collection<Node> nodes) {
		Collection<Node> result = this.removeVertices(nodes);
		return result==null ? new ArrayList<Node>() : result;
	}
	
	@Override
	public Place removePlace(Place place) {
		return this.removeVertex(place) == null ? null : place;
	}
	
	@Override
	public Collection<Place> removePlaces(Collection<Place> places) {
		Collection<Place> result = new ArrayList<Place>();
		if (places == null) return result;
		
		for (Place place : places)
			if (this.removeVertex(place) != null)
				result.add(place);
		
		return result;
	}
	
	@Override
	public Transition removeTransition(Transition transition) {
		return this.removeVertex(transition) == null ? null : transition;
	}
	
	@Override
	public Collection<Transition> removeTransitions(Collection<Transition> transitions) {
		Collection<Transition> result = new ArrayList<Transition>();
		if (transitions == null) return result;
		
		for (Transition transition : transitions)
			if (this.removeVertex(transition) != null)
				result.add(transition);
		
		return result;
	}
	
	@Override
	public Flow removeFlow(Flow flow) {
		return this.removeEdge(flow);
	}
	
	@Override
	public Collection<Flow> removeFlow(Collection<Flow> flow) {
		Collection<Flow> result = this.removeEdges(flow);
		return result==null ? new ArrayList<Flow>() : result;
	}
	
	
	@Override
	public Set<Node> getNodes() {
		// TODO this.getVertices() must return set.
		return new HashSet<Node>(this.getVertices());
	}
	
	@Override
	public Set<Place> getPlaces() {
		Set<Place> result = new HashSet<Place>();
		
		for (Node node : this.getVertices())
			if (node instanceof Place)
				result.add((Place)node);
		
		return result;
	}

	@Override
	public Set<Transition> getTransitions() {
		Set<Transition> result = new HashSet<Transition>();
		
		for (Node node : this.getVertices())
			if (node instanceof Transition)
				result.add((Transition)node);
		
		return result;
	}
	
	@Override
	public Set<Flow> getFlow() {
		// TODO this.getEdges() must return set.
		return new HashSet<Flow>(this.getEdges());
	}
	
	@Override
	public Set<Transition> getSilentTransitions() {
		Set<Transition> result = new HashSet<Transition>();
		
		for (Node node : this.getVertices())
			if (node instanceof Transition && node.getLabel().isEmpty())
				result.add((Transition)node);	
		
		return result;
	}

	@Override
	public Set<Transition> getObservableTransitions() {
		Set<Transition> result = new HashSet<Transition>();
		
		for (Node node : this.getVertices())
			if (node instanceof Transition && !node.getLabel().isEmpty())
				result.add((Transition)node);	
		
		return result;
	}
	
	@Override
	public Set<Place> getPostset(Transition transition) {
		Set<Place> result = new HashSet<Place>();
		for (Node node : this.getDirectSuccessors(transition))
			if (node instanceof Place)
				result.add((Place)node);
		
		return result;
	}
	
	@Override
	public Set<Place> getPostsetPlaces(Collection<Transition> transitions) {
		Set<Place> result = new HashSet<Place>();
		
		for (Transition transition : transitions)
			for (Node node : this.getDirectSuccessors(transition))
				if (node instanceof Place)
					result.add((Place)node);
		
		return result;
	}
	
	@Override
	public Set<Transition> getPostset(Place place) {
		Set<Transition> result = new HashSet<Transition>();
		
		for (Node node : this.getDirectSuccessors(place)) {
			if (node instanceof Transition)
				result.add((Transition)node);
		}
		
		return result;
	}
	
	@Override
	public Set<Transition> getPostsetTransitions(Collection<Place> places) {
		Set<Transition> result = new HashSet<Transition>();
		
		for (Place place : places)
			for (Node node : this.getDirectSuccessors(place))
				if (node instanceof Transition)
					result.add((Transition)node);
		
		return result;
	}

	@Override
	public Set<Node> getPostset(Node node) {
		// TODO this.getDirectSuccessors(node) must return set.
		return new HashSet<Node>(this.getDirectSuccessors(node));
	}
	
	@Override
	public Set<Node> getPostset(Collection<Node> nodes) {
		// TODO this.getDirectSuccessors(nodes) must return set.
		return new HashSet<Node>(this.getDirectSuccessors(nodes));
	}
	
	@Override
	public Set<Place> getPreset(Transition transition) {
		Set<Place> result = new HashSet<Place>();
		
		for (Node node : this.getDirectPredecessors(transition))
			if (node instanceof Place)
				result.add((Place)node);
		
		return result;
	}

	@Override
	public Set<Place> getPresetPlaces(Collection<Transition> transitions) {
		Set<Place> result = new HashSet<Place>();
		
		for (Transition transition : transitions)
			for (Node node : this.getDirectPredecessors(transition))
				if (node instanceof Place)
					result.add((Place)node);
		
		return result;
	}
	
	@Override
	public Set<Transition> getPreset(Place place) {
		Set<Transition> result = new HashSet<Transition>();
		
		for (Node node : this.getDirectPredecessors(place))
			if (node instanceof Transition)
				result.add((Transition)node);
		
		return result;
	}
	
	@Override
	public Set<Transition> getPresetTransitions(Collection<Place> places) {
		Set<Transition> result = new HashSet<Transition>();
		
		for (Place place : places)
			for (Node node : this.getDirectPredecessors(place))
				if (node instanceof Transition)
					result.add((Transition)node);
		
		return result;
	}
	
	@Override
	public Set<Node> getPreset(Node node) {
		// TODO this.getDirectPredecessors(node) must return set
		return new HashSet<Node>(this.getDirectPredecessors(node));
	}
	
	@Override
	public Set<Node> getPreset(Collection<Node> nodes) {
		// TODO this.getDirectPredecessors(nodes) must return set
		return new HashSet<Node>(this.getDirectPredecessors(nodes));
	}
	
	@Override
	public Set<Node> getSourceNodes() {
		return PetriNet.DIRECTED_GRAPH_ALGORITHMS.getSources(this);
	}
	
	@Override
	public Set<Place> getSourcePlaces() {
		Set<Place> result = new HashSet<Place>();
		
		for (Node node : this.getSourceNodes())
			if (node instanceof Place)
				result.add((Place)node);
		
		return result;
	}
	
	@Override
	public Set<Transition> getSourceTransitions() {
		Set<Transition> result = new HashSet<Transition>();
		
		for (Node node : this.getSourceNodes())
			if (node instanceof Transition)
				result.add((Transition)node);
		
		return result;
	}
	
	@Override
	public Set<Node> getSinkNodes() {
		return PetriNet.DIRECTED_GRAPH_ALGORITHMS.getSinks(this);
	}
	
	@Override
	public Set<Place> getSinkPlaces() {
		Set<Place> result = new HashSet<Place>();
		
		for (Node node : this.getSinkNodes())
			if (node instanceof Place)
				result.add((Place)node);
		
		return result;
	}
	
	@Override
	public Set<Transition> getSinkTransitions() {
		Set<Transition> result = new HashSet<Transition>();
		
		for (Node node : this.getSinkNodes())
			if (node instanceof Transition)
				result.add((Transition)node);
		
		return result;
	}
	
	@Override
	public Set<Node> getMin() {
		return this.getSourceNodes();
	}
	
	@Override
	public Set<Node> getMax() {
		return this.getSinkNodes();
	}
	
	@Override
	public PetriNet clone() {		
		PetriNet clone = (PetriNet) super.clone();
		return this.cloneHelper(clone, new HashMap<Node,Node>());
	}
	
	private PetriNet cloneHelper(PetriNet clone, Map<Node,Node> nodeMapping) {
		clone.clearMembers();

		for (Node n : this.getNodes()) {
			Node cn = (Node)n.clone();
			clone.addVertex(cn);
			nodeMapping.put(n,cn);
		}
		
		for (Flow f : this.getFlow()) {
			Flow cf = clone.addFlow(nodeMapping.get(f.getSource()),nodeMapping.get(f.getTarget()));
			
			if (f.getName() != null)
				cf.setName(new String(f.getName()));
			if (f.getDescription() != null)
				cf.setDescription(new String(f.getDescription()));
		}
		
		return clone;
	}
	
	public PetriNet clone(Map<Node,Node> nodeMapping) {
		PetriNet clone = (PetriNet) super.clone();
		return cloneHelper(clone, nodeMapping);
	}
	
	@Override
	public String toDOT() {
		String result = "digraph G {\n";
		result += "graph [fontname=\"Helvetica\" fontsize=10 nodesep=0.35 ranksep=\"0.25 equally\"];\n";
		result += "node [fontname=\"Helvetica\" fontsize=10 fixedsize style=filled fillcolor=white penwidth=\"2\"];\n";
		result += "edge [fontname=\"Helvetica\" fontsize=10 arrowhead=normal color=black];\n";
		result += "\n";
		result += "node [shape=circle];\n";
		
		for (Place place : this.getPlaces()) {
			String label = ""; 
			result += String.format("\tn%s[label=\"%s\" width=\".3\" height=\".3\"];\n", place.getId().replace("-", ""), label);
		}
		
		result += "\n";
		result += "node [shape=box];\n";
		
		for (Transition transition : this.getTransitions()) {
			if (transition.getName()=="") result += String.format("\tn%s[label=\"%s\" width=\".3\" height=\".1\"];\n", transition.getId().replace("-", ""), transition.getName());
			else result += String.format("\tn%s[label=\"%s\" width=\".3\" height=\".3\"];\n", transition.getId().replace("-", ""), transition.getName());
		}
		
		result += "\n";
		for (Flow flow: this.getFlow()) {
			result += String.format("\tn%s->n%s;\n", flow.getSource().getId().replace("-", ""), flow.getTarget().getId().replace("-", ""));
		}
		result += "}\n";
		
		return result;
	}

	@Override
	public Transition createTransition() {
		return new Transition();
	}

	@Override
	public Place createPlace() {
		return new Place();
	}

	@Override
	public IPetriNet<Flow,Node,Place,Transition> createPetriNet() {
		return new PetriNet();
	}
}
