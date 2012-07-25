package org.jbpt.petri.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import org.jbpt.petri.Flow;
import org.jbpt.petri.IFlow;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPetriNet;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;
import org.jbpt.petri.Node;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;

public class PetriNetTransformations<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition> {

	public static String getId() {
		return UUID.randomUUID().toString();
	}
	
	public static Transition addTransition(PetriNet net) {
		Transition newT = net.createTransition();
		newT.setId(getId());
		net.addNode(newT);
		return newT;
	}
	
	public static Place addPlace(PetriNet net) {
		Place newP = net.createPlace();
		newP.setId(getId());
		net.addNode(newP);
		return newP;
	}
	
	public static void relinkIncomingArcs(PetriNet net, Node from, Node to) {
		for (Flow f : net.getFlow()) {
			if (f.getTarget().equals(from)) {
				net.addFlow(f.getSource(), to);
				net.removeEdge(f);
			}
		}
	}
	
	public static void relinkOutgoingArcs(PetriNet net, Node from, Node to) {
		for (Flow f : net.getFlow()) {
			if (f.getSource().equals(from)) {
				net.addFlow(to, f.getTarget());
				net.removeEdge(f);
			}
		}
	}
	
	public static void isolateTransitions(PetriNet net) {
		Collection<Transition> ts = new ArrayList<>(net.getTransitions());
		Iterator<Transition> transitions = ts.iterator();
		while (transitions.hasNext()) {
			Transition transition = transitions.next();
			
			if (net.getDirectPredecessors(transition).size() > 1) {
				Place newP = addPlace(net);
				Transition newT = addTransition(net);
				relinkIncomingArcs(net, transition, newT);

				net.addFlow(newT, newP);
				net.addFlow(newP, transition);
			}
			if (net.getDirectSuccessors(transition).size()>1) {
				Place newP = addPlace(net);
				Transition newT = addTransition(net);
				relinkOutgoingArcs(net, transition, newT);

				net.addFlow(transition, newP);
				net.addFlow(newP, newT);
			}
		}
	}
	
	/**
	 * T-restrict a given Petri net, i.e., add a single input/output place to transitions with empty preset/postset. 
	 * A net is T-restricted if presets and postsets of all transitions are not empty. 
	 */
	public void doTRestrict(IPetriNet<F,N,P,T> net) {
		for (T t : net.getSourceTransitions())
			net.addFlow(net.createPlace(), t);
		
		for (T t : net.getSinkTransitions())
			net.addFlow(t, net.createPlace());
	}

}
