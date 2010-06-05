/**
 * Copyright (c) 2008 Artem Polyvyanyy
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.hpi.bpt.graph.abs.AbstractDirectedGraph;
import de.hpi.bpt.graph.algo.DirectedGraphAlgorithms;
import de.hpi.bpt.hypergraph.abs.Vertex;

/**
 * Petri net
 *  
 * @author Artem Polyvyanyy
 *
 */
public class PetriNet extends AbstractDirectedGraph<Flow, Node> {
	
	private DirectedGraphAlgorithms<Flow,Node> dga = null;
	
	public PetriNet() {}
	
	public Flow addFlow(Node from, Node to) {
		if (from == null || to == null) return null;
		
		Collection<Node> ss = new ArrayList<Node>(); ss.add(from);
		Collection<Node> ts = new ArrayList<Node>(); ts.add(to);
		
		if (!this.checkEdge(ss, ts)) return null;
		
		return new Flow(this, from, to);
	}
	
	public Node addNode(Node n) {
		return this.addVertex(n);
	}
	
	public Collection<Flow> getFlowRelation() {
		return this.getEdges();
	}
	
	public Set<Vertex> getEnabledElements() {
		Set<Vertex> res = new HashSet<Vertex>();
		Iterator<Transition> i = this.getTransitions().iterator();
		while(i.hasNext()) {
			Transition t = i.next();
			if (this.isEnabled(t))
				res.add(t);
		}
		
		return res;
	}
	
	public boolean isEnabled(Transition t) {
		Collection<Node> ps = getPredecessors(t);
		Iterator<Node> i = ps.iterator();
		while (i.hasNext()) {
			Node n = i.next();
			if (n instanceof Place) {
				if (((Place) n).getTokens()<=0) return false;
			}
			else return false;
		}
		return true;
	}
	
	public boolean isTerminated() {
		Iterator<Transition> i = this.getTransitions().iterator();
		while (i.hasNext()) {
			if (this.isEnabled(i.next())) return false;
		}
		return true;
	}
	
	public boolean fire(Vertex v) {
		Transition t = null;
		if (v instanceof Transition) {
			t = (Transition) v;
		}
		else return false;
		
		if (this.isEnabled(t)) {			
			Collection<Node> ps = getPredecessors(t);
			Iterator<Node> i = ps.iterator();
			while (i.hasNext()) {
				Node n = i.next();
				Place p = (Place) n;
				p.takeToken();
			}
			
			Collection<Node> ss = getSuccessors(t);
			i = ss.iterator();
			while (i.hasNext()) {
				Node n = i.next();
				if (n instanceof Place) {
					Place p = (Place) n;
					p.putToken();	
				}
			}
		}
		else return false;
		
		return true;
	}
	
	public boolean putToken(Place p) {
		return p.putToken();
	}

	public Collection<Node> getNodes() {
		return this.getVertices();
	}
	
	public Collection<Transition> getTransitions() {
		Collection<Transition> result = new ArrayList<Transition>();
		
		for (Node n : this.getVertices()) {
			if (n instanceof Transition)
				result.add((Transition)n);
		}
		
		return result;
	}

	public Collection<Place> getPlaces() {
		Collection<Place> result = new ArrayList<Place>();
		
		for (Node n : this.getVertices()) {
			if (n instanceof Place)
				result.add((Place)n);
		}
		
		return result;
	}
	
	public Collection<Place> getPostset(Transition t) {
		if (!this.contains(t)) return null;
		
		Collection<Place> result = new ArrayList<Place>();
		for (Node n : this.getSuccessors(t)) {
			if (n instanceof Place)
				result.add((Place)n);
		}
		
		return result;
	}
	
	public Collection<Transition> getPostset(Place p) {
		if (!this.contains(p)) return null;
		
		Collection<Transition> result = new ArrayList<Transition>();
		for (Node n : this.getSuccessors(p)) {
			if (n instanceof Transition)
				result.add((Transition)n);
		}
		
		return result;
	}
	
	public Collection<Node> getPostset(Node n) {
		if (!this.contains(n)) return null;
		
		return this.getSuccessors(n);
	}
	
	
	public Collection<Place> getPreset(Transition t) {
		if (!this.contains(t)) return null;
		
		Collection<Place> result = new ArrayList<Place>();
		for (Node n : this.getPredecessors(t)) {
			if (n instanceof Place)
				result.add((Place)n);
		}
		
		return result;
	}
	
	public Collection<Transition> getPreset(Place p) {
		if (!this.contains(p)) return null;
		
		Collection<Transition> result = new ArrayList<Transition>();
		for (Node n : this.getPredecessors(p)) {
			if (n instanceof Transition)
				result.add((Transition)n);
		}
		
		return result;
	}
	
	public Collection<Node> getPreset(Node n) {
		if (!this.contains(n)) return null;
		
		return this.getPredecessors(n);
	}
	
	public Collection<Node> getSourceNodes() {
		if (dga == null) dga = new DirectedGraphAlgorithms<Flow, Node>();
		return dga.getInputVertices(this);
	}
	
	public Collection<Place> getSourcePlaces() {
		Collection<Place> result = new ArrayList<Place>();
		for (Node n : getSourceNodes())
			if (n instanceof Place)
				result.add((Place)n);
		
		return result;
	}
	
	public Collection<Transition> getSourceTransitions() {
		Collection<Transition> result = new ArrayList<Transition>();
		for (Node n : getSourceNodes())
			if (n instanceof Transition)
				result.add((Transition)n);
		
		return result;
	}
	
	public Collection<Node> getSinkNodes() {
		if (dga == null) dga = new DirectedGraphAlgorithms<Flow, Node>();
		return dga.getOutputVertices(this);
	}
	
	public Collection<Place> getSinkPlaces() {
		Collection<Place> result = new ArrayList<Place>();
		for (Node n : getSinkNodes())
			if (n instanceof Place)
				result.add((Place)n);
		
		return result;
	}
	
	public Collection<Transition> getSinkTransitions() {
		Collection<Transition> result = new ArrayList<Transition>();
		for (Node n : getSinkNodes())
			if (n instanceof Transition)
				result.add((Transition)n);
		
		return result;
	}
}
