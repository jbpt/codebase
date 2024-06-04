package org.jbpt.pm.gen.bootstrap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.DirectedGraph;
import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;

public class ProcessDiscovery {
	
	public static DirectedGraph discover(EventLog log, double filter) {
		int size = (int) (filter * log.size());
		//EventLog fLog = EventLogSampling.logSamplingWithReplacement(log,size);
		EventLog fLog = EventLogSampling.logSamplingWithBreeding(log, size, 128, 1, 1.0);
		EventLogManipulation.swapRandomEvents(fLog, size/10);
		
		DirectedGraph G = new DirectedGraph();
		Vertex input    = new Vertex("INPUT");
		Vertex output   = new Vertex("OUTPUT");
		HashMap<String,Vertex> map = new HashMap<String,Vertex>();
		
		Iterator<Trace> it = fLog.iterator();
		while (it.hasNext()) {
			Trace t = it.next();
			Iterator<String> ie = t.iterator();
			String last = null;
			while (ie.hasNext()) {
				String next = ie.next();
				Vertex v = map.get(next);
				if (v==null) {
					v = new Vertex(next);
					map.put(next, v);
				}
				if (last!=null) {
					Vertex lv = map.get(last);
					if (G.getEdgesWithSourceAndTarget(lv,v).isEmpty()) {
						G.addEdge(lv, v);
					}
				}
				else {
					G.addEdge(input,v);
				}
				last = next;
			}
			Vertex lv = map.get(last);
			G.addEdge(lv, output);
		}
		return G;
	}
	
	public static NetSystem graphToNetSystem(DirectedGraph G) {
		NetSystem S = new NetSystem();
		Place input = new Place("");
		Place output = new Place("");
		
		Iterator<Vertex> vi = G.getVertices().iterator();
		Map<String,Place> name2pre = new HashMap<String,Place>();
		Map<String,Place> name2post = new HashMap<String,Place>();
		while (vi.hasNext()) {
			Vertex v = vi.next();
			String name = v.getName(); 
			if (name.equals("INPUT")) {
				name2post.put(name, input);
			}
			else if (name.equals("OUTPUT")) {
				name2pre.put(name, output);
			}
			else {
				Place preV = new Place(""); 
				Place postV = new Place("");
				Transition t = new Transition(name);
				t.setLabel(name);
				S.addFlow(preV, t);
				S.addFlow(t, postV);
				name2pre.put(name, preV);
				name2post.put(name, postV);
			}

		}
		
		Iterator<DirectedEdge> ei = G.getEdges().iterator();
		while (ei.hasNext()) {
			DirectedEdge edge = ei.next();
			Transition t = new Transition("");
			t.setLabel("");
			S.addFlow(name2post.get(edge.getSource().getName()),t);
			S.addFlow(t,name2pre.get(edge.getTarget().getName()));
		}
		
		S.loadNaturalMarking();
		
		return S;
	}
	
}
