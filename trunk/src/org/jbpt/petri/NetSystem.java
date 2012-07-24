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
public class NetSystem extends PetriNet implements INetSystem<IFlow<INode>,INode,IPlace,ITransition,IMarking<IPlace>> {
	
	protected Marking M = null;
	
	public NetSystem() {
		this.M = new Marking(this);
	}
	
	public NetSystem(PetriNet net) {
		this.M = new Marking(this);
	}
	
	@Override
	public INode removeNode(INode n) {
		INode result = super.removeNode(n);
		if (result!=null && n instanceof IPlace) {
			this.M.remove((IPlace)n);
		}
		return result;
	}

	@Override
	public Collection<INode> removeNodes(Collection<INode> ns) {
		Collection<INode> result = super.removeNodes(ns);
		if (result!=null) {
			for (INode n : result) {
				if (n instanceof IPlace)
					this.M.remove((IPlace)n);
			}
		}
		return result;
	}

	@Override
	public IPlace removePlace(IPlace p) {
		IPlace result = super.removePlace(p);
		if (result!=null) {
			this.M.remove(p);
		}
		return result;
	}

	@Override
	public Collection<IPlace> removePlaces(Collection<IPlace> ps) {
		Collection<IPlace> result = super.removePlaces(ps);
		if (result!=null) {
			for (IPlace p : result) {
				this.M.remove(p);
			}
		}
		return result;
	}

	@Override
	public Marking getMarking() {
		return this.M;
	}

	@Override
	public Set<IPlace> getMarkedPlaces() {
		return this.M.keySet();
	}

	@Override
	public Set<ITransition> getEnabledTransitions() {
		Set<ITransition> result = new HashSet<ITransition>();
		Set<IPlace> marked = new HashSet<IPlace>(this.getMarkedPlaces());
		
		for (ITransition t : this.getTransitions()) {
			if (marked.containsAll(this.getPreset(t)))
				result.add(t);
		}
		
		return result;
	}

	@Override
	public boolean isEnabled(ITransition t) {
		if (!this.getTransitions().contains(t)) return false;
		
		for (IPlace p : this.getPreset(t))
			if (!this.isMarked(p))
				return false;
			
		return true;
	}
	
	@Override
	public boolean isMarked(IPlace p) {
		return this.M.isMarked(p);
	}

	@Override
	public boolean fire(ITransition t) {
		if (!this.getTransitions().contains(t)) return false;
		
		if (!this.isEnabled(t)) return false;
		
		for (IPlace p : this.getPreset(t))
			this.M.put(p, this.M.get(p)-1);
		
		for (IPlace p : this.getPostset(t))
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
		
		for (IPlace p : this.getPlaces()) {
			Integer n = this.M.get(p);
			String label = ((n == 0) || (n == null)) ? p.getLabel() : p.getLabel() + "[" + n.toString() + "]"; 
			result += String.format("\tn%s[label=\"%s\" width=\".3\" height=\".3\"];\n", p.getId().replace("-", ""), label);
		}
		
		result += "\n";
		result += "node [shape=box];\n";
		
		for (ITransition t : this.getTransitions()) {
			String fillColor = this.isEnabled(t) ? " fillcolor=\"#9ACD32\"" : "";
			if (t.getName()=="")
				result += String.format("\tn%s[label=\"%s\" width=\".3\""+fillColor+" height=\".1\"];\n", t.getId().replace("-", ""), t.getName());
			else 
				result += String.format("\tn%s[label=\"%s\" width=\".3\""+fillColor+" height=\".3\"];\n", t.getId().replace("-", ""), t.getName());
		}
		
		result += "\n";
		for (IFlow<INode> f: this.getFlow()) {
			result += String.format("\tn%s->n%s;\n", f.getSource().getId().replace("-", ""), f.getTarget().getId().replace("-", ""));
		}
		result += "}\n";
		
		return result;
	}
	
	@Override
	public NetSystem clone() {
		Map<INode,INode> nodeMapping = new HashMap<INode,INode>();
		NetSystem clone = (NetSystem) super.clone(nodeMapping);
		cloneHelper(clone, nodeMapping);
		
		return clone;
	}
	
	@Override
	public NetSystem clone(Map<INode,INode> nodeMapping) {
		if (nodeMapping==null)
			nodeMapping = new HashMap<INode,INode>();
		
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
	private void cloneHelper(NetSystem clone, Map<INode,INode> nodeMapping) {
		// clone the marking 
		Marking cMarking = new Marking(clone);
		clone.M = cMarking;
		// initialise marking according to original net system
		for (IPlace p : this.getMarkedPlaces()) 
			clone.putTokens((IPlace) nodeMapping.get(p), this.getTokens(p));
	}

	@Override
	public Integer putTokens(IPlace p, Integer tokens) {
		return this.M.put(p, tokens);
	}
	

	@Override
	public Integer getTokens(IPlace p) {
		return this.M.get(p);
	}
	
	@Override
	public void loadNaturalMarking() {
		this.M.clear();
		for (IPlace p : this.getSourcePlaces()) {
			this.M.put(p,1);
		}
	}
	
	@Override
	public void loadMarking(IMarking<IPlace> newMarking) {
		if (newMarking.getPetriNet()!=this) return;
		
		this.M.clear();
		for (IPlace p : this.getPlaces())
			this.M.put(p,newMarking.get(p));
	}

	@Override
	public INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> createNetSystem() {
		return new NetSystem();
	}

}
