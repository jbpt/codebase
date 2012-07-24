package org.jbpt.petri.behavior;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jbpt.algo.graph.TransitiveClosure;
import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.petri.unfolding.SoundUnfoldingMSMS;

/**
 * This class implements completion methods for multi-terminal nets described in:
 * - Artem Polyvyanyy, Luciano García-Bañuelos, Marlon Dumas: Structuring acyclic process models. Inf. Syst. 37(6): 518-538 (2012).
 * - Bartek Kiepuszewski, Arthur H. M. ter Hofstede, Wil M. P. van der Aalst: Fundamentals of control flow in workflows. Acta Inf. (ACTA) 39(3):143-209 (2003)
 *
 * @author Artem Polyvyanyy
 */
public class Completion {
	
	/**
	 * This completion method is based on the technique described in: 
	 * Artem Polyvyanyy, Luciano García-Bañuelos, Marlon Dumas: Structuring acyclic process models. Inf. Syst. 37(6): 518-538 (2012).
	 * 
	 * @TODO Move this method on the level of interfaces.
	 * 
	 * @assumption A given net is multi-terminal.
	 * @assumption A given net is T-restricted.
	 * @assumption A given net is free-choice.
	 * @assumption A given net is acyclic.
	 * @assumption A given net is sound.
	 */
	public void completeSources(INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> sys) {
		if (sys.getSourcePlaces().size()==1) return;
		
		sys.loadNaturalMarking();
		SoundUnfoldingMSMS unf = new SoundUnfoldingMSMS(sys);
		unf.completeOriginativeSystemWithCorrectInstantiations();
		sys.loadNaturalMarking();
	}
	
	/**
	 * This completion method is based on the technique described in: 
	 * Bartek Kiepuszewski, Arthur H. M. ter Hofstede, Wil M. P. van der Aalst: Fundamentals of control flow in workflows. Acta Inf. (ACTA) 39(3):143-209 (2003). 
	 * See proof of Theorem 5.1.
	 * 
	 * @TODO Move this method on the level of interfaces.
	 * 
	 * @assumption A given net is is single-source-multi-sink.
	 * @assumption A given net is T-restricted.
	 * @assumption A given net is free-choice.
	 * @assumption A given net is acyclic.
	 * @assumption A given net is sound.
	 */
	public void completeSinks(INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> sys) {
		TransitiveClosure<IFlow<INode>,INode> tc = new TransitiveClosure<>(sys);
		Map<IPlace,Set<IPlace>> p2ps = new HashMap<>();
		Map<IPlace,Set<ITransition>> p2ts = new HashMap<>();
		
		for (IPlace p : sys.getPlaces()) {
			Set<IPlace> set = new HashSet<>();
			for (IPlace pp : sys.getPlaces()) {
				if (tc.hasPath(pp,p))
					set.add(pp);
				
			}
			p2ps.put(p,set);
		}
		
		for (IPlace p : sys.getPlaces()) {
			Set<ITransition> set = new HashSet<>();
			Set<IPlace> ps = p2ps.get(p);
			
			for (ITransition tt : sys.getTransitions()) {
				if (ps.containsAll(sys.getPreset(tt)) && this.areDisjoint(ps,sys.getPostset(tt))) {
					set.add(tt);
				}
			}
			
			p2ts.put(p,set);
		}
		
		for (IPlace p : sys.getSinkPlaces()) {
			for (ITransition t : p2ts.get(p))
				sys.addFlow(t,p);
		}
		
		Transition t = new Transition();
		for (IPlace p : sys.getSinkPlaces()) sys.addFlow(p,t);
		Place p = new Place();
		sys.addFlow(t,p);
	}
	
	private boolean areDisjoint(Set<IPlace> ps, Collection<IPlace> postset) {
		for (IPlace p : postset)
			if (ps.contains(p))
					return false;
			
		return true;
	}

	public void complete(INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> sys) {
		this.completeSources(sys);
		this.completeSinks(sys);
	}
}
