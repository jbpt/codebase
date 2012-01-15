package de.hpi.bpt.process.petri.unf;

import java.util.Comparator;

public class CosetComparator implements Comparator<Condition> {

	@Override
	public int compare(Condition c1, Condition c2) {
		int c = c1.getPlace().compareTo(c2.getPlace());
		if (c==0) 
			return c1.getPreEvent().getTransition().compareTo(c2.getPreEvent().getTransition());
		
		return c;
	}
	
}
