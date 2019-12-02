package org.jbpt.pm.utils;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jbpt.petri.Flow;
import org.jbpt.petri.IPetriNet;
import org.jbpt.petri.Marking;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;

import dk.brics.automaton.*;
import gnu.trove.map.TObjectShortMap;

/**
 * Utils for object transformations
 * 
 * @author akalenkova
 *
 */
public class Utils {

	private final static String TAU = "tau";
	
	/**
	 * Construct reachability graph of a given net system
	 * Note that we assume that we have checked that the net system is bounded
	 * 
	 * @param ns
	 * @return
	 */
	public static Automaton constructAutomatonFromNetSystem(NetSystem ns, TObjectShortMap<String> activity2short) {
		Map<Collection<Place>, State> markingToState = new HashMap<Collection<Place>, State>();
		Set<Collection<Place>> unprocessedMarkings = new HashSet<Collection<Place>>();
		
		Automaton a = new Automaton();
		boolean containsTauLabels = false;
		
		// Construct initial state
		Collection<Place> initialMarking = ns.getMarking().toMultiSet();
		
		// Derive final marking
		Collection<Place> finalMarking = deriveFinalMarking(ns);

		State initialState = new State();
		markingToState.put(initialMarking, initialState);
		a.setInitialState(initialState);
		unprocessedMarkings.add(initialMarking);
		
		// Pair of states connected by tau
		Set<StatePair> tauPairs = new HashSet<StatePair>();
		
		// Construct other states
		while (!unprocessedMarkings.isEmpty()) {
			Collection<Place> curMarking = unprocessedMarkings.iterator().next();
			Set<Transition> enabledTransitions = retrieveEnabledTransitions(curMarking, ns);

			for (Transition enabeledTransition : enabledTransitions) {

				Marking marking = new Marking(ns);
				marking.fromMultiSet(curMarking);
				ns.loadMarking(marking);

				ns.fire(enabeledTransition);

				Collection<Place> newMarking = ns.getMarking().toMultiSet();
				State curState = markingToState.get(curMarking);
				State newState = markingToState.get(newMarking);

				if (newState == null) {
					newState = new State();
					markingToState.put(newMarking, newState);
					unprocessedMarkings.add(newMarking);

					if (newMarking.containsAll(finalMarking) && finalMarking.containsAll(newMarking)) {
						newState.setAccept(true);
					}
				}
				
				char c = (char) Integer.valueOf(enabeledTransition.getLabel().hashCode()).shortValue();
				// If string is empty (silent)
				if (c == '\u0000') {
					tauPairs.add(new StatePair(curState, newState));
				} else {
					if(enabeledTransition.getLabel().contains(TAU)) {
						containsTauLabels = true;
					}
					activity2short.putIfAbsent(enabeledTransition.getLabel(), (short) activity2short.size());
					dk.brics.automaton.Transition t = new dk.brics.automaton.Transition((char)activity2short.get(enabeledTransition.getLabel()), newState);
					curState.addTransition(t);
				}

			}
			unprocessedMarkings.remove(curMarking);
		}
		
		if(containsTauLabels) {
			System.out.println("Note that some transitions contain labels with " + TAU + ". These transitions will not be considered as silent. "
					+ "To make a transition silent please set an empty label.");
		}
		
		a.addEpsilons(tauPairs);
		a.determinize();
		a.minimize();
		return a;
	}
	
	/**
	 * Construct prefix tree for a given event log
	 * 
	 * @param ns
	 * @return
	 */
	public static Automaton constructAutomatonFromLog(XLog log, TObjectShortMap<String> activity2short) {
		
		Automaton a = new Automaton();
		State initialState = new State();
		a.setInitialState(initialState);
		
		Iterator<XTrace> logIterator = log.iterator();
		
		while (logIterator.hasNext()) {
			State curState = initialState;
			XTrace trace = logIterator.next();
			Iterator<XEvent> traceIterator = trace.iterator();
			while (traceIterator.hasNext()) {
				XEvent event = traceIterator.next();
				XAttribute attribute = event.getAttributes().get("concept:name");
				if (attribute != null) {
					String label = attribute.toString();
					activity2short.putIfAbsent(label, (short) activity2short.size());
					char c = (char)activity2short.get(label);
					boolean alreadyConstructed = false;
					Set<dk.brics.automaton.Transition> transitions = curState.getTransitions();
					for (dk.brics.automaton.Transition transition : transitions) {
						if ((transition.getMax() == c) && (transition.getMin() == c)) {
							curState = transition.getDest();
							alreadyConstructed = true;
						}
					}
					if (!alreadyConstructed) {
						State newState = new State();
						dk.brics.automaton.Transition t = new dk.brics.automaton.Transition(c, newState);
						curState.addTransition(t);
						curState = newState;
					}
				}
			}
			curState.setAccept(true);
		}
		return a;
	}
	
