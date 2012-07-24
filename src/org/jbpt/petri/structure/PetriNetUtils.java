package org.jbpt.petri.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPetriNet;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;

public class PetriNetUtils {

	public static String getId() {
		return UUID.randomUUID().toString();
	}
	
	public static ITransition addTransition(IPetriNet<IFlow<INode>, INode, IPlace, ITransition> net) {
		ITransition newT = net.createTransition();
		newT.setId(getId());
		net.addNode(newT);
		return newT;
	}
	
	public static IPlace addPlace(IPetriNet<IFlow<INode>, INode, IPlace, ITransition> net) {
		IPlace newP = net.createPlace();
		newP.setId(getId());
		net.addNode(newP);
		return newP;
	}
	
	public static void relinkIncomingArcs(IPetriNet<IFlow<INode>, INode, IPlace, ITransition> net, INode from, INode to) {
		for (IFlow<INode> f : net.getFlow()) {
			if (f.getTarget().equals(from)) {
				net.addFreshFlow(f.getSource(), to);
				net.removeEdge(f);
			}
		}
	}
	
	public static void relinkOutgoingArcs(IPetriNet<IFlow<INode>, INode, IPlace, ITransition> net, INode from, INode to) {
		for (IFlow<INode> f : net.getFlow()) {
			if (f.getSource().equals(from)) {
				net.addFreshFlow(to, f.getTarget());
				net.removeEdge(f);
			}
		}
	}
	
	public static void isolateTransitions(IPetriNet<IFlow<INode>, INode, IPlace, ITransition> net) {
		Collection<ITransition> ts = new ArrayList<>(net.getTransitions());
		Iterator<ITransition> transitions = ts.iterator();
		while (transitions.hasNext()) {
			ITransition transition = transitions.next();
			
			if (net.getDirectPredecessors(transition).size() > 1) {
				IPlace newP = addPlace(net);
				ITransition newT = addTransition(net);
				relinkIncomingArcs(net, transition, newT);

				net.addFlow(newT, newP);
				net.addFlow(newP, transition);
			}
			if (net.getDirectSuccessors(transition).size()>1) {
				IPlace newP = addPlace(net);
				ITransition newT = addTransition(net);
				relinkOutgoingArcs(net, transition, newT);

				net.addFlow(transition, newP);
				net.addFlow(newP, newT);
			}
		}
	}

}
