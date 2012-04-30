package org.jbpt.petri.util;

import hub.top.petrinet.PetriNet;

import java.util.HashMap;
import java.util.Map;

import org.jbpt.petri.Flow;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;


public class PNAPIMapper {
	
	public static PetriNet jBPT2PNAPI(org.jbpt.petri.NetSystem net) {
		PetriNet result = new PetriNet();
		
		for (Place p : net.getPlaces()) {
			result.addPlace(p.getId());
			if (net.getMarking().get(p) > 0)
				result.setTokens(p.getId(), net.getMarking().get(p));
		}
			
		for (Transition t : net.getTransitions())
			result.addTransition(t.getId());
			
		for (Flow f : net.getFlow())
			result.addArc(f.getSource().getId(),f.getTarget().getId());
		
		return result;
	}

	public static org.jbpt.petri.NetSystem PNAPI2jBPT(PetriNet net) {
		org.jbpt.petri.NetSystem result = new org.jbpt.petri.NetSystem();
		
		Map<String,Node> nodes = new HashMap<String, Node>();
		
		for (hub.top.petrinet.Place p : net.getPlaces()) {
			Place n = new Place();
			n.setId(p.getUniqueIdentifier());
			n.setName(p.getName());
			result.addNode(n);
			result.getMarking().put(n,p.getTokens());
			nodes.put(p.getUniqueIdentifier(),n);
		}
			
		for (hub.top.petrinet.Transition t : net.getTransitions()) {
			Transition n = new Transition();
			n.setId(t.getUniqueIdentifier());
			n.setName(t.getName());
			result.addNode(n);
			nodes.put(t.getUniqueIdentifier(),n);
		}
			
		for (hub.top.petrinet.Arc a : net.getArcs()) {
			result.addFlow(nodes.get(a.getSource().getUniqueIdentifier()),nodes.get(a.getTarget().getUniqueIdentifier()));
		}
		
		return result;
	}

}
