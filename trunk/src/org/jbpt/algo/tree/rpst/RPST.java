package org.jbpt.algo.tree.rpst;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jbpt.algo.graph.DirectedGraphAlgorithms;
import org.jbpt.algo.tree.tctree.TCTree;
import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.graph.abs.AbstractMultiDirectedGraph;
import org.jbpt.graph.abs.AbstractTree;
import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.graph.abs.IDirectedGraph;
import org.jbpt.hypergraph.abs.IVertex;


/**
 * This class takes a multi-terminal graph and computes its Refined Process Structure Tree (RPST).<br/><br/>
 * 
 * NOTE THAT GIVEN GRAPH MUST BE MULTI-TERMINAL; OTHERWISE RESULT IS UNEXPECTED.<br/><br/>
 * 
 * The RPST of a multi-terminal graph is a containment hierarchy of all canonical fragments the graph. 
 * A fragment is a single-entry-single-exit (SESE) subgraph of the graph. 
 * A fragment of the graph is canonical if it does not overlap (on edges) with any other fragment of the graph. 
 * Every canonical fragment is induced by a triconnected component of the graph, 
 * @see {@link TCTree}, and, thus, inherits its type, @see {@link TCType}. 
 * If a canonical fragment is induced by a modified triconnected component - the flag isQuasi is set in {@link RPSTNode}. 
 * Note that every edge is a trivial fragment, but is not explicitly computed by this class.<br/><br/>
 * 
 * This class implements the RPST algorithm proposed in (refer for details): 
 * Artem Polyvyanyy, Jussi Vanhatalo, and Hagen Voelzer. 
 * Simplified Computation and Generalization of the Refined Process Structure Tree. 
 * Proceedings of the 7th International Workshop on Web Services and Formal Methods (WS-FM). 
 * Hoboken, NJ, US, September 2010.
 * 
 * @see {@link DirectedGraphAlgorithm.isMultiTerminal} for checking if a graph is multi-terminal.
 * 
 * @param <E> Edge template.
 * @param <V> Vertex template.
 * 
 * @author Artem Polyvyanyy
 * 
 * @assumption Given graph is multi-terminal, see {@code DirectedGraphAlgorithms.isMultiTerminal}.
 */
public class RPST<E extends IDirectedEdge<V>, V extends IVertex> extends AbstractTree<RPSTNode<E,V>> {
	// Original graph to decompose
	protected IDirectedGraph<E,V> diGraph = null;
	// Enhanced version of original graph
	private AbstractMultiDirectedGraph<E,V> enhancedGraph = null;
	
	protected Set<E> extraEdges = new HashSet<E>();
	
	protected TCTree<E,V> tctree = null;
	
	protected DirectedGraphAlgorithms<E,V> DGA = new DirectedGraphAlgorithms<E,V>();
	
	private Map<E,E> n2o = null;
	
	public RPST(IDirectedGraph<E,V> graph) {
		if (graph==null) return;
		if (graph.getEdges().isEmpty()) return;
		
		this.n2o = new HashMap<E,E>();
		
		this.diGraph = graph;
		
		this.constructEnhancedGraph();
		
		this.tctree = new TCTree<E,V>(this.enhancedGraph);
		
		this.constructRPST();
	}

	/**
	 * Get original graph.
	 * @return Original graph.
	 */
	public IDirectedGraph<E,V> getGraph() {
		return this.diGraph;
	}
	
	private void constructEnhancedGraph() {
		this.enhancedGraph = new AbstractMultiDirectedGraph<E,V>();
		
		for (V v : this.diGraph.getVertices())
			this.enhancedGraph.addVertex(v);
		
		for (E e : this.diGraph.getEdges())
			this.n2o.put(this.enhancedGraph.addEdge(e.getSource(), e.getTarget()), e);
		
		// TODO
	}
	
	private void constructRPST() {
		// TODO
	}

}
