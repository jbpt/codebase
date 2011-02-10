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
package de.hpi.bpt.process.petri;

import java.util.HashSet;
import java.util.Set;

import de.hpi.bpt.graph.algo.DirectedGraphAlgorithms;
import de.hpi.bpt.graph.algo.GraphAlgorithms;
import de.hpi.bpt.process.petri.rels.UnfoldingRelationType;
import de.hpi.bpt.process.petri.rels.UnfoldingRelationsProfiler;

public class PNAnalyzer {
	
	public static boolean isBipartite(PetriNet net) {
		for (Flow f : net.getFlowRelation()) {
			int cp,ct;
			cp = ct = 0;
			if (f.getTarget() instanceof Place) cp++;
			else ct++;
			if (f.getSource() instanceof Place) cp++;
			else ct++;
			
			if (cp!=ct) return false;
		}
		
		return true;
	}
	
	public static boolean isConnected(PetriNet net) {
		GraphAlgorithms<Flow, Node> ga = new GraphAlgorithms<Flow, Node>();
		return ga.isConnected(net);
	}
	
	public static boolean isFreeChoice(PetriNet net) {
		for (Place p : net.getPlaces()) {
			if (net.getPostset(p).size()>1) {
				Set<Place> z = new HashSet<Place>();
				for (Transition t : net.getPostset(p)) {
					z.addAll(net.getPreset(t));
				}
				if (z.size()>1) return false;
			}
		}
		
		return true;
	}
	
	public static boolean isOccurrenceNet(PetriNet net) {
		
		// no branched places
		for (Place p : net.getPlaces())
			if (net.getPreset(p).size()>1)
				return false;
		
		// must be acyclic
		DirectedGraphAlgorithms<Flow,Node> dga = new DirectedGraphAlgorithms<Flow, Node>();
		if (dga.hasCycles(net)) return false;
		
		// no event is in self-conflict
		UnfoldingRelationsProfiler urp = new UnfoldingRelationsProfiler(net);
		for (Transition t : net.getTransitions())
			if (urp.getRelation(t,t) == UnfoldingRelationType.CONFLICT)
				return false;
		
		return true;
	}
	
	/**
	 * Checks whether the given Petri net is extended free-choice. That is,
	 * all transitions that share a place in their presets have to coincide
	 * w.r.t. their presets
	 * 
	 * @param a Petri net
	 * @return true, if the given Petri net is extended free-choice
	 */
	public static boolean isExtendedFreeChoice(PetriNet net) {
		boolean isFC = true;
		
		outer:
		for (Transition t1 : net.getTransitions()) {
			for (Transition t2 : net.getTransitions()) {
				for (Place p : net.getPlaces()) {
					if (net.getPredecessors(t1).contains(p) && net.getPredecessors(t2).contains(p))
						isFC &= net.getPreset(t1).equals(net.getPreset(t2));
					if (!isFC) 
						break outer;
				}
			}
		}
		return isFC;
	}
	
	/**
	 * Checks whether the given Petri net is a workflow net. Such a net has
	 * exactly one initial and one final place and every place and 
	 * transition is one a path from i to o.
	 * 
	 * @return true, if the net is a workflow net
	 */
	public static boolean isWorkflowNet(PetriNet net) {
		boolean isWF = (net.getSourcePlaces().size() == 1) && (net.getSinkPlaces().size() == 1);
		// maybe we already know that the net is not a workflow net
		if (!isWF)
			return isWF;
		
		Node in = net.getSourcePlaces().iterator().next();
		Node out = net.getSinkPlaces().iterator().next();
		for (Node n : net.getNodes()) {
			if (n.equals(in) || n.equals(out))
				continue;
			isWF &= net.hasPath(in, n);
			isWF &= net.hasPath(n, out);
		}
		return isWF;
	}

	/**
	 * Checks whether the given Petri net is an S-net.
	 * 
	 * @return true, if net is an S-net.
	 */
	public static boolean isSNet(PetriNet net) {
		boolean result = true;
		for (Transition t : net.getTransitions())
			result &= (net.getIncomingEdges(t).size() == 1) && ((net.getOutgoingEdges(t).size() == 1));
		return result;	
	}

	/**
	 * Checks whether the given Petri net is a T-net.
	 * 
	 * @return true, if net is a T-net.
	 */
	public static boolean isTNet(PetriNet net) {
		boolean result = true;
		for (Place p : net.getPlaces())
			result &= (net.getIncomingEdges(p).size() == 1) && ((net.getOutgoingEdges(p).size() == 1));
		return result;	
	}

}
