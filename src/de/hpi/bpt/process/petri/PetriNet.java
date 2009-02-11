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

import de.hpi.bpt.graph.abs.AbstractMultiDirectedGraph;
import de.hpi.bpt.hypergraph.abs.Vertex;
import de.hpi.bpt.process.engine.IProcess;

/**
 * Petri net
 *  
 *  - Control bipartite property
 * @author artem.polyvyanyy
 *
 */
public class PetriNet extends AbstractMultiDirectedGraph<FlowRelation, Node> implements IProcess {
	
	ArrayList<Place> places = new ArrayList<Place>();
	ArrayList<Transition> transitions = new ArrayList<Transition>();
	
	public PetriNet() {
	}

	/*public boolean addFlow(FlowRelation f) {
		if (!places.contains(f.getPlace())) places.add(f.getPlace());
		if (!transitions.contains(f.getTransition())) transitions.add(f.getTransition());
		
		return super.addEdge(f);
	}*/
	
	public Set<Vertex> getEnabledElements() {
		Set<Vertex> res = new HashSet<Vertex>();
		Iterator<Transition> i = transitions.iterator();
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
		Iterator<Transition> i = transitions.iterator();
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

	public void serialize() {
		/*Graph graph = GraphFactory.newGraph();
		graph.getInfo().setCaption("Petri net");
	  
		HashMap<String, GraphNode> map = new HashMap<String, GraphNode>();
	  
		Collection<FlowRelation> es = getEdges();
		Iterator<FlowRelation> i = es.iterator();
	  
		while (i.hasNext()) {
			FlowRelation f = i.next();
		  
			GraphNode n1 = map.get(f.getSource().toString());
			if (n1 == null) {
				n1 = graph.addNode();
				n1.getInfo().setCaption(f.getSource().toString());
				if (f.getSource() instanceof Place) n1.getInfo().setShapeCircle();
				map.put(f.getSource().toString(), n1);
			}
			
			GraphNode n2 = map.get(f.getTarget().toString());
			if (n2 == null) {
				n2 = graph.addNode();
				n2.getInfo().setCaption(f.getTarget().toString());
				if (f.getTarget() instanceof Place) n2.getInfo().setShapeCircle();
				map.put(f.getTarget().toString(), n2);
			}
		  
			graph.addEdge(n1, n2);
		}

		// output graph to *.gif
		final String dotFileName = "PetriNet.dot"; 
		final String gifFileName = "PetriNet.gif";
		
		try {
			GRAPHtoDOTtoGIF.transform(graph, dotFileName, gifFileName, "C:\\Program Files\\Graphviz2.20\\bin\\dot.exe");
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}


	public Collection<Transition> getTransitions() {
		return transitions;
	}

	public Collection<Place> getPlaces() {
		return places;
	}
}
