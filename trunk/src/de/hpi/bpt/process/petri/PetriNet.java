package de.hpi.bpt.process.petri;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.hpi.bpt.graph.abs.AbstractDirectedGraph;
import de.hpi.bpt.graph.algo.DirectedGraphAlgorithms;
import de.hpi.bpt.hypergraph.abs.Vertex;

/**
 * Petri net implementation
 *  
 * @author Artem Polyvyanyy
 */
public class PetriNet extends AbstractDirectedGraph<Flow, Node> implements Cloneable {

	/**
	 * Silent transitions shall carry the following label.
	 */
	public static final String SILENT_LABEL = "tau";

	private DirectedGraphAlgorithms<Flow,Node> dga = null;
	
	public PetriNet() {}
	
	public Flow addFlow(Node from, Node to) {
		if (from == null || to == null) return null;
		
		Collection<Node> ss = new ArrayList<Node>(); ss.add(from);
		Collection<Node> ts = new ArrayList<Node>(); ts.add(to);
		
		if (!this.checkEdge(ss, ts)) return null;
		
		return new Flow(this, from, to);
	}
	
	public Node addNode(Node n) {
		return this.addVertex(n);
	}
	
	public Collection<Flow> getFlowRelation() {
		return this.getEdges();
	}
	
	public Set<Vertex> getEnabledElements() {
		Set<Vertex> res = new HashSet<Vertex>();
		Iterator<Transition> i = this.getTransitions().iterator();
		while(i.hasNext()) {
			Transition t = i.next();
			if (this.isEnabled(t))
				res.add(t);
		}
		
		return res;
	}
	
	public boolean isEnabled(Transition t) {
		Collection<Node> ps = getPredecessors(t);
		Iterator<Node> i = ps.iterator();
		while (i.hasNext()) {
			Node n = i.next();
			if (n instanceof Place) {
				if (((Place) n).getTokens()<=0) return false;
			}
			else return false;
		}
		return true;
	}
	
	public Collection<Place> getMarkedPlaces() {
		Collection<Place> markedPlaces = new HashSet<Place>();
		for (Place p : this.getPlaces())
			if (p.getTokens()>0)
				markedPlaces.add(p);
				
		return markedPlaces;
	}
	
	public boolean isTerminated() {
		Iterator<Transition> i = this.getTransitions().iterator();
		while (i.hasNext()) {
			if (this.isEnabled(i.next())) return false;
		}
		return true;
	}
	
	public boolean fire(Vertex v) {
		Transition t = null;
		if (v instanceof Transition) {
			t = (Transition) v;
		}
		else return false;
		
		if (this.isEnabled(t)) {			
			Collection<Node> ps = getPredecessors(t);
			Iterator<Node> i = ps.iterator();
			while (i.hasNext()) {
				Node n = i.next();
				Place p = (Place) n;
				p.takeToken();
			}
			
			Collection<Node> ss = getSuccessors(t);
			i = ss.iterator();
			while (i.hasNext()) {
				Node n = i.next();
				if (n instanceof Place) {
					Place p = (Place) n;
					p.putToken();	
				}
			}
		}
		else return false;
		
		return true;
	}
	
	public boolean putToken(Place p) {
		return p.putToken();
	}

	public Collection<Node> getNodes() {
		return this.getVertices();
	}
	
	public Collection<Transition> getTransitions() {
		Collection<Transition> result = new ArrayList<Transition>();
		
		for (Node n : this.getVertices()) {
			if (n instanceof Transition)
				result.add((Transition)n);
		}
		
		return result;
	}
	
	public Collection<Transition> getNonSilentTransitions() {
		Collection<Transition> result = new ArrayList<Transition>();
		
		for (Transition t : this.getTransitions()) {
			if (!t.getName().equals(SILENT_LABEL))
				result.add(t);
		}
		
		return result;
	}

	
	public Collection<Place> getPlaces() {
		Collection<Place> result = new ArrayList<Place>();
		
		for (Node n : this.getVertices()) {
			if (n instanceof Place)
				result.add((Place)n);
		}
		
		return result;
	}
	
	public Collection<Place> getPostset(Transition t) {
		if (!this.contains(t)) return null;
		
		Collection<Place> result = new ArrayList<Place>();
		for (Node n : this.getSuccessors(t)) {
			if (n instanceof Place)
				result.add((Place)n);
		}
		
		return result;
	}
	
	public Collection<Transition> getPostset(Place p) {
		if (!this.contains(p)) return null;
		
		Collection<Transition> result = new ArrayList<Transition>();
		for (Node n : this.getSuccessors(p)) {
			if (n instanceof Transition)
				result.add((Transition)n);
		}
		
		return result;
	}
	
