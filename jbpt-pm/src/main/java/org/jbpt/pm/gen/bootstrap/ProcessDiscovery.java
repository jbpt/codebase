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
import org.jbpt.pm.models.FDAGArc;
import org.jbpt.pm.models.FDAGNode;
import org.jbpt.pm.models.FDAGraph;

public class ProcessDiscovery {

	public static NetSystem fdagToNetSystem(FDAGraph G) {
		NetSystem S = new NetSystem();
		Place input = new Place("");
		Place output = new Place("");

		Map<String, Place> name2pre = new HashMap<>();
		Map<String, Place> name2post = new HashMap<>();

		Map<Integer, String> nodeIdToName = new HashMap<>();

		for (FDAGNode node : G.getNodes()) {
			String name = node.getLabel();
			nodeIdToName.put(node.getId(), name);
			if (name.equals("INPUT")) {
				name2post.put(name, input);
			} else if (name.equals("OUTPUT")) {
				name2pre.put(name, output);
			} else {
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

		for (FDAGArc arc : G.getArcs()) {
			Transition t = new Transition("");
			t.setLabel("");
			S.addFlow(name2post.get(nodeIdToName.get(arc.getFrom())), t);
			S.addFlow(t, name2pre.get(nodeIdToName.get(arc.getTo())));
		}

		S.loadNaturalMarking();

		return S;
	}

	public static NetSystem graphToNetSystem(DirectedGraph G) {
		NetSystem S = new NetSystem();
		Place input = new Place("");
		Place output = new Place("");
		
		Iterator<Vertex> vi = G.getVertices().iterator();
		Map<String,Place> name2pre = new HashMap<>();
		Map<String,Place> name2post = new HashMap<>();
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

		for (DirectedEdge edge : G.getEdges()) {
			Transition t = new Transition("");
			t.setLabel("");
			S.addFlow(name2post.get(edge.getSource().getName()), t);
			S.addFlow(t, name2pre.get(edge.getTarget().getName()));
		}
		
		S.loadNaturalMarking();
		
		return S;
	}
	
}
