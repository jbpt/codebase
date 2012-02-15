package org.jbpt.petri.unf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jbpt.petri.Flow;
import org.jbpt.petri.Node;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;


/**
 * Occurrence net
 * 
 * @author Artem Polyvyanyy
 */
public class OccurrenceNet extends PetriNet {
	private Unfolding unf = null;
	
	private Map<Transition,Event> t2e = new HashMap<Transition,Event>();
	private Map<Place,Condition>  p2c = new HashMap<Place,Condition>();
	private Map<Event,Transition> e2t = new HashMap<Event,Transition>();
	private Map<Condition,Place>  c2p = new HashMap<Condition,Place>();
	
	protected OccurrenceNet(Unfolding unf) {
		this.unf = unf;
		construct();
	}
	
	private void construct() {
		for (Event e : this.unf.getEvents()) {
			Transition t = new Transition(e.getName());
			this.addVertex(t);
			e2t.put(e,t);
			t2e.put(t,e);
		}
			
		for (Condition c : this.unf.getConditions()) {
			Place p = new Place(c.getName());
			this.addVertex(p);
			c2p.put(c,p);
			p2c.put(p,c);
		}
		
		for (Event e : this.unf.getEvents()) {
			for (Condition c : e.getPreConditions()) {
				this.addFlow(c2p.get(c), e2t.get(e));
			}
		}
		
		for (Condition c : this.unf.getConditions()) {
			this.addFlow(e2t.get(c.getPreEvent()),c2p.get(c));
		}	
	}
	
	public Unfolding getUnfolding() {
		return this.unf;
	}
	
	public PetriNet getOriginativeNet() {
		return this.unf.getNet();
	}
	
	public Event getEvent(Transition t) {
		return this.t2e.get(t);
	}
	
	public Condition getCondition(Place p) {
		return this.p2c.get(p);
	}
	
	public Place getPlace(Condition c) {
		return this.c2p.get(c);
	}
	
	public Transition getTransition(Event e) {
		return this.e2t.get(e);
	}
	
	public BPNode getUnfoldingNode(Node n) {
		if (n instanceof Place)
			return this.getCondition((Place) n);
		
		if (n instanceof Transition)
			return this.getEvent((Transition) n);
		
		return null;
	}
	
	public OrderingRelation getOrderingRelation(Node n1, Node n2) {
		BPNode bpn1 = this.getUnfoldingNode(n1);
		BPNode bpn2 = this.getUnfoldingNode(n2);
		
		if (bpn1!=null && bpn2!=null) 
			return this.unf.getOrderingRelation(bpn1,bpn2);
		
		return OrderingRelation.NONE;
	}
	
	public Set<Transition> getCutoffEvents() {
		Set<Transition> result = new HashSet<Transition>();
		for (Event e :this.unf.getCutoffEvents()) result.add(this.e2t.get(e));
		return result;
	}
	
	public Transition getCorrespondingEvent(Transition t) {
		return e2t.get(this.unf.getCorrespondingEvent(t2e.get(t)));
	}
	
	public boolean isCutoffEvent(Transition t) {
		return this.unf.isCutoffEvent(t2e.get(t));
	}
	
	public Set<Place> getCutInducedByLocalConfiguration(Transition t) {
		Set<Place> result = new HashSet<Place>();
		
		BPNode n = this.getUnfoldingNode(t);
		Event e = (Event) n;
		Cut cut = e.getLocalConfiguration().getCut();
		for (Condition c : cut) result.add(this.getPlace(c));
		
		return result;
		
	}
	
	public Collection<Cut> getCuts(Place p) {
		return this.unf.c2cut.get(this.getCondition(p));
	}

	@Override
	public String toDOT() {
		return this.toDOT(new ArrayList<Place>());
	}
	
	public String toDOTcs(Collection<Condition> cs) {
		if (cs == null) return "";
		Collection<Place> ps = new ArrayList<Place>();
		
		for (Condition c : cs) {
			ps.add(this.c2p.get(c));
		}
		
		return this.toDOT(ps);
	}
	
