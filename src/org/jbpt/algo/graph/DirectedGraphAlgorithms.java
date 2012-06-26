package org.jbpt.algo.graph;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.graph.abs.IDirectedGraph;
import org.jbpt.hypergraph.abs.IVertex;


/**
 * Collection of algorithms for manipulations on directed graph. 
 * 
 * @author Artem Polyvyanyy
 * @author Matthias Weidlich
 *
 * @param <E> template for edge (extends IDirectedEdge)
 * @param <V> template for vertex (extends IVertex)
 */
public class DirectedGraphAlgorithms<E extends IDirectedEdge<V>,V extends IVertex> {
	
	/**
	 * Get boundary vertices of a directed graph. 
	 * A vertex of a directed graph is called a boundary vertex if it neither has direct predecessors, nor direct successors.
	 * 
	 * Time complexity: linear to the size of graph.
	 *  
	 * @param g Directed graph
	 * @return Collection of graph's boundary vertices.
	 */
	public Collection<V> getBoundaryVertices(IDirectedGraph<E, V> g)
	{
		Collection<V> result = new ArrayList<V>();
		
		Iterator<V> i = g.getVertices().iterator();
		while (i.hasNext()) {
			V v = i.next();
			int in = g.getIncomingEdges(v).size();
			int out = g.getOutgoingEdges(v).size();
			if (in==0 || out==0) result.add(v);
		}
		
		return result;
	}
	
	/**
	 * Get source vertices of a given directed graph. 
	 * A vertex of a directed graph is called a source vertex if it has no direct predecessors. 
	 * 
	 * @param g Directed graph.
	 * @return Set of graph's source vertices.
	 */
	public Set<V> getSources(IDirectedGraph<E,V> g) {
		Set<V> result = new HashSet<V>();
		if (g==null) return result;
		
		for (V v : g.getVertices())
			if (g.getDirectPredecessors(v).isEmpty())
				result.add(v);
		
		return result;
	}
	
	/**
	 * Get sink vertices of a given directed graph. 
	 * A vertex of a directed graph is called a sink vertex if it has no direct successors.
	 * 
	 * @param g Directed graph
	 * @return Collection of graph's sink vertices.
	 */
	public Set<V> getSinks(IDirectedGraph<E,V> g) {
		Set<V> result = new HashSet<V>();
		if (g==null) return result;
		
		for (V v : g.getVertices())
			if (g.getDirectSuccessors(v).isEmpty())
				result.add(v);
		
		return result;
	}
	
	/**
	 * Test if a directed graph is acyclic.
	 * A directed graph is acyclic if it has no directed cycles, i.e., 
	 * it is impossible to start at some vertex v and follow a sequence of edges that eventually lead to v again. 
	 * 
	 * Time complexity: linear to the size of graph.
	 * 
	 * @param g Directed graph
	 * @return <tt>true</tt> if the directed graph is acyclic; <tt>false</tt> otherwise.
	 */
	public boolean isAcyclic(IDirectedGraph<E, V> g) {
		StronglyConnectedComponents<E,V> sccs = new StronglyConnectedComponents<E,V>();
		return sccs.compute(g).size() == g.getVertices().size();
	}
	
	/**
	 * Test if a directed graph is cyclic.
	 * A directed graph is cyclic if it has at least one directed cycle, i.e., 
	 * it is possible to start at some vertex v and follow a sequence of edges that eventually lead to v again. 
	 * 
	 * Time complexity: linear to the size of graph.
	 * 
	 * @param g Directed graph
	 * @return <tt>true</tt> if the directed graph is cyclic; <tt>false</tt> otherwise.
	 */
	public boolean isCyclic(IDirectedGraph<E,V> g) {
		return !this.isAcyclic(g);
	}
	
	/**
	 * Check if directed graph has a path between the given nodes. 
	 * 
	 * TODO: Improve. 
	 * 
	 * @param Directed graph
	 * @param source node
	 * @param target node
	 * @return true, if there is a path from the source node to the target node in the directed graph
	 */
	public boolean hasPath(IDirectedGraph<E, V> g, V from, V to) {
		TransitiveClosure<E,V> tc = new TransitiveClosure<E,V>(g);
		return tc.hasPath(from, to);
	}

