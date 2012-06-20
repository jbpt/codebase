package org.jbpt.algo.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jbpt.graph.Fragment;
import org.jbpt.graph.abs.IEdge;
import org.jbpt.graph.abs.IFragment;
import org.jbpt.graph.abs.IGraph;
import org.jbpt.hypergraph.abs.IVertex;

/**
 * Connected components of a graph.
 * 
 * @author Artem Polyvyanyy
 *
 * @param <E> template for edge (extends IEdge)
 * @param <V> template for vertex (extends IVertex)
 */
public class ConnectedComponents<E extends IEdge<V>,V extends IVertex> {
	
	private IGraph<E,V> g = null;
	
	private Collection<IFragment<E,V>> components = null;
	
	/**
	 * Constructor.
	 * 
	 * @param g Graph for which to compute connected components.
	 */
	public ConnectedComponents(IGraph<E,V> g) {
		this.g = g;
		this.components = new ArrayList<IFragment<E,V>>();
		
		if (this.g == null) return;
		
		this.construct();
	}

	private void construct() {
		Set<V> toVisit = new HashSet<V>(this.g.getVertices());
		
		while (!toVisit.isEmpty()) {
			V v = toVisit.iterator().next();
			this.constructConnectedComponent(v,toVisit);
		}
	}
	
	private void constructConnectedComponent(V v, Collection<V> toVisit) {
		IFragment<E,V> f = new Fragment<E,V>(this.g);
		Set<V> visited = new HashSet<V>();
		visited.add(v);
		Queue<V> queue = new ConcurrentLinkedQueue<V>();
		queue.add(v);
		
		while (!queue.isEmpty()) {
			V vv = queue.poll();
			f.addAll(this.g.getEdges(vv));
			
			for (V vvv : this.g.getAdjacent(vv)) {
				if (!visited.contains(vvv))
					queue.add(vvv);
				
				visited.add(vvv);
				toVisit.remove(vvv);
			}
		}
		
		this.components.add(f);
	}

	/**
	 * Get connected components of the graph.
	 * @return Collection of connected components of the graph.
	 */
	public Collection<IFragment<E,V>> getConnectedComponents() {
		return this.components;
	}
	
}
