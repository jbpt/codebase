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
 * @author Matthias, Weidlich
 * @author Andreas Meyer
 */
public class PetriNet extends AbstractDirectedGraph<IFlow,INode> implements IPetriNet {

	// Directed graph algorithms 
	public static DirectedGraphAlgorithms<IFlow,INode> DGA = new DirectedGraphAlgorithms<IFlow,INode>();
	
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
	protected IFlow addFlow(INode from, INode to) {
		if (from == null || to == null) return null;
		
		if ((from instanceof IPlace && to instanceof ITransition) ||
				from instanceof ITransition && to instanceof IPlace) {
			
			Collection<INode> ss = new ArrayList<INode>(); ss.add(from);
			Collection<INode> ts = new ArrayList<INode>(); ts.add(to);
			
			if (!this.checkEdge(ss,ts)) return null;
			
			return new Flow(this, from, to);
		}
		
		return null;
	}
	
	@Override
	public IFlow addFlow(IPlace place, ITransition transition) {
		return this.addFlow(place,transition);
	}
	
	@Override
	public IFlow addFlow(ITransition transition, IPlace place) {
		return this.addFlow(transition,place);
	}
	
	@Override
	public INode addNode(INode node) {
		return this.addVertex(node);
	}
	
	@Override
	public Collection<INode> addNodes(Collection<INode> nodes) {
		Collection<INode> result = this.addVertices(nodes);
		return result==null ? new ArrayList<INode>() : result;
	}

	@Override
	public IPlace addPlace(IPlace place) {
		return this.addVertex(place)==null ? null : place;
	}
	
	@Override
	public Collection<IPlace> addPlaces(Collection<IPlace> places) {
		Collection<IPlace> result = new ArrayList<IPlace>();
		if (places == null) return result;
		
		for (IPlace place : places)
			if (this.addVertex(place) != null)
				result.add(place);
		
		return result;
	}
	
	@Override
	public ITransition addTransition(ITransition transition) {
		return this.addVertex(transition)==null ? null : transition;
	}
	
	@Override
	public Collection<ITransition> addTransitions(Collection<ITransition> transitions) {
		Collection<ITransition> result = new ArrayList<ITransition>();
		if (transitions == null) return result;
		
		for (ITransition transition : transitions)
			if (this.addVertex(transition) != null)
				result.add(transition);
		
		return result;
	}
	
	@Override
	public INode removeNode(INode node) {
		return this.removeVertex(node);
	}
	
	@Override
	public Collection<INode> removeNodes(Collection<INode> nodes) {
		Collection<INode> result = this.removeVertices(nodes);
		return result==null ? new ArrayList<INode>() : result;
	}
	
	@Override
	public IPlace removePlace(IPlace place) {
		return this.removeVertex(place) == null ? null : place;
	}
	
	@Override
	public Collection<IPlace> removePlaces(Collection<IPlace> places) {
		Collection<IPlace> result = new ArrayList<IPlace>();
		if (places == null) return result;
		
		for (IPlace place : places)
			if (this.removeVertex(place) != null)
				result.add(place);
		
		return result;
	}
	
	@Override
	public ITransition removeTransition(ITransition transition) {
		return this.removeVertex(transition) == null ? null : transition;
	}
	
	@Override
	public Collection<ITransition> removeTransitions(Collection<ITransition> transitions) {
		Collection<ITransition> result = new ArrayList<ITransition>();
		if (transitions == null) return result;
		
		for (ITransition transition : transitions)
			if (this.removeVertex(transition) != null)
				result.add(transition);
		
		return result;
	}
	
	@Override
	public IFlow removeFlow(IFlow flow) {
		return this.removeEdge(flow);
	}
	
	@Override
	public Collection<IFlow> removeFlow(Collection<IFlow> flow) {
		Collection<IFlow> result = this.removeEdges(flow);
		return result==null ? new ArrayList<IFlow>() : result;
	}
	
	
	@Override
	public Collection<INode> getNodes() {
		return this.getVertices();
	}
	
	@Override
	public Collection<IPlace> getPlaces() {
		Collection<IPlace> result = new ArrayList<IPlace>();
		
		for (INode node : this.getVertices())
			if (node instanceof IPlace)
				result.add((IPlace)node);
		
		return result;
	}

