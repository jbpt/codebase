package org.jbpt.algo.tree.rpst;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jbpt.algo.tree.tctree.TCTree;
import org.jbpt.algo.tree.tctree.TCTreeNode;
import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.MultiDirectedGraph;
import org.jbpt.graph.abs.AbstractTree;
import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.graph.abs.IDirectedGraph;
import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.hypergraph.abs.Vertex;


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
	// original graph to decompose
	protected IDirectedGraph<E,V> diGraph = null;
	// normalized version of original graph
	private MultiDirectedGraph normalizedGraph = null;
	
	protected Set<DirectedEdge> extraEdges = new HashSet<DirectedEdge>();
	protected TCTree<DirectedEdge,Vertex> tctree = null;
	private DirectedEdge backEdge = null;
	
	protected Map<DirectedEdge,E> ne2oe = null;
	private Map<V,Vertex> ov2nv = null;
	
	/**
	 * Constructor.
	 * 
	 * @param graph A graph to build RPST for.
	 */
	public RPST(IDirectedGraph<E,V> graph) {
		if (graph==null) return;
		if (graph.getEdges().isEmpty()) return;
		
		this.ne2oe = new HashMap<DirectedEdge,E>();
		this.ov2nv = new HashMap<V,Vertex>();
		
		this.diGraph = graph;
		
		this.normalizeGraph();
		
		this.tctree = new TCTree<DirectedEdge,Vertex>(this.normalizedGraph,this.backEdge);
		
		this.constructRPST();
	}

	/**
	 * Get original graph.
	 * 
	 * @return Original graph.
	 */
	public IDirectedGraph<E,V> getGraph() {
		return this.diGraph;
	}
	
	/**
	 * Get RPST nodes induced by triconnected components of a given {@link TCType} class.
	 * 
	 * @param {@link TCType} class.
	 * @return Collection of RPST nodes induced by the given {@link TCType} class.
	 */
	public Collection<RPSTNode<E,V>> getRPSTNodes(TCType type) {
		Set<RPSTNode<E,V>> result = new HashSet<RPSTNode<E,V>>();
		for (RPSTNode<E,V> node : this.getVertices())
			if (node.getType()==type)
				result.add(node);
		
		return result;
	}
	
	/**
	 * Get RPST nodes.
	 * 
	 * @return Collection of RPST nodes.
	 */
	public Collection<RPSTNode<E,V>> getRPSTNodes() {
		return this.getVertices();
	}
	
	private void normalizeGraph() {
		this.normalizedGraph = new MultiDirectedGraph();
		
		Collection<V> sources = new ArrayList<V>();
		Collection<V> sinks = new ArrayList<V>();
		Collection<V> mixed = new ArrayList<V>();
		
		// copy vertices
		for (V v : this.diGraph.getVertices()) {
			if (this.diGraph.getIncomingEdges(v).isEmpty()) 
				sources.add(v);
			
			if (this.diGraph.getOutgoingEdges(v).isEmpty()) 
				sinks.add(v);
			
			if (this.diGraph.getIncomingEdges(v).size()>1 && this.diGraph.getOutgoingEdges(v).size()>1) 
				mixed.add(v);
			
			this.ov2nv.put(v,this.normalizedGraph.addVertex(new Vertex(v.getName())));
		}
		
		// copy edges 
		for (E e : this.diGraph.getEdges())
			this.ne2oe.put(this.normalizedGraph.addEdge(this.ov2nv.get(e.getSource()), this.ov2nv.get(e.getTarget())), e);
		
		// introduce single source
		Vertex src = new Vertex("SRC");
		for (V v : sources)
			this.extraEdges.add(this.normalizedGraph.addEdge(src,this.ov2nv.get(v)));
		
		// introduce single sink
		Vertex snk = new Vertex("SNK");
		for (V v : sinks)
			this.extraEdges.add(this.normalizedGraph.addEdge(this.ov2nv.get(v),snk));
		
		// split mixed 'gateways', i.e., vertices with multiple inputs and outputs
		for (V v : mixed) {
			Vertex vertex = new Vertex(v.getName()+"*");
			
			for (DirectedEdge edge : this.normalizedGraph.getIncomingEdges(this.ov2nv.get(v))) {
				this.normalizedGraph.removeEdge(edge);
				E e = this.ne2oe.remove(edge);
				DirectedEdge ee = this.normalizedGraph.addEdge(this.ov2nv.get(e.getSource()),vertex);
				this.ne2oe.put(ee, e);
			}
			
			this.extraEdges.add(this.normalizedGraph.addEdge(vertex,this.ov2nv.get(v)));
		}
		
		this.backEdge = this.normalizedGraph.addEdge(snk,src);
		this.extraEdges.add(this.backEdge);
	}
	
	private void constructRPST() {
		// remove extra edges
		for (TCTreeNode<DirectedEdge,Vertex> node : this.tctree.getVertices()) {
			Set<DirectedEdge> edges = new HashSet<DirectedEdge>(node.getSkeleton().getOriginalEdges());
			for (DirectedEdge edge : edges) {
				if (this.extraEdges.contains(edge)) {
					node.getSkeleton().removeOriginalEdge(edge);
				}
			}
		}
		
		Collection<TCTreeNode<DirectedEdge,Vertex>> nodes = new ArrayList<TCTreeNode<DirectedEdge,Vertex>>(this.tctree.getTCTreeNodes());
		for (TCTreeNode<DirectedEdge,Vertex> node : nodes) {
			if (this.tctree.getChildren(node).size()==1 && node.getSkeleton().getOriginalEdges().isEmpty()) 
			{
				TCTreeNode<DirectedEdge,Vertex> child = this.tctree.getChildren(node).iterator().next();
				
				if (this.tctree.isRoot(node)) {
					this.tctree.removeVertex(node);
					this.tctree.reRoot(child);
				}
				else {
					TCTreeNode<DirectedEdge,Vertex> parent = this.tctree.getParent(node);
					this.tctree.removeVertex(node);
					this.tctree.addEdge(parent,child);
				}
			}
		}
		
		nodes = new ArrayList<TCTreeNode<DirectedEdge,Vertex>>(this.tctree.getTCTreeNodes());
		for (TCTreeNode<DirectedEdge,Vertex> node : nodes) {
			if (node.getType()==TCType.POLYGON && this.tctree.getChildren(node).isEmpty() && node.getSkeleton().getOriginalEdges().size()==1) {
				DirectedEdge edge = node.getSkeleton().getOriginalEdges().iterator().next();
				this.tctree.getParent(node).getSkeleton().addEdge(edge.getSource(), edge.getTarget(), edge);
				this.tctree.removeVertex(node);
			}
		}
		
		// construct RPST nodes
		Map<TCTreeNode<DirectedEdge,Vertex>,RPSTNode<E,V>> t2r = new HashMap<TCTreeNode<DirectedEdge,Vertex>,RPSTNode<E,V>>();
		
		for (IDirectedEdge<TCTreeNode<DirectedEdge,Vertex>> edge : this.tctree.getEdges()) {
			TCTreeNode<DirectedEdge,Vertex> src = edge.getSource();
			TCTreeNode<DirectedEdge,Vertex> tgt = edge.getTarget();
			
			RPSTNode<E,V> rsrc = t2r.get(src);
			RPSTNode<E,V> rtgt = t2r.get(tgt);
			
			if (rsrc==null) {
				rsrc = new RPSTNode<E,V>(this, src);
				t2r.put(src, rsrc);
			}
				
			if (rtgt==null) {
				rtgt = new RPSTNode<E,V>(this, tgt);
				t2r.put(tgt, rtgt);
			}
			
			if (this.tctree.isRoot(src)) this.root = rsrc;
			if (this.tctree.isRoot(tgt)) this.root = rtgt;
			
			this.addEdge(rsrc,rtgt);
		}
	}
	
	/*public void debug() {
		System.out.println("DEBUG");
		IOUtils.toFile("original.dot", this.diGraph.toDOT());
		IOUtils.toFile("normalized.dot", this.normalizedGraph.toDOT());
		IOUtils.toFile("tctree.dot", this.tctree.toDOT());
		
		for (TCTreeNode<DirectedEdge,Vertex> node : this.tctree.getVertices()) {
			IOUtils.toFile(node.getName()+".dot", node.getSkeleton().toDOT());
		}
	}*/
}
