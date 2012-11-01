package org.jbpt.algo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Given a list of lists of objects generate combinations of one element from each list.
 * 
 * @author Frank Böhr
 * @author Artem Polyvyanyy
 */
public class ListCombinationGenerator<X> {
	
	private List<List<X>> listOfLists = null;
	private List<Pair> currentCombination = new LinkedList<Pair>();
	private boolean hasMoreCombinations = true;
	
	/**
	 * Constructor.
	 * 
	 * @param listOfLists List of list of objects to use when generating combinations.
	 */
	public ListCombinationGenerator(List<List<X>> listOfLists) {
		this.listOfLists = listOfLists;

		Iterator<List<X>> iter = this.listOfLists.iterator();
		while(iter.hasNext()){
			List<X> currentSubList = iter.next();
			Pair p = new Pair(1,currentSubList.size());
			currentCombination.add(p);
		}
	}
	
	public List<X> getNextCombination() {
		List<X> result = new ArrayList<X>();
		Iterator<Pair> currentCombinationIterator = this.getCurrentCombination().iterator();
		Iterator<List<X>> uncombinedListIterator = this.listOfLists.iterator();
		
		while(currentCombinationIterator.hasNext() && uncombinedListIterator.hasNext()){
			Pair currentPair = currentCombinationIterator.next();
			List<X> currentSublist = uncombinedListIterator.next();
			result.add(currentSublist.get(currentPair.getCurrentValue()-1));
		}
	
		int overflow = 1;
		
		Iterator<Pair> iter = this.getCurrentCombination().iterator();
		while(iter.hasNext()) {
			Pair currentPair = iter.next();
			if((currentPair.getCurrentValue()+overflow)>currentPair.getMaxValue()){
				overflow = (currentPair.getCurrentValue()+overflow) - currentPair.getMaxValue();
				currentPair.setCurrentValue(1);
			}
			else {
				currentPair.setCurrentValue(currentPair.getCurrentValue()+1);
				overflow=0;
				break;
			}
		}
		
		if(overflow!=0){
			this.setHasMoreCombinations(false);
		}
		
		return result;
	}
	
	private List<Pair> getCurrentCombination() {
		return this.currentCombination;
	}
	
	
	public boolean hasMoreCombinations() {
		return this.hasMoreCombinations;
	}
	
	private void setHasMoreCombinations(boolean hasMoreCombinations) {
		this.hasMoreCombinations = hasMoreCombinations;
	}
		
	private class Pair {
		private int currentValue = 1;
		private int maxValue = 1;
		
		public Pair(int currentValue, int maxValue){
			this.currentValue = currentValue;
			this.maxValue=maxValue;
		}
		
		public int getCurrentValue() {
			return this.currentValue;
		}
		
		public void setCurrentValue(int currentValue) {
			this.currentValue = currentValue;
		}
		
		public int getMaxValue() {
			return this.maxValue;
		}
	}
}