	@Override
	public Collection<ITransition> getTransitions() {
		Collection<ITransition> result = new ArrayList<ITransition>();
		
		for (INode node : this.getVertices())
			if (node instanceof ITransition)
				result.add((ITransition)node);
		
		return result;
	}
	
	@Override
	public Collection<IFlow> getFlow() {
		return this.getEdges();
	}
	
	@Override
	public Collection<ITransition> getSilentTransitions() {
		Collection<ITransition> result = new ArrayList<ITransition>();
		
		for (INode node : this.getVertices())
			if (node instanceof ITransition && node.getLabel().isEmpty())
				result.add((ITransition)node);	
		
		return result;
	}

	@Override
	public Collection<ITransition> getObservableTransitions() {
		Collection<ITransition> result = new ArrayList<ITransition>();
		
		for (INode node : this.getVertices())
			if (node instanceof ITransition && !node.getLabel().isEmpty())
				result.add((ITransition)node);	
		
		return result;
	}
	
	@Override
	public Collection<IPlace> getPostset(ITransition transition) {
		Collection<IPlace> result = new ArrayList<IPlace>();
		for (INode node : this.getDirectSuccessors(transition))
			if (node instanceof IPlace)
				result.add((IPlace)node);
		
		return result;
	}
	
	@Override
	public Collection<IPlace> getPostsetPlaces(Collection<ITransition> transitions) {
		Set<IPlace> result = new HashSet<IPlace>();
		
		for (ITransition transition : transitions)
			for (INode node : this.getDirectSuccessors(transition))
				if (node instanceof IPlace)
					result.add((IPlace)node);
		
		return result;
	}
	
	@Override
	public Collection<ITransition> getPostset(IPlace place) {
		Collection<ITransition> result = new ArrayList<ITransition>();
		
		for (INode node : this.getDirectSuccessors(place)) {
			if (node instanceof ITransition)
				result.add((ITransition)node);
		}
		
		return result;
	}
	
	@Override
	public Collection<ITransition> getPostsetTransitions(Collection<IPlace> places) {
		Set<ITransition> result = new HashSet<ITransition>();
		
		for (IPlace place : places)
			for (INode node : this.getDirectSuccessors(place))
				if (node instanceof ITransition)
					result.add((ITransition)node);
		
		return result;
	}

	@Override
	public Collection<INode> getPostset(INode n) {
		return this.getDirectSuccessors(n);
	}
	
	@Override
	public Collection<INode> getPostset(Collection<INode> nodes) {
		Collection<INode> result = this.getDirectSuccessors(nodes); 
		return result == null ? new ArrayList<INode>() : result;
	}
	
	@Override
	public Collection<IPlace> getPreset(ITransition transition) {
		Collection<IPlace> result = new ArrayList<IPlace>();
		
		for (INode node : this.getDirectPredecessors(transition))
			if (node instanceof Place)
				result.add((Place)node);
		
		return result;
	}

	@Override
	public Collection<IPlace> getPresetPlaces(Collection<ITransition> transitions) {
		Set<IPlace> result = new HashSet<IPlace>();
		
		for (ITransition transition : transitions)
			for (INode node : this.getDirectPredecessors(transition))
				if (node instanceof IPlace)
					result.add((IPlace)node);
		
		return result;
	}
	
	@Override
	public Collection<ITransition> getPreset(IPlace place) {
		Collection<ITransition> result = new ArrayList<ITransition>();
		
		for (INode node : this.getDirectPredecessors(place))
			if (node instanceof ITransition)
				result.add((ITransition)node);
		
		return result;
	}
	
	@Override
	public Collection<ITransition> getPresetTransitions(Collection<IPlace> places) {
		Set<ITransition> result = new HashSet<ITransition>();
		
		for (IPlace place : places)
			for (INode node : this.getDirectPredecessors(place))
				if (node instanceof ITransition)
					result.add((ITransition)node);
		
		return result;
	}
	
	@Override
	public Collection<INode> getPreset(INode node) {
		Collection<INode> result = this.getDirectPredecessors(node); 
		return result == null ? new ArrayList<INode>() : result;
	}
	