	/**
	 * Simple implementation of an algorithm to derive dominators and postdominators of a 
	 * directed graph. It uses the iterative approach which is simple but not really efficient, 
	 * it requires polynomial time. Could be done in linear time though.
	 * 
	 *  TODO: Replace with an efficient implementation.
	 * 
	 * @param Directed graph
	 * @param postDominators boolean parameter, if set the postdominators instead of dominators are computed
	 * @return A map comprising for each vertex the set of its dominators (or postdominators, respectively).
	 */
	public Map<V,Set<V>> getDominators(IDirectedGraph<E, V> g, boolean postDominators) {
		List<V> vList = new ArrayList<V>(g.getVertices());
		
		Collection<V> initV = postDominators ? this.getSinks(g) : this.getSources(g);
		
		int size = vList.size(); 
		final BitSet[] dom = new BitSet[size];
		final BitSet ALL = new BitSet(size);
		
		for (int i = 0; i < size; i++) ALL.set(i);

		for (int i = 0; i < size; i++) {
			BitSet curDoms = new BitSet(size);
			dom[i] = curDoms;

			if (!initV.contains(vList.get(i))) curDoms.or(ALL);
			else curDoms.set(i);
		}
		
		boolean changed = true;
	
		/*
		 * While we change the dom relation for a node
		 */
		while (changed) {
			changed = false;
			for (int i = 0; i < size; i++) {
				if (initV.contains(vList.get(i))) continue;
				 
				final BitSet old = dom[i];
				final BitSet curDoms = new BitSet(size);
				curDoms.or(old);
				
				Collection<V> predecessors = postDominators ? g.getDirectSuccessors(vList.get(i)) : g.getDirectPredecessors(vList.get(i));
				for (V v : predecessors)
					curDoms.and(dom[vList.indexOf(v)]);
				
				curDoms.set(i);
				
				if (!curDoms.equals(old)) {
					changed = true;
					dom[i] = curDoms;
				}
			}
		}
		
		/*
		 * Create the data structure that we want to return
		 * 
		 * The quadratic time complexity of building up this structure does not hurt
		 * as the above algorithm requires more time anyways
		 */
		Map<V,Set<V>> dominators = new HashMap<V, Set<V>>();
		for (int i = 0; i < size; i++) {
			dominators.put(vList.get(i), new HashSet<V>());
			for (int j = 0; j < size; j++)
				if (dom[i].get(j))
					dominators.get(vList.get(i)).add(vList.get(j));
		}
		
		return dominators;
	}
	
	/**
	 * Check if directed graph is a two-terminal graph. 
	 * A directed graph is called two-terminal if it has one source and one sink vertex, 
	 * such that each vertex lies on a path from the source to the sink. 
	 * 
	 * Time complexity: linear to the size of the graph.
	 * 
	 * @param g Directed graph.
	 * @return <tt>true</tt> if directed graph is two-terminal; <tt>false</tt> otherwise.
	 */
	public boolean isTwoTerminal(IDirectedGraph<E,V> g) {
		if (g==null) return false;
		if (this.getSources(g).size()!=1 || this.getSinks(g).size()!=1)
			return false;
		return this.isMultiTerminal(g);
	}
	
	/**
	 * Check if directed graph is a multi-terminal graph. 
	 * A directed graph is called multi-terminal if it has at least one source and at least one sink vertex, 
	 * such that each vertex lies on a path from some source to some sink. 
	 * 
	 * Time complexity: linear to the size of the graph.
	 * 
	 * @param g Directed graph.
	 * @return <tt>true</tt> if directed graph is multi-terminal; <tt>false</tt> otherwise.
	 */
	public boolean isMultiTerminal(IDirectedGraph<E,V> g) {
		if (g==null) return false;
		Collection<V> sources = this.getSources(g);
		Collection<V> sinks = this.getSinks(g);
		if (sources.isEmpty() || sinks.isEmpty()) return false;
		V src = g.getFreshVertex(); V snk = g.getFreshVertex();
		for (V v : sources) g.addEdge(src,v);
		for (V v : sinks) g.addEdge(v,snk);
		g.addEdge(snk,src);
		StronglyConnectedComponents<E,V> sccs = new StronglyConnectedComponents<E,V>();
		boolean result = sccs.isStronglyConnected(g);
		g.removeVertex(src); g.removeVertex(snk);
		return result;
	}
}
