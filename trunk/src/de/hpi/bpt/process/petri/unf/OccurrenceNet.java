package de.hpi.bpt.process.petri.unf;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gwt.dev.util.collect.HashSet;

import de.hpi.bpt.process.petri.Flow;
import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;

/**
 * Occurrence net
 * 
 * @author Artem Polyvyanyy
 */
public class OccurrenceNet extends PetriNet {	
	private Unfolding unf = null;
	
	private Map<Transition,Event> t2e = new HashMap<Transition,Event>();
	private Map<Place,Condition> p2c = new HashMap<Place,Condition>();
	private Map<Event,Transition> e2t = new HashMap<Event,Transition>();
	private Map<Condition,Place> c2p = new HashMap<Condition,Place>();
	
	protected OccurrenceNet(Unfolding unf) {
		this.unf = unf;
		construct();
	}
	
	private void construct() {
		for (Event e : this.unf.getEvents()) {
			Transition t = new Transition(e.getName());
			this.addVertex(t); // TODO
			e2t.put(e,t);
			t2e.put(t,e);
		}
			
		for (Condition c : this.unf.getConditions()) {
			Place p = new Place(c.getName());
			this.addVertex(p); // TODO
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
	
	public Event getEvent(Transition t) {
		return this.t2e.get(t);
	}
	
	public Condition getCondition(Place p) {
		return this.p2c.get(p);
	}
	
	private BPNode getUnfNode(Node n) {
		if (n instanceof Place)
			return this.getCondition((Place) n);
		
		if (n instanceof Transition)
			return this.getEvent((Transition) n);
		
		return null;
	}
	
	public OrderingRelation getOrderingRelation(Node n1, Node n2) {
		BPNode bpn1 = this.getUnfNode(n1);
		BPNode bpn2 = this.getUnfNode(n2);
		
		if (bpn1!=null && bpn2!=null) 
			this.unf.getOrderingRelation(bpn1,bpn2);
		
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
	
	public void toDOT(String fileName) throws FileNotFoundException {
		PrintStream out = new PrintStream(fileName);
		
		out.println("digraph G {");
		for (Transition t : this.getTransitions()) {
			if (this.isCutoffEvent(t))
				out.printf("\tn%s[shape=box,style=filled,fillcolor=orange,label=\"%s\"];\n", t.getId().replace("-", ""), t.getName());
			else
				out.printf("\tn%s[shape=box,label=\"%s\"];\n", t.getId().replace("-", ""), t.getName());	
		}
		for (Place n : this.getPlaces())
			out.printf("\tn%s[shape=circle,label=\"%s\"];\n", n.getId().replace("-", ""), n.getName());
		
		for (Flow f: this.getFlowRelation()) {
			out.printf("\tn%s->n%s;\n", f.getSource().getId().replace("-", ""), f.getTarget().getId().replace("-", ""));
		}
		
		out.print("\tedge [fontname=\"Helvetica\" fontsize=8 arrowhead=normal color=orange];\n");
		for (Transition t : this.getCutoffEvents()) {
			out.printf("\tn%s->n%s;\n", t.getId().replace("-", ""), this.getCorrespondingEvent(t).getId().replace("-", ""));
		}
		out.println("}");
		
		out.close();
	}
}
