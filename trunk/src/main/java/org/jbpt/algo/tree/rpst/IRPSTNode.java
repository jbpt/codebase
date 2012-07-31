package org.jbpt.algo.tree.rpst;

import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.graph.abs.IFragment;
import org.jbpt.hypergraph.abs.IVertex;

/**
 * Interface to an RPST node.
 *
 * @param <E> Edge template.
 * @param <V> Vertex template.
 *
 * @author Artem Polyvyanyy
 */
public interface IRPSTNode<E extends IDirectedEdge<V>, V extends IVertex> extends IVertex {

	/**
	 * Get type of the triconnected component which induces this fragment.
	 * 
	 * @return Type of the triconnected component which induces this fragment. 
	 */
	public TCType getType();

	/**
	 * Get entry of the fragment represented by this RPST node.<br/><br/>
	 * 
	 * NOTE THAT ENTRY CAN BE EQUAL TO EXIT! THIS IS THE CASE WHEN THERE ARE CUTVERTICES IN THE GRAPH! 
	 * SEE SECTION 4.1 IN: 
	 * Artem Polyvyanyy, Jussi Vanhatalo, and Hagen Voelzer. 
	 * Simplified Computation and Generalization of the Refined Process Structure Tree. 
	 * Proceedings of the 7th International Workshop on Web Services and Formal Methods (WS-FM).<br/><br/>
	 * 
	 * NOTE THAT ENTRY CAN BE NULL! THIS IS THE CASE WHEN MULTIPLE SOURCES OF THE GRAPH BELONG TO THE FRAGMENT!
	 * SEE SECTION 4.2 IN: 
	 * Artem Polyvyanyy, Jussi Vanhatalo, and Hagen Voelzer. 
	 * Simplified Computation and Generalization of the Refined Process Structure Tree. 
	 * Proceedings of the 7th International Workshop on Web Services and Formal Methods (WS-FM).
	 * 
	 * @return Entry of the fragment.
	 */
	public V getEntry();

	/**
	 * Get exit of the fragment represented by this RPST node.<br/><br/>
	 * 
	 * NOTE THAT ENTRY CAN BE EQUAL TO EXIT! THIS IS THE CASE WHEN THERE ARE CUTVERTICES IN THE GRAPH! 
	 * SEE SECTION 4.1 IN: 
	 * Artem Polyvyanyy, Jussi Vanhatalo, and Hagen Voelzer. 
	 * Simplified Computation and Generalization of the Refined Process Structure Tree. 
	 * Proceedings of the 7th International Workshop on Web Services and Formal Methods (WS-FM).<br/><br/>
	 * 
	 * NOTE THAT EXIT CAN BE NULL! THIS IS THE CASE WHEN MULTIPLE SINKS OF THE GRAPH BELONG TO THE FRAGMENT!
	 * SEE SECTION 4.2 IN: 
	 * Artem Polyvyanyy, Jussi Vanhatalo, and Hagen Voelzer. 
	 * Simplified Computation and Generalization of the Refined Process Structure Tree. 
	 * Proceedings of the 7th International Workshop on Web Services and Formal Methods (WS-FM).
	 * 
	 * @return Exit of the fragment.
	 */
	public V getExit();

	/**
	 * Get fragment represented by this RPST node.
	 * 
	 * @return Fragment represented by this RPST node.
	 */
	public IFragment<E,V> getFragment();

}