	public String toDOT(Collection<Place> ps) {
		if (ps == null) return "";
		
		String result = "digraph G {\n";
		result += "graph [fontname=\"Helvetica\" fontsize=10 nodesep=0.35 ranksep=\"0.25 equally\"];\n";
		result += "node [fontname=\"Helvetica\" fontsize=10 fixedsize style=filled penwidth=\"2\"];\n";
		result += "edge [fontname=\"Helvetica\" fontsize=10 arrowhead=normal color=black];\n";
		result += "\n";
		result += "node [shape=circle];\n";
		
		for (Place p : this.getPlaces()) {
			if (ps.contains(p))
				result += String.format("\tn%s[label=\"%s\" width=\".5\" height=\".5\" fillcolor=red];\n", p.getId().replace("-", ""), p.getName());
			else
				result += String.format("\tn%s[label=\"%s\" width=\".5\" height=\".5\" fillcolor=white];\n", p.getId().replace("-", ""), p.getName());
		}
			
		
		result += "\n";
		result += "node [shape=box];\n";
		
		for (Transition t : this.getTransitions()) {
			if (this.isCutoffEvent(t)) {
				if (t.getName()=="") result += String.format("\tn%s[label=\"%s\" width=\".5\" height=\".1\" fillcolor=orange];\n", t.getId().replace("-", ""), t.getName());
				else result += String.format("\tn%s[label=\"%s\" width=\".5\" height=\".5\" fillcolor=orange];\n", t.getId().replace("-", ""), t.getName());	
			}
			else {
				if (t.getName()=="") result += String.format("\tn%s[label=\"%s\" width=\".5\" height=\".1\" fillcolor=white];\n", t.getId().replace("-", ""), t.getName());
				else result += String.format("\tn%s[label=\"%s\" width=\".5\" height=\".5\" fillcolor=white];\n", t.getId().replace("-", ""), t.getName());
			}
		}
		
		result += "\n";
		for (Flow f: this.getFlowRelation()) {
			result += String.format("\tn%s->n%s;\n", f.getSource().getId().replace("-", ""), f.getTarget().getId().replace("-", ""));
		}
		
		result += "\tedge [fontname=\"Helvetica\" fontsize=8 arrowhead=normal color=orange];\n";
		for (Transition t : this.getCutoffEvents()) {
			result += String.format("\tn%s->n%s;\n", t.getId().replace("-", ""), this.getCorrespondingEvent(t).getId().replace("-", ""));
		}
		
		result += "}\n";
		
		return result;
	}
	
	@Override
	public OccurrenceNet clone() {
		OccurrenceNet clone = (OccurrenceNet) super.clone();
		
		clone.unf = this.unf;
		clone.c2p = new HashMap<Condition,Place>(this.c2p);
		clone.p2c = new HashMap<Place,Condition>(this.p2c);
		clone.t2e = new HashMap<Transition,Event>(this.t2e);
		clone.e2t = new HashMap<Event,Transition>(this.e2t);
		
		return clone;
	}
	
	/**
	 * Re-wire cutoff event
	 * @param cutoff cutoff event
	 */
	public void rewire(Transition cutoff) {
		Transition corr = this.getCorrespondingEvent(cutoff);
		if (corr == null) return;
		
		if (this.getPostset(cutoff).size()>1) {			
			Transition t = this.getPreset(this.getPreset(cutoff).iterator().next()).iterator().next();
			this.removeTransition(cutoff);
			this.removePlaces(this.getPostset(t));
			for (Place p : this.getPreset(corr)) this.addFlow(cutoff,p);
		}
		else {
			this.removePlaces(this.getPostset(cutoff));
			for (Place p : this.getPostset(corr)) this.addFlow(cutoff,p);
		}
	}
	
	/**
	 * Re-wire all cutoff events
	 */
	public void rewire() {
		for (Transition t : this.getCutoffEvents()) {
			this.rewire(t);
		}
	}
}
