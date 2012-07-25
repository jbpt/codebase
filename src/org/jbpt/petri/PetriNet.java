package org.jbpt.petri;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jbpt.algo.graph.DirectedGraphAlgorithms;
import org.jbpt.graph.abs.AbstractDirectedGraph;

/**
 * Implementation of a Petri net. 
 * 
 * @author Artem Polyvyanyy
 * @author Matthias Weidlich
 * @author Andreas Meyer
 */
public class PetriNet extends AbstractDirectedGraph<Flow,Node> implements IPetriNet<Flow,Node,Place,Transition> {

	// Directed graph algorithms
	public static DirectedGraphAlgorithms<Flow,Node> DGA = new DirectedGraphAlgorithms<Flow,Node>();
	
	/**
	 * Empty constructor.
	 */
	public PetriNet(){}
	
	/**
	 * Add flow to this net. 
	 * The method ensures that the net stays bipartite.
	 * 
	 * @param from Source node.
	 * @param to Target node.
	 * @return Flow added to the net; <tt>null</tt> if no flow was added.
	 */
	public Flow addFreshFlow(Node from, Node to) {
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
		return this.addFreshFlow(place,transition);
	}
	
	@Override
	public Flow addFlow(Transition transition, Place place) {
		return this.addFreshFlow(transition,place);
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
	public Collection<Node> getNodes() {
		return this.getVertices();
	}
	
	@Override
	public Collection<Place> getPlaces() {
		Collection<Place> result = new ArrayList<Place>();
		
		for (Node node : this.getVertices())
			if (node instanceof Place)
				result.add((Place)node);
		
		return result;
	}

	@Override
	public Collection<Transition> getTransitions() {
		Collection<Transition> result = new ArrayList<Transition>();
		
		for (Node node : this.getVertices())
			if (node instanceof Transition)
				result.add((Transition)node);
		
		return result;
	}
	
	@Override
	public Collection<Flow> getFlow() {
		return this.getEdges();
	}
	
	@Override
	public Collection<Transition> getSilentTransitions() {
		Collection<Transition> result = new ArrayList<Transition>();
		
		for (Node node : this.getVertices())
			if (node instanceof Transition && node.getLabel().isEmpty())
				result.add((Transition)node);	
		
		return result;
	}

	@Override
	public Collection<Transition> getObservableTransitions() {
		Collection<Transition> result = new ArrayList<Transition>();
		
		for (Node node : this.getVertices())
			if (node instanceof Transition && !node.getLabel().isEmpty())
				result.add((Transition)node);	
		
		return result;
	}
	
	@Override
	public Collection<Place> getPostset(Transition transition) {
		Collection<Place> result = new ArrayList<Place>();
		for (Node node : this.getDirectSuccessors(transition))
			if (node instanceof Place)
				result.add((Place)node);
		
		return result;
	}
	
	@Override
	public Collection<Place> getPostsetPlaces(Collection<Transition> transitions) {
		Set<Place> result = new HashSet<Place>();
		
		for (Transition transition : transitions)
			for (Node node : this.getDirectSuccessors(transition))
				if (node instanceof Place)
					result.add((Place)node);
		
		return result;
	}
	
	@Override
	public Collection<Transition> getPostset(Place place) {
		Collection<Transition> result = new ArrayList<Transition>();
		
		for (Node node : this.getDirectSuccessors(place)) {
			if (node instanceof Transition)
				result.add((Transition)node);
		}
		
		return result;
	}
	
	@Override
	public Collection<Transition> getPostsetTransitions(Collection<Place> places) {
		Set<Transition> result = new HashSet<Transition>();
		
		for (Place place : places)
			for (Node node : this.getDirectSuccessors(place))
				if (node instanceof Transition)
					result.add((Transition)node);
		
		return result;
	}

	@Override
	public Collection<Node> getPostset(Node n) {
		return this.getDirectSuccessors(n);
	}
	
	@Override
	public Collection<Node> getPostset(Collection<Node> nodes) {
		Collection<Node> result = this.getDirectSuccessors(nodes); 
		return result == null ? new ArrayList<Node>() : result;
	}
	
	@Override
	public Collection<Place> getPreset(Transition transition) {
		Collection<Place> result = new ArrayList<Place>();
		
		for (Node node : this.getDirectPredecessors(transition))
			if (node instanceof Place)
				result.add((Place)node);
		
		return result;
	}

	@Override
	public Collection<Place> getPresetPlaces(Collection<Transition> transitions) {
		Set<Place> result = new HashSet<Place>();
		
		for (Transition transition : transitions)
			for (Node node : this.getDirectPredecessors(transition))
				if (node instanceof Place)
					result.add((Place)node);
		
		return result;
	}
	
	@Override
	public Collection<Transition> getPreset(Place place) {
		Collection<Transition> result = new ArrayList<Transition>();
		
		for (Node node : this.getDirectPredecessors(place))
			if (node instanceof Transition)
				result.add((Transition)node);
		
		return result;
	}
	
	@Override
	public Collection<Transition> getPresetTransitions(Collection<Place> places) {
		Set<Transition> result = new HashSet<Transition>();
		
		for (Place place : places)
			for (Node node : this.getDirectPredecessors(place))
				if (node instanceof Transition)
					result.add((Transition)node);
		
		return result;
	}
	
	@Override
	public Collection<Node> getPreset(Node node) {
		Collection<Node> result = this.getDirectPredecessors(node); 
		return result == null ? new ArrayList<Node>() : result;
	}
	
	@Override
	public Collection<Node> getPreset(Collection<Node> nodes) {
		Collection<Node> result = this.getDirectPredecessors(nodes); 
		return result == null ? new ArrayList<Node>() : result;
	}
	
	@Override
	public Collection<Node> getSourceNodes() {
		return PetriNet.DGA.getSources(this);
	}
	
	@Override
	public Collection<Place> getSourcePlaces() {
		Collection<Place> result = new ArrayList<Place>();
		
		for (Node node : this.getSourceNodes())
			if (node instanceof Place)
				result.add((Place)node);
		
		return result;
	}
	
	@Override
	public Collection<Transition> getSourceTransitions() {
		Collection<Transition> result = new ArrayList<Transition>();
		
		for (Node node : this.getSourceNodes())
			if (node instanceof Transition)
				result.add((Transition)node);
		
		return result;
	}
	
	@Override
	public Collection<Node> getSinkNodes() {
		return PetriNet.DGA.getSinks(this);
	}
	
	@Override
	public Collection<Place> getSinkPlaces() {
		Collection<Place> result = new ArrayList<Place>();
		
		for (Node node : this.getSinkNodes())
			if (node instanceof Place)
				result.add((Place)node);
		
		return result;
	}
	
	@Override
	public Collection<Transition> getSinkTransitions() {
		Collection<Transition> result = new ArrayList<Transition>();
		
		for (Node node : this.getSinkNodes())
			if (node instanceof Transition)
				result.add((Transition)node);
		
		return result;
	}
	
	@Override
	public Collection<Node> getMin() {
		return this.getSourceNodes();
	}
	
	@Override
	public Collection<Node> getMax() {
		return this.getSinkNodes();
	}
	
	@Override
	public boolean isTRestricted() {
		return this.getSourceTransitions().isEmpty() && this.getSinkTransitions().isEmpty();
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
			Flow cf = clone.addFreshFlow(nodeMapping.get(f.getSource()),nodeMapping.get(f.getTarget()));
			
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
	public void doTRestrict() {
		for (Transition transition : this.getSourceTransitions())
			this.addFlow(new Place(), transition);
		
		for (Transition transition : this.getSinkTransitions())
			this.addFlow(transition, new Place());
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
