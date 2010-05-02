/**
 * Copyright (c) 2010 Artem Polyvyanyy
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.hpi.bpt.process.petri.unf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;

public class Unfolding {
	PetriNet net = null;
	PetriNet unf_net = null;
	
	Collection<ProcessItem> unf = new ArrayList<ProcessItem>();
	Map<Node,ProcessItem> unf2net = new HashMap<Node,ProcessItem>();
	
	public Unfolding(PetriNet pn) {
		this.net = pn;	
		
		this.construct();
	}
	
	private void construct() {
		if (this.net==null) return;
		
		// initialize
		unf.clear();
		unf2net.clear();
		unf_net = new PetriNet();
		
		for (Place i : this.net.getSourcePlaces()) {
			Condition c = new Condition(i,null);
			
			//construct unfolding
			unf.add(c);
			Place unfP = new Place(i.getName());
			unf_net.addNode(unfP);
			unf2net.put(unfP, c);
		}
		
		// get possible extensions of the unfolding
		Collection<Event> pe = getPossibleExtensions();
		
		while (pe.size()>0) {
			for (Event e : pe) {
				unf.add(e);
				
				// TODO
				//unf_net.addFlow(from, e.getTransition());
				
				for (Place p : net.getPostset(e.getTransition())) {
						Condition c = new Condition(p,e);
						unf.add(c);
						
						// TODO
				}
			}
		}
		
		pe = getPossibleExtensions();
	}
	
	/**
	 * Get possible extensions of the unfolding
	 * @return Collection of possible extension events of the unfolding 
	 */
	private Collection<Event> getPossibleExtensions() {
		Collection<Event> result = new ArrayList<Event>();
		
		Collection<Place> unfPlaces = unf_net.getPlaces();
		Set<Place> unfNetPlaces = new HashSet<Place>();
		for (Place p : unfPlaces) unfNetPlaces.add(((Condition)unf2net.get(p)).getPlace());
		
		for (Transition t : this.net.getTransitions()) {
			Collection<Place> tPreset = net.getPreset(t);
			
			if (unfNetPlaces.containsAll(tPreset)) {
				
			}
			
		}
		
		
		return result;
	}
}
