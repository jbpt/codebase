package org.jbpt.algo.graph;

import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.graph.abs.IDirectedGraph;
import org.jbpt.hypergraph.abs.IVertex;

public class ReflexiveTransitiveClosure<E extends IDirectedEdge<V>,V extends IVertex> extends TransitiveClosure<E, V> {

	public ReflexiveTransitiveClosure(IDirectedGraph<E, V> g) {
		super(g);
	}
	
	@Override
	protected void calculateMatrix() {
		super.calculateMatrix();
		
		for (int i=0; i<this.verticesAsList.size(); i++) {
			this.matrix[i][i] = true;
		}
	}
}


