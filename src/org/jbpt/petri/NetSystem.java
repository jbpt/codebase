package org.jbpt.petri;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Net system implementation
 * 
 * @author Artem Polyvyanyy
 */
public class NetSystem extends PetriNet {
	
	protected Marking M = null;
	
	public NetSystem() {
		this.M = new Marking(this);
	}
	
	@Override
	public Node removeNode(Node n) {
		Node result = super.removeNode(n);
		if (result!=null && n instanceof Place) {
			this.M.remove((Place)n);
		}
		return result;
	}

	@Override
	public Collection<Node> removeNodes(Collection<Node> ns) {
		Collection<Node> result = super.removeNodes(ns);
		if (result!=null) {
			for (Node n : result) {
				if (n instanceof Place)
					this.M.remove((Place)n);
			}
		}
		return result;
	}

	@Override
	public Place removePlace(Place p) {
		Place result = super.removePlace(p);
		if (result!=null) {
			this.M.remove(p);
		}
		return result;
	}

	@Override
	public Collection<Place> removePlaces(Collection<Place> ps) {
		Collection<Place> result = super.removePlaces(ps);
		if (result!=null) {
			for (Place p : result) {
				this.M.remove(p);
			}
		}
		return result;
	}

	/**
	 * Get marking
	 * @return Marking of the net system
	 */
	public Marking getMarking() {
		return this.M;
	}

	/**
	 * Get marked places of this net system.
	 * 
	 * @return Set of all marked places of this net system.
	 */
	public Set<Place> getMarkedPlaces() {
		return this.M.keySet();
	}
	
	/**
	 * Get enabled transitions of the net system.
	 * 
	 * @return Enabled transitions of the net system.
	 */
	public Set<Transition> getEnabledTransitions() {
		Set<Transition> result = new HashSet<Transition>();
		Set<Place> marked = new HashSet<Place>(this.getMarkedPlaces());
		
		for (Transition t : this.getTransitions()) {
			if (marked.containsAll(this.getPreset(t)))
				result.add(t);
		}
		
		return result;
	}
	
	/**
	 * Check if transition is enabled.
	 * 
	 * @param t Transition
	 * @return <tt>true</tt> if transition is enabled; <tt>false</tt> otherwise.
	 */
	public boolean isEnabled(Transition t) {
		if (!this.getTransitions().contains(t)) return false;
		
		for (Place p : this.getPreset(t))
			if (!this.isMarked(p))
				return false;
			
		return true;
	}
	
	/**
	 * Check if place is marked, i.e., contains at least one token
	 * @param p Place
	 * @return <tt>true</tt> if place is marked; <tt>false</tt> otherwise
	 */
	private boolean isMarked(Place p) {
		return this.M.isMarked(p);
	}
	
	/**
	 * Fire a transition in this net system. 
	 * Transition fires only if it is enabled. 
	 * Firing of a transition removes one token from every preplace and adds one token to every postplace of the transition. 
	 * 
	 * @param t Transition to fire.
	 * @return <tt>true</tt> if firing took place; <tt>false</tt> otherwise.
	 */
	public boolean fire(Transition t) {
		if (!this.getTransitions().contains(t)) return false;
		
		if (!this.isEnabled(t)) return false;
		
		for (Place p : this.getPreset(t))
			this.M.put(p, this.M.get(p)-1);
		
		for (Place p : this.getPostset(t))
			this.M.put(p, this.M.get(p)+1);
		
		return true;
	}

