package org.jbpt.petri.structure;

import java.util.HashSet;
import java.util.Set;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPetriNet;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;

/**
 * Reduces a Petri net by removing places and transitions that are given as a projection set. 
 * The reduction is done in a way that the TAR (aka footprint, aka <code>RelSet</code> with 
 * lookahead 1) is preserved for the transitions of the projection set.
 * 
 * Reduction is defined in:
 * Johannes Prescher, Matthias Weidlich, and Jan Mendling. 
 * The Projected TAR and its Application to Conformance Checking.
 * Proceedings of EMISA, Vienna, Austria, September, 2012.
 * 
 * @author Johannes Prescher
 * @author Matthias Weidlich
 *
 * @param <F> Flow template.
 * @param <N> Node template.
 * @param <P> Place template.
 * @param <T> Transition template.
 */
public class PetriNetProjector<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition> {

	public void reducePetriNetBasedOnProjectionSet(IPetriNet<F, N, P, T> pn, Set<T> projectionSet) {
		
		Set<T> notInProjectionSet = new HashSet<>(pn.getTransitions());
		notInProjectionSet.removeAll(projectionSet);
		
		//Rule a)
		for (T t : notInProjectionSet) {
			Set<P> pre = pn.getPreset(t);

			if(pre.size() == 1) {
				P pre_p = (P) pre.iterator().next();
				Set<T> possiblyConcurrentTransitions = pn.getPostset(pre_p);
				if (possiblyConcurrentTransitions.size() < 2) 
					if (pn.getPostset(t).size() < 2)
						applyReductionRuleA(pn, t);
			}
		}	
		
		//Rule b)
		for (T t : notInProjectionSet) {
			Set<P> suc = pn.getPostset(t);
			if(suc.size() == 1){
				P p = (P) suc.iterator().next();
				Set<T> parallel = pn.getPreset(p);
				if(parallel.size() < 2){
					Set<T> sucTs = pn.getPostset(p);
					
					if(sucTs.size() == 1){
						applyReductionRuleB(pn, t);
					}
				}
			}
		}
		
		//Rule c)
		for (T t : notInProjectionSet) {
			Set<P> pre = pn.getPreset(t);
			if(pre.size() == 1){
				P p = (P) pre.iterator().next();
				Set<T> parallel = pn.getPostset(p);
				if(parallel.size() < 2){
					Set<T> preT = pn.getPreset(p);
					
					if(preT.size() == 1){
						Set<P> placeSet = pn.getPostset(preT.iterator().next());
						if(placeSet.size() < 2){
							applyReductionRuleC(pn, t);
						}
					}
				}
			}
		}
		
		
		//Rule d)
		Set<P> pToRemove = new HashSet<>();
		for (T t : notInProjectionSet) {
			//more than one place in postset?
			Set<P> suc_p = pn.getPostset(t);
			if(suc_p.size() > 1) {
				//more than one place with equal preset and postset?
				
				for (P node : suc_p) {
					Set<T> preset = pn.getPreset(node);
					Set<T> postset = pn.getPostset(node);
					
					if(preset.size() < 2 && postset.size() < 2) {
						for (P node2 : suc_p) {
							if(node.equals(node2))
								continue;
							Set<T> preset2 = pn.getPreset(node2);
							Set<T> postset2 = pn.getPostset(node2);
							if (preset.equals(preset2) && postset.equals(postset2)) {
								T postT = (T) postset2.iterator().next();
								if(notInProjectionSet.contains(postT)) 
									pToRemove.add(node2); 
							}
						}
					}
				}
			}
		}
		pn.removePlaces(pToRemove);
		
		//Rule e)
		Set<T> tToRemove = new HashSet<>();
		for (T t : notInProjectionSet) {
			Set<P> preset = pn.getPreset(t);
			Set<P> postset = pn.getPostset(t);
			
			if(preset.size() == 1 && postset.size() == 1) {
				P pre = (P) preset.iterator().next(); 
				Set<T> parallelTs = pn.getPostset(pre); 
				
				for (T parallelT : parallelTs) {
					if(t != parallelT && notInProjectionSet.contains(parallelT)) {
						Set<P> preset2 = pn.getPreset(parallelT);
						Set<P> postset2 = pn.getPostset(parallelT);
						if (preset.equals(preset2) && postset.equals(postset2))
							tToRemove.add(parallelT);
					}
				}
			}
		}
		pn.removeTransitions(tToRemove);
					
		//Rule f)
		tToRemove = new HashSet<>();
		for (T t : notInProjectionSet) {
			Set<P> pre = pn.getPreset(t);
			Set<P> suc = pn.getPostset(t);

			if(pre.containsAll(suc) && suc.containsAll(pre))
					tToRemove.add(t);
		}
		pn.removeTransitions(tToRemove);
		
		//Rule g)
		pToRemove = new HashSet<>();
		for (P p : pn.getPlaces()){
			Set<T> preTs = pn.getPreset(p);
			Set<T> sucTs = pn.getPostset(p);

			if (preTs.containsAll(sucTs) && sucTs.containsAll(preTs))
				pToRemove.add(p); 	
			
		}
		pn.removePlaces(pToRemove);
	}
	
	private void applyReductionRuleA(IPetriNet<F, N, P, T> pn, T t){
		P pre = pn.getPreset(t).iterator().next(); 
		Set<T> pre_t = pn.getPreset(pre); 
			
		P suc = pn.getPostset(t).iterator().next(); 
		for (T node : pre_t) 
			pn.addFlow(node, suc); 
		
		pn.removePlace(pre);
		pn.removeTransition(t);
	}
	
	private void applyReductionRuleB(IPetriNet<F, N, P, T> pn, T t){
		Set<P> prePlaces = pn.getPreset(t); 
		P sucPlace = pn.getPostset(t).iterator().next(); 
		T sucT = (T) pn.getPostset(sucPlace).iterator().next(); 
		
		for (P node : prePlaces)
			pn.addFlow(node, sucT);
		
		pn.removeTransition(t);
		pn.removePlace(sucPlace);
	}
	
	private void applyReductionRuleC(IPetriNet<F, N, P, T> pn, T t){
		P prePlace = pn.getPreset(t).iterator().next(); 
		T preT = pn.getPreset(prePlace).iterator().next(); 
		Set<P> sucPlaces = pn.getPostset(t);
		
		for (P node : sucPlaces) 
			pn.addFlow(preT, node);
			
		pn.removeTransition(t);
		pn.removePlace(prePlace);
	}
	
	
}