package org.jbpt.algo.tree.rpst;

import java.util.List;
import java.util.Set;

import org.jbpt.algo.tree.tctree.TCTree;
import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.graph.abs.IDirectedGraph;
import org.jbpt.graph.abs.ITree;
import org.jbpt.hypergraph.abs.IVertex;

/**
 * Interface to the Refined Process Structure Tree (RPST).<br/><br/>
 * 
 * NOTE THAT GIVEN GRAPH MUST BE MULTI-TERMINAL; OTHERWISE RESULT IS UNEXPECTED.<br/><br/>
 * 
 * The RPST of a multi-terminal graph is a containment hierarchy of all canonical fragments the graph. 
 * A fragment is a single-entry-single-exit (SESE) subgraph of the graph. 
 * A fragment of the graph is canonical if it does not overlap (on edges) with any other fragment of the graph. 
 * Every canonical fragment is induced by a triconnected component of the graph, 
 * @see {@link TCTree}, and, thus, inherits its type, @see {@link TCType}. 
 * 
 * One can implement the RPST by following the algorithm proposed in: 
 * Artem Polyvyanyy, Jussi Vanhatalo, and Hagen Voelzer. 
 * Simplified Computation and Generalization of the Refined Process Structure Tree. 
 * Proceedings of the 7th International Workshop on Web Services and Formal Methods (WS-FM). 
 * Hoboken, New Jersey, USA, September 2010.
 * 
 * @see {@link DirectedGraphAlgorithm.isMultiTerminal} for checking if a graph is multi-terminal.
 * @see {@link RPST} for an implementation of this interface. 
 * 
 * @param <E> Edge template.
 * @param <V> Vertex template.
 * 
 * @author Artem Polyvyanyy
 * 
 * @assumption Given graph is multi-terminal, see {@code DirectedGraphAlgorithms.isMultiTerminal}.
 */
public interface IRPST<E extends IDirectedEdge<V>, V extends IVertex> extends ITree<IRPSTNode<E,V>> {

	/**
	 * Get original graph.
	 * 
	 * @return Original graph.
	 */
	public IDirectedGraph<E,V> getGraph();

	/**
	 * Get RPST nodes induced by the triconnected components of a given {@link TCType} type.
	 * 
	 * @param {@link TCType} type.
	 * @return Set of RPST nodes induced by the given {@link TCType} type.
	 */
	public Set<IRPSTNode<E,V>> getRPSTNodes(TCType type);

	/**
	 * Get RPST nodes.
	 * 
	 * @return Set of RPST nodes.
	 */
	public Set<IRPSTNode<E,V>> getRPSTNodes();

	/**
	 * Get ordered children of a polygon fragment. 
	 * A polygon is a sequence of other fragments such that the exit of the i-th fragment in the sequence is the entry to the (i+1)-th fragment.<br/><br/> 
	 * 
	 * NOTE THAT THE ENTRY OF THE FIRST AND THE EXIT OF THE LAST FRAGMENT CAN BE NULL. 
	 * THIS IS THE CASE IF THE POLYGON CONTAINS MULTIPLE SOURCES AND/OR SINKS OF THE GRAPH.
	 * 
	 * @param node Node of the RPST. 
	 * @return An ordered list of polygon child fragments; the order is random if node is not of type polygon.
	 */
	public List<IRPSTNode<E,V>> getPolygonChildren(IRPSTNode<E,V> node);

}