package org.jbpt.algo.tree.rpst;

import org.jbpt.algo.tree.tctree.TCTreeNode;
import org.jbpt.graph.Fragment;
import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.hypergraph.abs.IVertex;


/**
 * Implementation of the node of the RPST.
 * 
 * @author Artem Polyvyanyy
 *
 * @param <E> Edge template.
 * @param <V> Vertex template.
 */
public class RPSTNode<E extends IDirectedEdge<V>, V extends IVertex> extends TCTreeNode<E,V> {

	protected boolean isQuasi = false;
	
	protected V entry = null;
	
	protected V exit = null;
	
	protected Fragment<E,V> fragment = null;

	
	
}
