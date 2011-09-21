package de.hpi.bpt.process.petri.unf;

import java.util.ArrayList;
import java.util.Collection;

import de.hpi.bpt.graph.algo.CombinationGenerator;
import de.hpi.bpt.graph.algo.DirectedGraphAlgorithms;
import de.hpi.bpt.process.petri.Flow;
import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;
import de.hpi.bpt.process.petri.unf.order.EsparzaTotalAdequateOrderForSafeSystems;

/**
 * Unfolding for soundness checks (multi-source-multi-sink nets)
 * 
 * Proof of concept - must be improved
 * 
 * @author Artem Polyvyanyy
 */
public class SoundUnfoldingMSMS extends SoundUnfolding {
	
	public SoundUnfoldingMSMS(PetriNet pn) {
		if (!pn.isFreeChoice()) return; // must be free choice new IllegalArgumentException ();
		DirectedGraphAlgorithms<Flow,Node> dga = new DirectedGraphAlgorithms<Flow,Node>();
		if (dga.hasCycles(pn)) return; // must be acyclic
		
		this.constructAugmentedNet(pn);
		this.totalOrderTs = new ArrayList<Transition>(this.net.getTransitions());
		
		UnfoldingSetup setup = new UnfoldingSetup();
		setup.ADEQUATE_ORDER = new EsparzaTotalAdequateOrderForSafeSystems();
		setup.MAX_BOUND	= Integer.MAX_VALUE;
		setup.MAX_EVENTS = Integer.MAX_VALUE;
		
		this.setup = setup;
		
		this.construct();
	}

	private void constructAugmentedNet(PetriNet net) {
		try { this.net = (PetriNet) net.clone(); } 
		catch (CloneNotSupportedException e) { e.printStackTrace(); }
		
		Collection<Place> sources = this.net.getSourcePlaces();
		Place s = new Place("s");
		this.net.addNode(s);
		for (int i=0; i<sources.size(); i++) {
			CombinationGenerator<Place> cg = new CombinationGenerator<Place>(sources, i+1);
			while (cg.hasMore()) {
				Collection<Place> comb = cg.getNextCombination();
				Transition t = new Transition();
				this.net.addNode(t);
				this.net.addFlow(s, t);
				for (Place p : comb) {
					this.net.addFlow(t, p);
				}
			}
		}
		
		Utils.addInitialMarking(this.net);
	}
}