	public Collection<Node> getPostset(Node n) {
		if (!this.contains(n)) return null;
		
		return this.getSuccessors(n);
	}
	
	
	public Collection<Place> getPreset(Transition t) {
		if (!this.contains(t)) return null;
		
		Collection<Place> result = new ArrayList<Place>();
		for (Node n : this.getPredecessors(t)) {
			if (n instanceof Place)
				result.add((Place)n);
		}
		
		return result;
	}
	
	public Collection<Transition> getPreset(Place p) {
		if (!this.contains(p)) return null;
		
		Collection<Transition> result = new ArrayList<Transition>();
		for (Node n : this.getPredecessors(p)) {
			if (n instanceof Transition)
				result.add((Transition)n);
		}
		
		return result;
	}
	
	public Collection<Node> getPreset(Node n) {
		if (!this.contains(n)) return null;
		
		return this.getPredecessors(n);
	}
	
	public Collection<Node> getSourceNodes() {
		if (dga == null) dga = new DirectedGraphAlgorithms<Flow, Node>();
		return dga.getInputVertices(this);
	}
	
	public Collection<Place> getSourcePlaces() {
		Collection<Place> result = new ArrayList<Place>();
		for (Node n : getSourceNodes())
			if (n instanceof Place)
				result.add((Place)n);
		
		return result;
	}
	
	public Collection<Transition> getSourceTransitions() {
		Collection<Transition> result = new ArrayList<Transition>();
		for (Node n : getSourceNodes())
			if (n instanceof Transition)
				result.add((Transition)n);
		
		return result;
	}
	
	public Collection<Node> getSinkNodes() {
		if (dga == null) dga = new DirectedGraphAlgorithms<Flow, Node>();
		return dga.getOutputVertices(this);
	}
	
	public Collection<Place> getSinkPlaces() {
		Collection<Place> result = new ArrayList<Place>();
		for (Node n : getSinkNodes())
			if (n instanceof Place)
				result.add((Place)n);
		
		return result;
	}
	
	public Collection<Transition> getSinkTransitions() {
		Collection<Transition> result = new ArrayList<Transition>();
		for (Node n : getSinkNodes())
			if (n instanceof Transition)
				result.add((Transition)n);
		
		return result;
	}
	
	public boolean hasCycle() {
		if (dga == null) dga = new DirectedGraphAlgorithms<Flow, Node>();
		return dga.hasCycles(this);
	}
	
	public boolean hasPath(Node from, Node to) {
		if (dga == null) dga = new DirectedGraphAlgorithms<Flow, Node>();
		return dga.hasPath(this, from, to);
	}
	
	public boolean isFreeChoice() {
		return PNAnalyzer.isFreeChoice(this);
	}
	
	public boolean isExtendedFreeChoice() {
		return PNAnalyzer.isExtendedFreeChoice(this);
	}
	
	public boolean isWFNet() {
		return PNAnalyzer.isWorkflowNet(this);
	}

	public boolean isSNet() {
		return PNAnalyzer.isSNet(this);
	}

	public boolean isTNet() {
		return PNAnalyzer.isTNet(this);
	}

	public Map<Node, Set<Node>> getDominators() {
		if (dga == null) dga = new DirectedGraphAlgorithms<Flow, Node>();
		return dga.getDominators(this, false);
	}

	public Map<Node, Set<Node>> getPostDominators() {
		if (dga == null) dga = new DirectedGraphAlgorithms<Flow, Node>();
		return dga.getDominators(this, true);
	}

	/**
	 * Returns the current marking of this petri net.
	 * @return {@link Marking}
	 */
	public Marking getMarking() {
		Marking marking = new Marking(this);
		for (Place p:getPlaces()) {
			marking.put(p, p.getTokens());
		}
		return marking;
	}
	
	/**
	 * Applies the given {@link Marking} to this petri net.
	 * Places not listed in the marking will receive zero tokens.
	 * @param marking
	 */
	public void setMarking(Marking marking) {
		for (Place p:getPlaces()) {
			Integer value = marking.get(p);
			if (value != null)
				p.setTokens(value);
			else
				p.setTokens(0);
		}
	}
	
	/**
	 * Set natural marking.
	 * Natural marking is a marking which puts one token at each source place of the net and no tokens elsewhere.
	 */
	public void setNaturalInitialMarking() {
		Collection<Place> sources = getSourcePlaces();
		
		for (Place p : getPlaces())
			if (sources.contains(p)) 
				p.setTokens(1);
			else 
				p.setTokens(0);
	}
	
	/**
	 * Reset private and protected members. Needed for clone routines.
	 */
	@Override
	public void clearMembers() {
		super.clearMembers();
		this.dga = null;
	}
	