	/**
	 * Constructing system net from a Petri net
	 * 
	 * @param pn
	 * @return
	 */
	public static NetSystem constructNetSystemFromPetrinet(Petrinet pn) {
		
		Map<org.processmining.models.graphbased.directed.petrinet.elements.Place, Place> placesMap 
			= new HashMap<org.processmining.models.graphbased.directed.petrinet.elements.Place, Place>();
		Map<org.processmining.models.graphbased.directed.petrinet.elements.Transition, Transition> transitionsMap 
			= new HashMap<org.processmining.models.graphbased.directed.petrinet.elements.Transition, Transition>();
		
		NetSystem ns = new NetSystem();
		for (org.processmining.models.graphbased.directed.petrinet.elements.Place place : pn.getPlaces()) {
			Place newPlace = new Place();
			placesMap.put(place, newPlace);
			ns.addPlace(newPlace);
		}
		for (org.processmining.models.graphbased.directed.petrinet.elements.Transition transition : pn.getTransitions()) {
			Transition newTransition = new Transition("", transition.getLabel());
			if(transition.isInvisible()) {
				newTransition.setLabel("");
			}
			transitionsMap.put(transition, newTransition);
			ns.addTransition(newTransition);
		}
		for(PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : pn.getEdges()) {
			Object source = edge.getSource();
			Object target = edge.getTarget();
			if ((source instanceof org.processmining.models.graphbased.directed.petrinet.elements.Place) 
				&& (target instanceof org.processmining.models.graphbased.directed.petrinet.elements.Transition)) {
				ns.addFlow(placesMap.get((org.processmining.models.graphbased.directed.petrinet.elements.Place)source),
						transitionsMap.get((org.processmining.models.graphbased.directed.petrinet.elements.Transition)target));
			}
			if ((target instanceof org.processmining.models.graphbased.directed.petrinet.elements.Place) 
					&& (source instanceof org.processmining.models.graphbased.directed.petrinet.elements.Transition)) {
					ns.addFlow(transitionsMap.get((org.processmining.models.graphbased.directed.petrinet.elements.Transition)source),
							placesMap.get((org.processmining.models.graphbased.directed.petrinet.elements.Place)target));
				}
		}
		return ns;
	}
	
	/**
	 * Construct enabled transitions for the current marking
	 * 
	 * @param marking
	 * @return
	 */
	private static Set<Transition> retrieveEnabledTransitions(Collection<Place> marking, NetSystem net) {

		Set<Transition> enabledTransitions = new HashSet<Transition>();

		IPetriNet<Flow, Node, Place, Transition> petriNet = net;
		for (Transition transition : petriNet.getTransitions()) {
			boolean isEnabled = true;
			for (Flow inFlow : petriNet.getIncomingEdges(transition)) {
				Place inPlace = (Place)inFlow.getSource();
				if (!marking.contains(inPlace)) {
					isEnabled = false;
					break;
				}
			}
			if(isEnabled) {
				enabledTransitions.add(transition);
			}
		}
		return enabledTransitions;
	}
	
//	/**
//	 * Derive initial marking from the structure of a given net system
//	 * (should work for workflow nets, for other types of nets results are to be checked)
//	 * 
//	 * @param ns
//	 * @return
//	 */
//	private static Marking deriveInitialMarking(NetSystem ns) {
//
//		Marking initialMarking = new Marking();
//		if (ns != null) {
//			for (Place place : ns.getPlaces()) {
//				Collection<Node> predsessors = ns.getDirectPredecessors(place);
//				if ((predsessors == null) || (predsessors.size() == 0)) {
//					initialMarking.put(place, 1);
//				} else {
//					initialMarking.put(place, 0);
//				}
//			}
//		}
//		return initialMarking;
//	}
	
	
	/**
	 * Derive final marking from the structure of a given net system
	 * (should work for workflow nets, for other types of nets results are to be checked)
	 * 
	 * @param ns
	 * @return
	 */
	private static Collection<Place> deriveFinalMarking(NetSystem ns) {

		Collection<Place> finalMarking = new HashSet<Place>();
		if (ns != null) {
			for (Place place : ns.getPlaces()) {
				Collection<Node> successors = ns.getDirectSuccessors(place);
				if ((successors == null) || (successors.size() == 0)) {
					finalMarking.add(place);
				} 
			}
		}
		return finalMarking;
	}
	
	private static PrintStream originalStream = System.out;

	private static PrintStream dummyStream = new PrintStream(new OutputStream(){
	    public void write(int b) {
	    	// Nothing
	    }
	});

	public static void hidePrinting() {
		System.setOut(dummyStream);
	}
	
	public static void restorePrinting() {
		System.setOut(originalStream);
	}
	
	
	public static String numberOfTransitions(Automaton a) {
		int c = 0;
		for (State s : a.getStates()) {
			for (dk.brics.automaton.Transition  t : s.getTransitions()) {
				char max = t.getMax();
				char min = t.getMin();
				int numberOfChars = max - min + 1;
				c += numberOfChars;
			}
		}
		return Integer.toString(c);
	}
}
