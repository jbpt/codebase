package org.jbpt.algo.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.jbpt.algo.CombinationGenerator;
import org.jbpt.graph.abs.AbstractMultiGraphFragment;
import org.jbpt.graph.abs.IEdge;
import org.jbpt.graph.abs.IGraph;
import org.jbpt.hypergraph.abs.IVertex;


/**
 * Collection of graph algorithms
 * @author Artem Polyvyanyy
 *
 * @param <E> template for edge (extends IEdge)
 * @param <V> template for vertex (extends IVertex)
 */
public class GraphAlgorithms<E extends IEdge<V>,V extends IVertex> {
	
	/**
	 * Get graph boundary vertices (vertices that are included only into one edge)
	 * @param g Graph
	 * @return Collection of boundary vertices of the graph
	 */
	public Collection<V> getBoundaryVertices(IGraph<E,V> g)
	{
		Collection<V> result = new ArrayList<V>();
		
		Iterator<V> i = g.getVertices().iterator();
		while (i.hasNext()) {
			V v = i.next();
			if (g.getEdges(v).size()==1)
				result.add(v);
		}
		
		return result;
	}
	
	/**
	 * Check if graph is connected (there is a path between any pair of vertices)
	 * @param g Graph
	 * @return true if graph is connected, false otherwise
	 */
	public boolean isConnected(IGraph<E,V> g)
	{
		if (g.countVertices()==0) return true;
		
		V x = g.getVertices().iterator().next(); 
		Collection<V> L = new ArrayList<V>();
		Collection<V> K = new ArrayList<V>();
		L.add(x); K.add(x);
		
		while (K.size()>0) {
			V y = K.iterator().next();
			K.remove(y);

			Iterator<V> j = g.getAdjacent(y).iterator();
			while (j.hasNext()) {
				V z = j.next();
				if (!L.contains(z)) {
					L.add(z);
					K.add(z);
				}
			}
		}
		
		return (g.countVertices()==L.size()) ? true : false;
	}
	
	/**
	 * Check if graph is v-connected
	 * Connected if any combination of v vertices is removed
	 * 
	 * @param g Graph
	 * @param v Number of vertices
	 * @return <code>true</code> if graph is v-connected, <code>false</code> otherwise
	 */
	public boolean isConnected(IGraph<E,V> g, int v) {
		if (g == null) return false;
		Collection<V> vs = g.getVertices();
		
		if (v<=0) return this.isConnected(g);
		
		if (v > vs.size()) return false;
		
		CombinationGenerator<V> cgv = new CombinationGenerator<V>(vs,v);
		
		// do extensive search
		AbstractMultiGraphFragment<E,V> gf = new AbstractMultiGraphFragment<E,V>(g);
		while (cgv.hasMore()) {
			Collection<V> cvs = cgv.getNextCombination();
			
			gf.copyOriginalGraph();
			
			gf.removeVertices(cvs);
			
			if (!this.isConnected(gf)) return false;
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param g Graph
	 * @param nv 
	 * @return
	 */
	public Collection<V> getSeparationSet(IGraph<E,V> g, int nv) {
		if (nv<0) return null;
		Collection<V> vs = g.getVertices();
		if (nv > vs.size()) return null;
		CombinationGenerator<V> cgv = new CombinationGenerator<V>(vs,nv);
		
		AbstractMultiGraphFragment<E,V> gf = new AbstractMultiGraphFragment<E,V>(g);
		while (cgv.hasMore()) {
			Collection<V> cvs = cgv.getNextCombination();

			gf.copyOriginalGraph();
			
			gf.removeVertices(cvs);
			
			if (!this.isConnected(gf)) return cvs;
		}
		
		return null;
	}
}
