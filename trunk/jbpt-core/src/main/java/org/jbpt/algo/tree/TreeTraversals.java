package org.jbpt.algo.tree;

import java.util.ArrayList;
import java.util.List;

import org.jbpt.graph.abs.ITree;
import org.jbpt.hypergraph.abs.IVertex;

/**
 * Collection of tree traversal techniques.
 * 
 * @author Artem Polyvyanyy
 *
 * @param <V> Vertex template.
 */
public class TreeTraversals<V extends IVertex> {

	/**
	 * Traverse a tree in a post order
	 * @param tree Tree to traverse.
	 * @return List of vertices of the tree in the order of post order traversal. 
	 */
	public List<V> postOrderTraversal(ITree<V> tree) {
		List<V> result = new ArrayList<V>();
		if (tree == null) return result;
		
		V root = tree.getRoot();
		for (V v : tree.getChildren(root)) {
			postOrderTraversal(tree,v,result);
		}
	
		result.add(root);
		return result;
	}

	private void postOrderTraversal(ITree<V> tree, V v, List<V> result) {
		for (V vv : tree.getChildren(v)) {
			postOrderTraversal(tree,vv,result);
		}
		
		result.add(v);
	}
}
