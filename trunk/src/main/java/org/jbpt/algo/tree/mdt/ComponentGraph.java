package org.jbpt.algo.tree.mdt;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.jbpt.graph.abs.AbstractDirectedGraph;
import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.hypergraph.abs.Vertex;

public class ComponentGraph<E extends IDirectedEdge<V>, V extends IVertex> extends AbstractDirectedGraph<E, V> {
	Map<V, Set<Set<V>>> pmap;
	
	public ComponentGraph(AbstractDirectedGraph<E, V> g, Collection<Set<V>> m, V v) {
		super();
		
		pmap = new HashMap<V, Set<Set<V>>>();
		
		// Build component graph
		Map<V, V> map = new HashMap<V, V>();
		
		for (Set<V> p: m) {
			@SuppressWarnings("unchecked")
			V vp = (V) new Vertex("CG:"+p.toString());
			addVertex(vp);
			
			// vp is a vertex in the ComponentGraph, which is then associated with
			// vertex from the partition in the original graph (any)
			map.put(vp, p.iterator().next());
			
			// Initialize the set of partitions associated to each node in the component graph
			Set<Set<V>> sopart =  new HashSet<Set<V>>();
			sopart.add(p);
			pmap.put(vp, sopart);
		}
		
		for (V xp: map.keySet()) {
			V x = map.get(xp);
			if (x.equals(v)) continue;
			for (V yp: map.keySet()) {
				V y = map.get(yp);
				if (y.equals(v) || x.equals(y)) continue;
				
				if (distinguishes(g, x, y, v))
					addEdge(xp, yp);
			}
		}
		
		// Compute connected components and collapse them
		contractSCC();
	}

	public Set<Set<V>> getPartitions(Set<V> vertices) {
		Set<Set<V>> result = new HashSet<Set<V>>();
		for (V v: vertices)
			result.addAll(pmap.get(v));
		return result;
	}
	
	public Set<V> getPartitionUnion() {
		Set<V> result = new HashSet<V>();
		for (V vertex: getVertices()) {
			for (Set<V> bs: pmap.get(vertex))
				result.addAll(bs);
		}
		return result;
	}
	
	public void contractSCC() {		
		Set<Set<V>> scc = kosaraju();
		
		for (Set<V> cc : scc) {
			if (cc.size() > 1) {
				Set<Set<V>> parts = getPartitions(cc);
				V v = cc.iterator().next();
				cc.remove(v);
				removeVertices(cc);
				pmap.put(v, parts);
			}
		}
	}

	
	// Computation of Strongly Connected Components using the Kosaraju-Sharir's algorithm
	private Set<Set<V>> kosaraju() {
		Set<Set<V>> scc = new HashSet<Set<V>>();
		Stack<V> stack = new Stack<V>();
		Set<V> visited = new HashSet<V>();
		for (V vertex: getVertices())
			if (!visited.contains(vertex))
				searchForward(vertex, stack, visited);			

		visited.clear();
		while(!stack.isEmpty()) {
			Set<V> component = new HashSet<V>();
			searchBackward(stack.peek(), visited, component);
			scc.add(component);
			stack.removeAll(component);
		}
		return scc;
	}

	private void searchBackward(V node, Set<V> visited, Set<V> component) {
		Stack<V> worklist = new Stack<V>();
		worklist.push(node);
		while (!worklist.isEmpty()) {
			V curr = worklist.pop();
			visited.add(curr);
			component.add(curr);
			for (V pred: getDirectPredecessors(curr))
				if (!visited.contains(pred) && !worklist.contains(pred))
					worklist.add(pred);
		}
	}

	private void searchForward(V curr, Stack<V> stack, Set<V> visited) {
		visited.add(curr);
		for (V succ: getDirectSuccessors(curr))
			if (!visited.contains(succ))
				searchForward(succ, stack, visited);
		stack.push(curr);
	}

	private boolean distinguishes(AbstractDirectedGraph<E, V> g, V x, V y,
			V z) {		
		return (hasEdge(g, x, y) != hasEdge(g, x, z)) || (hasEdge(g, y, x) != hasEdge(g, z, x));
	}
	
	private boolean hasEdge(AbstractDirectedGraph<E, V> g, V x, V y) {
		return g.getDirectedEdge(x, y) != null;
	}

	public Set<V> getSinkNodes() {
		Set<V> sinks = new HashSet<V>(getVertices());
		for (E e: getEdges())
			sinks.remove(e.getSource());
		return sinks;
	}
}
