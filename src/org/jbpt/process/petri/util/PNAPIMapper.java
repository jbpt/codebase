package de.hpi.bpt.process.petri.util;

import hub.top.petrinet.PetriNet;

import java.util.HashMap;
import java.util.Map;

import de.hpi.bpt.process.petri.Flow;
import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;

public class PNAPIMapper {
	
	public static PetriNet jBPT2PNAPI(de.hpi.bpt.process.petri.PetriNet net) {
		PetriNet result = new PetriNet();
		
		for (Place p : net.getPlaces()) {
			result.addPlace(p.getId());
			if (p.getTokens() > 0)
				result.setTokens(p.getId(), p.getTokens());
		}
			
		for (Transition t : net.getTransitions())
			result.addTransition(t.getId());
			
		for (Flow f : net.getFlowRelation())
			result.addArc(f.getSource().getId(),f.getTarget().getId());
		
		return result;
	}

	public static de.hpi.bpt.process.petri.PetriNet PNAPI2jBPT(PetriNet net) {
		de.hpi.bpt.process.petri.PetriNet result = new de.hpi.bpt.process.petri.PetriNet();
		
		Map<String,Node> nodes = new HashMap<String, Node>();
		
		for (hub.top.petrinet.Place p : net.getPlaces()) {
			Place n = new Place();
			n.setId(p.getUniqueIdentifier());
			n.setName(p.getName());
			result.addNode(n);
			n.setTokens(p.getTokens());
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
