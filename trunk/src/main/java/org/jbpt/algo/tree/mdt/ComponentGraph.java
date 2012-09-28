package org.jbpt.algo.tree.mdt;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.DirectedGraph;
import org.jbpt.hypergraph.abs.Vertex;

public class ComponentGraph extends DirectedGraph {
	Map<Vertex, Set<Set<Vertex>>> pmap;
	
	public ComponentGraph(DirectedGraph g, Collection<Set<Vertex>> m, Vertex v) {
		super();
		
		pmap = new HashMap<Vertex, Set<Set<Vertex>>>();
		
		// Build component graph
		Map<Vertex, Vertex> map = new HashMap<Vertex, Vertex>();
		
		for (Set<Vertex> p: m) {
			Vertex vp = new Vertex("CG:"+p.toString());
			addVertex(vp);
			
			// vp is a vertex in the ComponentGraph, which is then associated with
			// vertex from the partition in the original graph (any)
			map.put(vp, p.iterator().next());
			
			// Initialize the set of partitions associated to each node in the component graph
			Set<Set<Vertex>> sopart =  new HashSet<Set<Vertex>>();
			sopart.add(p);
			pmap.put(vp, sopart);
		}
		
		for (Vertex xp: map.keySet()) {
			Vertex x = map.get(xp);
			if (x.equals(v)) continue;
			for (Vertex yp: map.keySet()) {
				Vertex y = map.get(yp);
				if (y.equals(v) || x.equals(y)) continue;
				
				if (distinguishes(g, x, y, v))
					addEdge(xp, yp);
			}
		}
		
		// Compute connected components and collapse them
		contractSCC();
	}

	public Set<Set<Vertex>> getPartitions(Set<Vertex> vertices) {
		Set<Set<Vertex>> result = new HashSet<Set<Vertex>>();
		for (Vertex v: vertices)
			result.addAll(pmap.get(v));
		return result;
	}
	
	public Set<Vertex> getPartitionUnion() {
		Set<Vertex> result = new HashSet<Vertex>();
		for (Vertex vertex: getVertices()) {
			for (Set<Vertex> bs: pmap.get(vertex))
				result.addAll(bs);
		}
		return result;
	}
	
	public void contractSCC() {		
		Set<Set<Vertex>> scc = kosaraju();
		
		for (Set<Vertex> cc : scc) {
			if (cc.size() > 1) {
				Set<Set<Vertex>> parts = getPartitions(cc);
				Vertex v = cc.iterator().next();
				cc.remove(v);
				removeVertices(cc);
				pmap.put(v, parts);
			}
		}
	}

	
	// Computation of Strongly Connected Components using the Kosaraju-Sharir's algorithm
	private Set<Set<Vertex>> kosaraju() {
		Set<Set<Vertex>> scc = new HashSet<Set<Vertex>>();
		Stack<Vertex> stack = new Stack<Vertex>();
		Set<Vertex> visited = new HashSet<Vertex>();
		for (Vertex vertex: getVertices())
			if (!visited.contains(vertex))
				searchForward(vertex, stack, visited);			

		visited.clear();
		while(!stack.isEmpty()) {
			Set<Vertex> component = new HashSet<Vertex>();
			searchBackward(stack.peek(), visited, component);
			scc.add(component);
			stack.removeAll(component);
		}
		return scc;
	}

	private void searchBackward(Vertex node, Set<Vertex> visited, Set<Vertex> component) {
		Stack<Vertex> worklist = new Stack<Vertex>();
		worklist.push(node);
		while (!worklist.isEmpty()) {
			Vertex curr = worklist.pop();
			visited.add(curr);
			component.add(curr);
			for (Vertex pred: getDirectPredecessors(curr))
				if (!visited.contains(pred) && !worklist.contains(pred))
					worklist.add(pred);
		}
	}

	private void searchForward(Vertex curr, Stack<Vertex> stack, Set<Vertex> visited) {
		visited.add(curr);
		for (Vertex succ: getDirectSuccessors(curr))
			if (!visited.contains(succ))
				searchForward(succ, stack, visited);
		stack.push(curr);
	}

	private boolean distinguishes(DirectedGraph g, Vertex x, Vertex y,
			Vertex z) {		
		return (hasEdge(g, x, y) != hasEdge(g, x, z)) || (hasEdge(g, y, x) != hasEdge(g, z, x));
	}
	
	private boolean hasEdge(DirectedGraph g, Vertex x, Vertex y) {
		return g.getDirectedEdge(x, y) != null;
	}

	public Set<Vertex> getSinkNodes() {
		Set<Vertex> sinks = new HashSet<Vertex>(getVertices());
		for (DirectedEdge e: getEdges())
			sinks.remove(e.getSource());
		return sinks;
	}
}