	/**
	 * Creates a deep copy of whole Petri net. 
	 * 
	 * @return the clone of the Petri net
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		PetriNet clone = (PetriNet) super.clone();
		
		// clear algorithm class
		clone.clearMembers();
		
		// workaround since abstract graph notifier is not cloned
		clone.removeVertices(clone.getVertices());
		clone.removeEdges(clone.getEdges());
		
		Map<Node,Node> nodeCopies = new HashMap<Node, Node>();
		
		for (Node n : this.getNodes()) {
			Node c = (Node)n.clone();
			clone.addNode(c);
			nodeCopies.put(n, c);
		}
		
		for (Flow f : this.getFlowRelation()) {
			Node from = nodeCopies.get(f.getSource());
			Node to = nodeCopies.get(f.getTarget());
			clone.addFlow(from, to);
		}
		
		return clone;
	}
	
	/**
	 * Creates a deep copy of whole Petri net and enters 
	 * the node mapping between the original and the clone
	 * into the given map.
	 * 
	 * @param nodeMapping, mapping between nodes of the original and of the clone
	 * 
	 * @return the clone of the Petri net
	 */
	public Object clone(Map<Node,Node> nodeMapping) throws CloneNotSupportedException {
		PetriNet clone = (PetriNet) super.clone();
		
		// clear members that have not been cloned by a deep copy
		clone.clearMembers();
		
		for (Node n : this.getNodes()) {
			Node c = (Node)n.clone();
			clone.addNode(c);
			nodeMapping.put(n, c);
		}
		
		for (Flow f : this.getFlowRelation()) {
			Node from = nodeMapping.get(f.getSource());
			Node to = nodeMapping.get(f.getTarget());
			clone.addFlow(from, to);
		}
		
		return clone;
	}
	
	@Override
	public String toDOT() {
		String result = "digraph G {\n";
		result += "graph [fontname=\"Helvetica\" fontsize=10 nodesep=0.35 ranksep=\"0.25 equally\"];\n";
		result += "node [fontname=\"Helvetica\" fontsize=10 fixedsize style=filled fillcolor=white penwidth=\"2\"];\n";
		result += "edge [fontname=\"Helvetica\" fontsize=10 arrowhead=normal color=black];\n";
		result += "\n";
		result += "node [shape=circle];\n";
		
		for (Place n : this.getPlaces()) {
			String label = "";
			label += (n.getTokens()>0) ? n.getTokens() : ""; 
			result += String.format("\tn%s[label=\"%s\" width=\".3\" height=\".3\"];\n", n.getId().replace("-", ""), label);
		}
		
		result += "\n";
		result += "node [shape=box];\n";
		
		for (Transition t : this.getTransitions()) {
			if (t.getName()=="") result += String.format("\tn%s[label=\"%s\" width=\".3\" height=\".1\"];\n", t.getId().replace("-", ""), t.getName());
			else result += String.format("\tn%s[label=\"%s\" width=\".3\" height=\".3\"];\n", t.getId().replace("-", ""), t.getName());
		}
		
		result += "\n";
		for (Flow f: this.getFlowRelation()) {
			result += String.format("\tn%s->n%s;\n", f.getSource().getId().replace("-", ""), f.getTarget().getId().replace("-", ""));
		}
		result += "}\n";
		
		return result;
	}

	public Node findNode(String string) {
		for (Node n : this.getNodes())
			if (n.getId().equals(string))
				return n;
		return null;
	}
	

	/***************************************
	 * Remove methods
	 **************************************/
	
	public Node removeNode(Node n) {
		return this.removeVertex(n);
	}
	
	public Collection<Node> removeNodes(Collection<Node> ns) {
		return this.removeVertices(ns);
	}
	
	public Place removePlace(Place p) {
		return (this.removeVertex(p) == null) ? null : p;
	}
	
	public Collection<Place> removePlaces(Collection<Place> ps) {
		Collection<Place> result = new ArrayList<Place>();
		
		Iterator<Place> i = ps.iterator();
		while (i.hasNext()) {
			Place p = i.next();
			if (this.removePlace(p) != null)
				result.add(p);
		}
		return (result.size()>0) ? result : null;
	}
	
	public Transition removeTransition(Transition t) {
		return (this.removeVertex(t) == null) ? null : t;
	}
	
	public Collection<Transition> removeTransitions(Collection<Transition> ts) {
		Collection<Transition> result = new ArrayList<Transition>();
		
		Iterator<Transition> i = ts.iterator();
		while (i.hasNext()) {
			Transition t = i.next();
			if (this.removeTransition(t) != null)
				result.add(t);
		}
		return (result.size()>0) ? result : null;
	}
}
