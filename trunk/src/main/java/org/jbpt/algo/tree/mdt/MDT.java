package org.jbpt.algo.tree.mdt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpt.algo.tree.mdt.ComponentGraph;
import org.jbpt.algo.tree.mdt.MDTNode;
import org.jbpt.graph.abs.AbstractDirectedGraph;
import org.jbpt.graph.abs.AbstractTree;
import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.hypergraph.abs.IVertex;

/**
 * This class computes the Modular Decomposition Tree of a directed graph.
 * It implements the algorithm described in the following article:
 * 
 * A. Ehrenfeucht, H.N. Gabow, R.M. McConnell, and S.J. Sullivan
 * An O(n^2) Divide-and Conquer Algorithm for the Prime Tree Decomposition of
 * Two-Structures and Modular Decomposition of Graphs
 * JOURNAL OF ALGORITHMS 16, 283-294 (1994)
 * 
 * @author Luciano Garcia-Banuelos
 */
public class MDT<E extends IDirectedEdge<V>, V extends IVertex> extends AbstractTree<IMDTNode<E,V>> {
	private AbstractDirectedGraph<E, V> graph;

	public MDT(AbstractDirectedGraph<E, V> graph) {
		this.graph = graph;
		this.root = decompose(graph.getVertices());
	}

	/**
	 * Given vertex v, this method partitions a set of vertices into four
	 * partitions according to their connectivity pattern with v:
	 * 	1:	Bidirectional connected
	 * 	2:	Directed edge having v as target vertex
	 * 	3:	Directed edge having v as source vertex
	 *	4:	Disconnected
	 *
	 * @param vertices	Set of vertices to partition
	 * @param v			Vertex to guide partitioning
	 */
	private List<Set<V>> partitionSubsets(Collection<V> vertices, V v) {		
		List<Set<V>> partitions = new ArrayList<Set<V>>(4);
		for (int i = 0; i<4; i++)
			partitions.add(new HashSet<V>());
		
		for (V w: vertices) {
			E w_v = graph.getDirectedEdge(w, v);
			E v_w = graph.getDirectedEdge(v, w);
			
												// Four cases:
			if (w_v != null && v_w != null)		// (w,v),(v,w) \in E(G)
				partitions.get(0).add(w);
			else if (w_v != null)				// (w,v) \in E(G) /\ (v,w) \nin E(G)
				partitions.get(1).add(w);
			else if (v_w != null)				// (w,v) \nin E(G) /\ (v,w) \in E(G)
				partitions.get(2).add(w);
			else								// (w,v), (v,w) \nin E(G)
				partitions.get(3).add(w);				
		}
		
		return partitions ;
	}
	
	/**
	 * Algorithm 3.1 Compute M(g, v)
	 * 
	 * @param vertices AKA dom(g) in the reference paper, corresponds with the set of vertices of graph g
	 * @param v		   vertex used for partitioning
	 * @return
	 */
	private Collection<Set<V>> partition(Collection<V> vertices, V v) {
		// L - Family of partition classes 
		Set<Set<V>> l = new HashSet<Set<V>>();
		// Z - Unprocessed outsiders
		Map<Set<V>, Set<V>> z = new HashMap<Set<V>, Set<V>>();
		
		// Place holder
		Set<Set<V>> result = new LinkedHashSet<Set<V>>();
		
		// Initially, there is one partition class S = V(g) \ {v} in L
		Set<V> s = new HashSet<V>(vertices);
		s.remove(v);
		l.add(s);
		
		// with Z(S) = {v}
		Set<V> _v_ = new HashSet<V>();
		_v_.add(v);
		z.put(s, _v_);
		
		while(!l.isEmpty()) {
			// Remove S from L
			s = l.iterator().next(); l.remove(s);
			
			// Let w be an arbitrary member of Z(S)
			V w = z.get(s).iterator().next();
			
			// Partition S into maximal subsets that are not distinguished by w
			// -- for each resulting subset W
			for (Set<V> W: partitionSubsets(s, w)) {
				if (W.isEmpty()) continue;
				
				// Let Z(W) = (S \ W) \cup Z(S) \ {w}
				Set<V> tmp = new HashSet<V>(s);
				tmp.removeAll(W);
				tmp.addAll(z.get(s));
				tmp.remove(w);
				
				if (!tmp.isEmpty()) {
					// Make W a member of L
					l.add(W);
					z.put(W, tmp); // Actual assignment to Z
				} else
					result.add(W);
			}
		}
		
		return result;
	}

	/**
	 * Algorithm 6.1 Compute the PRIME TREE FAMILY (aka Modular Decomposition Tree) for
	 * an arbitrary two-structure.
	 * 
	 * @param dom
	 * @return
	 */
	private IMDTNode<E,V> decompose(Collection<V> dom) {
		if (dom.size() == 0) return null; // Nothing to do
		
		// Select one vertex from dom
		V v = dom.iterator().next();
		
		// Create a node in the MDT
		MDTNode<E,V> t = new MDTNode<E,V>(this, dom, v);
		addVertex(t);
		
		// Dom is a singleton, then t is a TRIVIAL
		if (dom.size() == 1) return t;
		
		Collection<Set<V>> m = partition(dom, v);
		
		ComponentGraph<E,V> gpp = new ComponentGraph<E,V>(graph, m, v);
		MDTNode<E,V> u = t;
		
		while (gpp.getVertices().size() > 0) {
			Set<V> tmp = gpp.getPartitionUnion();
			tmp.add(v);
			u.setClan(tmp);
			
			tmp = new HashSet<V>();
			tmp.add(v);
			MDTNode<E,V> w = new MDTNode<E,V>(this, tmp, v);
			addVertex(w);
			addChild(u, w);
			
			Set<V> sinks = gpp.getSinkNodes();
			Set<Set<V>> F = gpp.getPartitions(sinks);
			gpp.removeVertices(sinks);
			
			if (sinks.size() == 1 && F.size() > 1)
				u.setType(MDTType.PRIMITIVE);
			else {
				V x = F.iterator().next().iterator().next();
				
				if ((graph.getDirectedEdge(v, x) != null && graph.getDirectedEdge(x, v) != null) ||
						(!(graph.getDirectedEdge(v, x) != null) && !(graph.getDirectedEdge(x, v) != null))) {
					u.setType(MDTType.COMPLETE);
					u.setColor(graph.getDirectedEdge(v, x) != null ? 1 : 0);
				} else
					u.setType(MDTType.LINEAR);
			}
			
			for (Set<V> partition: F) {	
				IMDTNode<E,V> root = decompose(partition);
				if (((u.getType() == MDTType.COMPLETE && root.getType() == MDTType.COMPLETE) ||
						(u.getType() == MDTType.LINEAR && root.getType() == MDTType.LINEAR)) &&
						u.getColor() == root.getColor())
					
					for (IMDTNode<E,V> child: getChildren(root))
						addChild(u, child);
				else
					addChild(u, root);
			}
			u = w;
		}
		return t;
	}
	
	@Override
	public String toString() {
		return root.toString();
	}
	
	@Override
	public IMDTNode<E,V> reRoot(IMDTNode<E,V> v) {
		throw new UnsupportedOperationException("An MDT cannot be modified!");
	}
}
