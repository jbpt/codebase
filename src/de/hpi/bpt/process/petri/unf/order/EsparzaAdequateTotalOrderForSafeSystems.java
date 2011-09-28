package de.hpi.bpt.process.petri.unf.order;

import java.util.List;
import java.util.Set;

import de.hpi.bpt.process.petri.Transition;
import de.hpi.bpt.process.petri.unf.Event;
import de.hpi.bpt.process.petri.unf.LocalConfiguration;

/**
 * Esparza adequate order for 1-safe systems (total order)
 * 
 * Javier Esparza, Stefan Roemer, Walter Vogler: An Improvement of McMillan's Unfolding Algorithm. Formal Methods in System Design (FMSD) 20(3):285-310 (2002)
 * 
 * @author Artem Polyvyanyy
 */
public class EsparzaAdequateTotalOrderForSafeSystems extends AdequateOrder {
	
	@Override
	public boolean isSmaller(LocalConfiguration lc1, LocalConfiguration lc2) {
		if (lc1.size() < lc2.size()) return true;
		
		int comp = this.compareQuasiParikhVectors(lc1, lc2);
		if ((lc1.size()==lc2.size()) && comp<0) return true;
		
		int comp2 = this.compareFoataNormalForms(lc1, lc2);
		if (comp==0 && comp2<0) return true;
		
		return false;	
	}
	
	private int compareFoataNormalForms(LocalConfiguration lc1, LocalConfiguration lc2) {
		List<Set<Event>> foata1 = lc1.getFoataNormalForm();
		List<Set<Event>> foata2 = lc2.getFoataNormalForm();
		
		for (int i=0; i<foata1.size(); i++) {
			boolean flag = true;
			for (int j=0; j<i; j++) {
				List<Transition> pvec1 = lc1.getQuasiParikhVector(foata1.get(j));
				List<Transition> pvec2 = lc2.getQuasiParikhVector(foata2.get(j));
				
				int comp = compareQuasiParikhVectors(lc1,pvec1,pvec2);
				if (comp!=0) {
					flag = false;
					break;
				}
			}
			
			if (flag) {
				List<Transition> pvec1 = lc1.getQuasiParikhVector(foata1.get(i));
				List<Transition> pvec2 = lc2.getQuasiParikhVector(foata2.get(i));
				int comp = compareQuasiParikhVectors(lc1,pvec1,pvec2);
				if (comp==0) return -1;
			}
		}
		
		return 0;
	}

	
	/**
	 * Lexicographically compare two quasi Parikh vectors 
	 * @param lc local configuration (to keep a link to the total order of transitions)
	 * @param pvec1 vector
	 * @param pvec2 vector
	 * @return -1,0,1 if pvec1 is smaller, equal, or larger than pvec2, respectively
	 */
	private int compareQuasiParikhVectors(LocalConfiguration lc, List<Transition> pvec1, List<Transition> pvec2) {
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
	 * Lexicographically compare two quasi Parikh vectors 
	 * @param lc local configuration (to keep a link to the total order of transitions)
	 * @param pvec1 vector
	 * @param pvec2 vector
	 * @return -1,0,1 if pvec1 is smaller, equal, or larger than pvec2, respectively
	 */
	private int compareQuasiParikhVectors(LocalConfiguration lc1, LocalConfiguration lc2) {
		List<Transition> pvec1 = lc1.getQuasiParikhVector();
		List<Transition> pvec2 = lc2.getQuasiParikhVector();
		
		return this.compareQuasiParikhVectors(lc1, pvec1, pvec2);
	}
}