	@Override
	public Collection<INode> getPreset(Collection<INode> nodes) {
		Collection<INode> result = this.getDirectPredecessors(nodes); 
		return result == null ? new ArrayList<INode>() : result;
	}
	
	@Override
	public Collection<INode> getSourceNodes() {
		return PetriNet.DGA.getSources(this);
	}
	
	@Override
	public Collection<IPlace> getSourcePlaces() {
		Collection<IPlace> result = new ArrayList<IPlace>();
		
		for (INode node : this.getSourceNodes())
			if (node instanceof IPlace)
				result.add((IPlace)node);
		
		return result;
	}
	
	@Override
	public Collection<ITransition> getSourceTransitions() {
		Collection<ITransition> result = new ArrayList<ITransition>();
		
		for (INode node : this.getSourceNodes())
			if (node instanceof ITransition)
				result.add((ITransition)node);
		
		return result;
	}
	
	@Override
	public Collection<INode> getSinkNodes() {
		return PetriNet.DGA.getSinks(this);
	}
	
	@Override
	public Collection<IPlace> getSinkPlaces() {
		Collection<IPlace> result = new ArrayList<IPlace>();
		
		for (INode node : this.getSinkNodes())
			if (node instanceof IPlace)
				result.add((IPlace)node);
		
		return result;
	}
	
	@Override
	public Collection<ITransition> getSinkTransitions() {
		Collection<ITransition> result = new ArrayList<ITransition>();
		
		for (INode node : this.getSinkNodes())
			if (node instanceof ITransition)
				result.add((ITransition)node);
		
		return result;
	}
	
	@Override
	public Collection<INode> getMin() {
		return this.getSourceNodes();
	}
	
	@Override
	public Collection<INode> getMax() {
		return this.getSinkNodes();
	}
	
	@Override
	public boolean isTRestricted() {
		return this.getSourceTransitions().isEmpty() && this.getSinkTransitions().isEmpty();
	}
	
	@Override
	public PetriNet clone() {		
		PetriNet clone = (PetriNet) super.clone();
		return this.cloneHelper(clone, new HashMap<INode,INode>());
	}
	
	private PetriNet cloneHelper(PetriNet clone, Map<INode,INode> nodeMapping) {
		clone.clearMembers();

		for (INode n : this.getNodes()) {
			INode cn = (INode)n.clone();
			clone.addVertex(cn);
			nodeMapping.put(n,cn);
		}
		
		for (IFlow f : this.getFlow()) {
			IFlow cf = clone.addFlow(nodeMapping.get(f.getSource()),nodeMapping.get(f.getTarget()));
			
			if (f.getName() != null)
				cf.setName(new String(f.getName()));
			if (f.getDescription() != null)
				cf.setDescription(new String(f.getDescription()));
		}
		
		return clone;
	}
	
	public PetriNet clone(Map<INode,INode> nodeMapping) {
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
		
		for (IPlace place : this.getPlaces()) {
			String label = ""; 
			result += String.format("\tn%s[label=\"%s\" width=\".3\" height=\".3\"];\n", place.getId().replace("-", ""), label);
		}
		
		result += "\n";
		result += "node [shape=box];\n";
		
		for (ITransition transition : this.getTransitions()) {
			if (transition.getName()=="") result += String.format("\tn%s[label=\"%s\" width=\".3\" height=\".1\"];\n", transition.getId().replace("-", ""), transition.getName());
			else result += String.format("\tn%s[label=\"%s\" width=\".3\" height=\".3\"];\n", transition.getId().replace("-", ""), transition.getName());
		}
		
		result += "\n";
		for (IFlow flow: this.getFlow()) {
			result += String.format("\tn%s->n%s;\n", flow.getSource().getId().replace("-", ""), flow.getTarget().getId().replace("-", ""));
		}
		result += "}\n";
		
		return result;
	}

	@Override
	public void doTRestrict() {
		for (ITransition transition : this.getSourceTransitions())
			this.addFlow(new Place(), transition);
		
		for (ITransition transition : this.getSinkTransitions())
			this.addFlow(transition, new Place());
	}
}
