package org.jbpt.algo.tree.rpst;

import java.util.HashSet;
import java.util.Set;

import org.jbpt.algo.graph.DirectedGraphAlgorithms;
import org.jbpt.algo.tree.tctree.TCTree;
import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.graph.abs.IDirectedGraph;
import org.jbpt.hypergraph.abs.IVertex;


/**
 * The Refined Process Structure Tree (RPST).<br/><br/>
 * 
 * This class implements the RPST algorithm proposed in: 
 * Artem Polyvyanyy, Jussi Vanhatalo, and Hagen Voelzer. 
 * Simplified Computation and Generalization of the Refined Process Structure Tree. 
 * Proceedings of the 7th International Workshop on Web Services and Formal Methods (WS-FM). 
 * Hoboken, NJ, US, September 2010.
 * 
 * @author Artem Polyvyanyy 
 * 
 * @assumption A given directed graph is multi-terminal, see {@code DirectedGraphAlgorithms.isMultiTerminal}.
 */
public class RPST<E extends IDirectedEdge<V>, V extends IVertex> extends TCTree<E,V> {
	protected IDirectedGraph<E,V> diGraph = null;
	
	protected Set<E> extraEdges = new HashSet<E>();
	
	protected DirectedGraphAlgorithms<E,V> DGA = new DirectedGraphAlgorithms<E,V>();;
	
	public RPST(IDirectedGraph<E,V> graph) {
		if (graph==null) return;
		if (graph.getEdges().isEmpty()) return;
		
		this.diGraph = graph; 
		
		this.preprocess();
		
		this.construct();
	}
	
	private void preprocess() {
	}
	
	private void postprocess() {
	}
}
