package org.jbpt.petri.unfolding.order;

import java.util.List;
import java.util.Set;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;
import org.jbpt.petri.unfolding.IBPNode;
import org.jbpt.petri.unfolding.ICondition;
import org.jbpt.petri.unfolding.IEvent;
import org.jbpt.petri.unfolding.ILocalConfiguration;


/**
 * Esparza adequate order for 1-safe systems (a total order).
 * 
 * Javier Esparza, Stefan Roemer, Walter Vogler: An Improvement of McMillan's Unfolding Algorithm. Formal Methods in System Design (FMSD) 20(3):285-310 (2002)
 * 
 * @author Artem Polyvyanyy
 */
public class EsparzaAdequateTotalOrderForSafeSystems<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>>
	extends AdequateOrder<BPN,C,E,F,N,P,T,M> {
	
	@Override
	public boolean isSmaller(ILocalConfiguration<BPN,C,E,F,N,P,T,M> lc1, ILocalConfiguration<BPN,C,E,F,N,P,T,M> lc2) {
		if (lc1.size() < lc2.size()) return true;
		
		int comp = this.compareQuasiParikhVectors(lc1, lc2);
		if ((lc1.size()==lc2.size()) && comp<0) return true;
		
		int comp2 = this.compareFoataNormalForms(lc1, lc2);
		if (comp==0 && comp2<0) return true;
		
		return false;	
	}
	
	private int compareFoataNormalForms(ILocalConfiguration<BPN,C,E,F,N,P,T,M> lc1, ILocalConfiguration<BPN,C,E,F,N,P,T,M> lc2) {
		List<Set<E>> foata1 = lc1.getFoataNormalForm();
		List<Set<E>> foata2 = lc2.getFoataNormalForm();
		
		for (int i=0; i<foata1.size(); i++) {
			boolean flag = true;
			for (int j=0; j<i; j++) {
				List<T> pvec1 = lc1.getQuasiParikhVector(foata1.get(j));
				List<T> pvec2 = lc2.getQuasiParikhVector(foata2.get(j));
				
				int comp = compareQuasiParikhVectors(lc1,pvec1,pvec2);
				if (comp!=0) {
					flag = false;
					break;
				}
			}
			
			if (flag) {
				List<T> pvec1 = lc1.getQuasiParikhVector(foata1.get(i));
				List<T> pvec2 = lc2.getQuasiParikhVector(foata2.get(i));
				int comp = compareQuasiParikhVectors(lc1,pvec1,pvec2);
				if (comp==0) return -1;
			}
		}
		
		return 0;
	}

	
	/**
	 * Lexicographically compare two quasi Parikh vectors.
	 * 
	 * @param lc A local configuration (gives a link to the total order of transitions).
	 * @param pvec1 A list of transitions.
	 * @param pvec2 A list of transitions.
	 * @return -1,0,1 if 'pvec1' is smaller, equal, or larger than 'pvec2', respectively.
	 */
	private int compareQuasiParikhVectors(ILocalConfiguration<BPN,C,E,F,N,P,T,M> lc, List<T> pvec1, List<T> pvec2) {
		int n = pvec1.size();
		if (pvec2.size()<n) n = pvec2.size();
		
		for (int i = 0; i<n; i++) {
			Integer comp = lc.compareTransitions(pvec1.get(i), pvec2.get(i));
			if (comp<0) return -1;
			if (comp>0) return 1;
		}
		
		if (pvec1.size()==pvec2.size()) return 0;
		if (pvec1.size()<pvec2.size()) return -1;
		return 1;
	}

	/**
	 * Lexicographically compare two quasi Parikh vectors.
	 * 
	 * @param lc A local configuration (gives a link to the total order of transitions).
	 * @param pvec1 A list of transitions.
	 * @param pvec2 A list of transitions.
	 * @return -1,0,1 if 'pvec1' is smaller, equal, or larger than 'pvec2', respectively.
	 */
	private int compareQuasiParikhVectors(ILocalConfiguration<BPN,C,E,F,N,P,T,M> lc1, ILocalConfiguration<BPN,C,E,F,N,P,T,M> lc2) {
		List<T> pvec1 = lc1.getQuasiParikhVector();
		List<T> pvec2 = lc2.getQuasiParikhVector();
		
		return this.compareQuasiParikhVectors(lc1, pvec1, pvec2);
	}
}
