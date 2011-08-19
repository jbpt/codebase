package de.hpi.bpt.process.petri.unf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.hpi.bpt.process.petri.Place;

/**
 * Local configuration of an event in the branching process 
 * 
 * @author Artem Polyvyanyy
 */
public class LocalConfiguration extends HashSet<Event> {
	private static final long serialVersionUID = 1L;
	private Unfolding unf = null;
	private Event e = null;
	
	protected LocalConfiguration(Unfolding unf, Event e) {
		this.unf = unf;
		this.e = e;
		
		this.add(this.e);
		
		for (Condition c : this.e.getConditions()) {
			for (BPNode n : this.unf.ica.get(c)) {
				if (n instanceof Event)
					this.add((Event) n);
			}
		}
	}
	
	public Collection<Place> getMarking() {
		Collection<Place> result = new ArrayList<Place>();
		Set<Condition> cut = this.getCut();
		for (Condition c : cut) {
			result.add(c.getPlace());
		}
		
		return result;
	}
	
	public Set<Condition> getCut() {
		Set<Condition> result = new HashSet<Condition>();
		
		result.addAll(this.unf.initialBP);
		for (Event e : this) result.addAll(e.post);
		for (Event e : this) result.removeAll(e.getConditions());
		return result;
	}
}
