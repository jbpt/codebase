package org.jbpt.graph.algo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.graph.abs.IDirectedGraph;
import org.jbpt.hypergraph.abs.IVertex;

/**
 * Compute strongly connected components of a directed graph.
 * A directed graph is called strongly connected if there is a path from each vertex in the graph to every other vertex.
 * The strongly connected components of a directed graph G are its maximal strongly connected subgraphs.
 * 
 * Implementation of Tarjan's algorithm.
 * The running time is O(|V|+|E|).
 * 
 * @author Artem Polyvyanyy
 *
 * @param <E> template for edge (extends IDirectedEdge)
 * @param <V> template for vertex (extends IVertex)
 */
public class StronglyConnectedComponents<E extends IDirectedEdge<V>,V extends IVertex> {
	private int index = 0;
	private Map<V,Integer> indices = new HashMap<V,Integer>();
	private Map<V,Integer> lowlink = new HashMap<V,Integer>();
	private Stack<V> stack = new Stack<V>();
	private Set<Set<V>> sccs = new HashSet<Set<V>>();
	
	/**
	 * Compute strongly connected components of a directed graph.
	 * 
	 * @param g Directed graph
	 * @return Set of strongly connected components of the directed graph. Each strongly connected component is given as a set of vertices of the directed graph.
	 */
	public Set<Set<V>> compute(IDirectedGraph<E,V> g) {
		this.index = 0;
		this.stack.clear();
		this.sccs.clear();
		this.indices.clear();
		this.lowlink.clear();
		
		if (g == null) return sccs;
		
		if (g.getVertices() == null) return sccs;

		for (V v : g.getVertices())
			if(this.indices.get(v) == null)
				this.tarjan(g,v);
		
		return sccs;
	}
	
	/**
	 * Main logic of Tarjan's algorithm
	 * 
	 * @param g Directed graph
	 * @param v Vertex
	 */
	private void tarjan(IDirectedGraph<E,V> g, V v) {
		this.indices.put(v,this.index);
		this.lowlink.put(v,this.index);
		this.index++;
		
		this.stack.push(v);
		
		for (V vv : g.getDirectSuccessors(v)) {
			if (this.indices.get(vv) == null) {
				this.tarjan(g,vv);
				this.lowlink.put(v, Math.min(this.lowlink.get(v), this.lowlink.get(vv)));
			}
			else if (this.stack.contains(vv))
				this.lowlink.put(v, Math.min(this.lowlink.get(v), this.indices.get(vv)));
		}
	
		if (this.lowlink.get(v) == this.indices.get(v)) {
			Set<V> scc = new HashSet<V>(); 
			V vv = null;
			do {
				vv = this.stack.pop();	
				scc.add(vv);
			} while (!v.equals(vv));
			
			this.sccs.add(scc);
		}
	}
	
	/**
	 * Test if a directed graph is strongly connected.
	 * A directed graph is strongly connected if there is a path from each vertex in the graph to every other vertex.
	 * 
	 * @param g Directed graph.
	 * @return <code>true</code> if the directed graph is strongly connected; <code>false</code> otherwise.
	 */
	public boolean isStronglyConnected(IDirectedGraph<E,V> g) {
		return this.compute(g).size() == 1;
	}
}