	@Override
	public String toDOT() {
		String result = "digraph G {\n";
		result += "graph [fontname=\"Helvetica\" fontsize=10 nodesep=0.35 ranksep=\"0.25 equally\"];\n";
		result += "node [fontname=\"Helvetica\" fontsize=10 fixedsize style=filled fillcolor=white penwidth=\"2\"];\n";
		result += "edge [fontname=\"Helvetica\" fontsize=10 arrowhead=normal color=black];\n";
		result += "\n";
		result += "node [shape=circle];\n";
		
		for (Place p : this.getPlaces()) {
			Integer n = this.M.get(p);
			String label = ((n == 0) || (n == null)) ? p.getLabel() : p.getLabel() + "[" + n.toString() + "]"; 
			result += String.format("\tn%s[label=\"%s\" width=\".3\" height=\".3\"];\n", p.getId().replace("-", ""), label);
		}
		
		result += "\n";
		result += "node [shape=box];\n";
		
		for (Transition t : this.getTransitions()) {
			String fillColor = this.isEnabled(t) ? " fillcolor=\"#9ACD32\"" : "";
			if (t.getName()=="")
				result += String.format("\tn%s[label=\"%s\" width=\".3\""+fillColor+" height=\".1\"];\n", t.getId().replace("-", ""), t.getName());
			else 
				result += String.format("\tn%s[label=\"%s\" width=\".3\""+fillColor+" height=\".3\"];\n", t.getId().replace("-", ""), t.getName());
		}
		
		result += "\n";
		for (Flow f: this.getFlow()) {
			result += String.format("\tn%s->n%s;\n", f.getSource().getId().replace("-", ""), f.getTarget().getId().replace("-", ""));
		}
		result += "}\n";
		
		return result;
	}
	
	/**
	 * Create a deep copy of this net system. 
	 * 
	 * @return A clone of this instance.
	 */
	@Override
	public NetSystem clone() {
		Map<Node,Node> nodeMapping = new HashMap<Node,Node>();
		NetSystem clone = (NetSystem) super.clone(nodeMapping);
		cloneHelper(clone, nodeMapping);
		
		return clone;
	}
	
	/**
	 * Create a deep copy of this net system and enter the node mapping between this node system and its copy into the given map. 
	 * 
	 * @param nodeMapping Container to store the node mapping.
	 * @return A clone of this instance.
	 */
	@Override
	public NetSystem clone(Map<Node,Node> nodeMapping) {
		if (nodeMapping==null)
			nodeMapping = new HashMap<Node,Node>();
		
		NetSystem clone = (NetSystem) super.clone(nodeMapping);
		cloneHelper(clone,nodeMapping);
		
		return clone;
	}
	
	/**
	 * This method clones the marking of the net system. 
	 * 
	 * @param clone A clone object.
	 * @param nodeMapping Mapping of nodes of the original net system to nodes of its clone object. 
	 */
	private void cloneHelper(NetSystem clone, Map<Node,Node> nodeMapping) {
		// clone the marking 
		Marking cMarking = new Marking(clone);
		clone.M = cMarking;
		// initialise marking according to original net system
		for (Place p : this.getMarkedPlaces()) 
			clone.putTokens((Place) nodeMapping.get(p), this.getTokens(p));
	}
	
	/**
	 * Put tokens at a place.
	 * 
	 * @param p Place
	 * @param tokens Number of tokens to put
	 * @return the previous number of tokens at p, or <tt>null</tt> if parameters are wrong (either are equal to <tt>null</tt> or place does not belong to the net system, etc)
	 */
	public Integer putTokens(Place p, Integer tokens) {
		return this.M.put(p, tokens);
	}
	
	/**
	 * Get number of tokens at a place.
	 * 
	 * @param p Place
	 * @return Number of tokens at p.
	 */
	public Integer getTokens(Place p) {
		return this.M.get(p);
	}
	
	/**
	 * Changes marking of the net system to its natural initial marking, i.e., 
	 * the marking which put one token at each source place of the net system and no tokens elsewhere.
	 */
	public void loadNaturalMarking() {
		this.M.clear();
		for (Place p : this.getSourcePlaces()) {
			this.M.put(p,1);
		}
	}
	
	/**
	 * Changes marking of the net system to the given one. 
	 * Note that new marking must be associated with this net system. 
	 * 
	 * @param M Marking
	 */
	public void loadMarking(Marking M) {
		if (M.getPetriNet()!=this) return;
		
		this.M.clear();
		for (Map.Entry<Place,Integer> entry : M.entrySet()) {
			this.M.put(entry.getKey(),entry.getValue());
		}
	}
}
