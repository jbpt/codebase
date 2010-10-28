/**
 * Copyright (c) 2010 Artem Polyvyanyy, Christian Wiggert
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.hpi.bpt.graph.algo.tctree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.hpi.bpt.abstraction.TriAbstractionStepInfo;
import de.hpi.bpt.graph.abs.AbstractDirectedGraph;
import de.hpi.bpt.graph.abs.AbstractMultiGraphFragment;
import de.hpi.bpt.graph.abs.IDirectedEdge;
import de.hpi.bpt.graph.abs.IEdge;
import de.hpi.bpt.graph.abs.IGraph;
import de.hpi.bpt.graph.algo.GraphAlgorithms;
import de.hpi.bpt.graph.util.GMLUtils;
import de.hpi.bpt.hypergraph.abs.IGObject;
import de.hpi.bpt.hypergraph.abs.IVertex;

/**
 * SPQR-tree decomposition of the biconnected multi graph with respect to its triconnected components
 * @author Artem Polyvyanyy
 * @author Christian Wiggert
 *
 * @param <E> template for edge (extends IEdge)
 * @param <V> template for vertex (extends IVertex)
 */
public class TCTree<E extends IEdge<V>, V extends IVertex> extends AbstractDirectedGraph<TCTreeEdge<E,V>, TCTreeNode<E,V>> {
	private IGraph<E,V> graph;
	
	private static int n = 0;
	
	private Collection<TCTreeNode<E,V>> nodes = new ArrayList<TCTreeNode<E,V>>();
	
	private E backEdge;
	
	private GraphAlgorithms<E,V> ga = new GraphAlgorithms<E,V>();
	
	private TCTreeNode<E,V> root = null;
	
	/*
	 * (non-Javadoc)
	 * @see de.hpi.bpt.graph.abs.AbstractDirectedGraph#addEdge(de.hpi.bpt.hypergraph.abs.IVertex, de.hpi.bpt.hypergraph.abs.IVertex)
	 */
	@Override
	public TCTreeEdge<E,V> addEdge(TCTreeNode<E,V> v1, TCTreeNode<E,V> v2) {
		if (v1 == null || v2 == null) return null;
		
		Collection<TCTreeNode<E,V>> ss = new ArrayList<TCTreeNode<E,V>>(); ss.add(v1);
		Collection<TCTreeNode<E,V>> ts = new ArrayList<TCTreeNode<E,V>>(); ts.add(v2);
		
		if (!this.checkEdge(ss, ts)) return null;
		
		return new TCTreeEdge<E,V>(this, v1, v2);
	}
	
	/**
	 * Constructor
	 * @param g
	 */
	public TCTree(IGraph<E,V> g) {
		if (g==null) return;
		this.graph = g;
		
		//graph must have one entry and one exit
		Collection<V> bs = ga.getBoundaryVertices(this.graph);
		if (bs.size()!=2) return;
		if (isTrivialCase()) {
			TCTreeSkeleton<E,V> sk = new TCTreeSkeleton<E,V>(this.graph);
			sk.copyOriginalGraph();
			this.root = new TCTreeNode<E,V>("T0");
			root.setSkeleton(sk);
			root.setType(TCType.T);
			this.addVertex(this.root);
			this.root.setBoundaryNodes(this.graph.getVertices());
		} else {
			TCTreeSkeleton<E,V> sk = new TCTreeSkeleton<E,V>(this.graph);
			sk.copyOriginalGraph();
			Iterator<V> bi = bs.iterator();
			this.backEdge = sk.addVirtualEdge(bi.next(), bi.next());
			// graph must be biconnected
			BiconnectivityCheck<E,V> check = new BiconnectivityCheck<E,V>(sk);
			if (!check.isBiconnected()) return;
			//if (!ga.isConnected(sk, 1)) return;
	
			// make first node
			this.root = new TCTreeNode<E,V>("0");
			root.setSkeleton(sk);
	
			// decompose
			ModelDecomposer<E, V> decomposer = new ModelDecomposer<E, V>();
			Collection<TCTreeNode<E, V>> newNodes = decomposer.getTriconnectedComponents(graph, root, backEdge);
			if (newNodes != null) {
				for (TCTreeNode<E, V> node:newNodes) {
					this.putNode(node);
				}
			}
			// classify node types
			this.classifyNodes();
			
			Iterator<TCTreeNode<E,V>> ni = this.nodes.iterator();
			
			// construct tree
			this.constructTree();
			
			nodes.clear();
			nodes.addAll(this.getVertices());
			this.nodes.remove(this.root);
			
			ni = this.nodes.iterator();
			while (ni.hasNext()) {
				TCTreeNode<E,V> n = ni.next();
				List<V> bns = new ArrayList<V>(n.getBoundaryNodes());
				this.classifyBoundaryNode(n,bns.get(0));
				this.classifyBoundaryNode(n,bns.get(1));
			}
					
			this.nodes.clear();
		}
	}
	
	private boolean isTrivialCase() {
		if (this.graph.getEdges().size() == 1 && !(this.graph.getVertices().size() > 2))
			return true;
		return false;
	}
	
	/**
	 * Get original graph
	 * @return Graph
	 */
	public IGraph<E,V> getGraph() {
		return this.graph;
	}
	
	/**
	 * Add TC-tree node, if it was not added before
	 * @param node Node to add
	 */
	private void putNode(TCTreeNode<E,V> node) {
		Iterator<TCTreeNode<E,V>> i = this.nodes.iterator();
		
		Collection<V> vs1 = node.getSkeleton().getVertices();
		boolean flag = true;
		while (i.hasNext()) {
			TCTreeNode<E,V> n = i.next();
			
			Collection<V> vs2 = n.getSkeleton().getVertices();
			
			if (vs2.containsAll(vs1) && vs2.size()==vs1.size()) {
				flag = false;
				break;
			}
		}
		
		if (flag)
			this.nodes.add(node);
	}
	
	/**
	 * @param type
	 * @return
	 */
	public Collection<TCTreeNode<E,V>> getVertices(TCType type) {
		Collection<TCTreeNode<E,V>> result = new ArrayList<TCTreeNode<E,V>>();
		
		Iterator<TCTreeNode<E,V>> i = this.getVertices().iterator();
		while (i.hasNext()) {
			TCTreeNode<E,V> n = i.next();
			if (n.getType()==type)
				result.add(n);
		}
		
		return result;
	}
	
	/**
	 * Classify TCTree nodes on types: S,P,R
	 */
	private void classifyNodes() {
		int Pc, Bc, Rc;
		Pc = Bc = Rc = 0;
		
		Iterator<TCTreeNode<E,V>> i = this.nodes.iterator();
		while (i.hasNext()) {
			TCTreeNode<E,V> n = i.next();
			
			if (n.getSkeleton().countVertices()==2) { n.setType(TCType.B); n.setName("B" + Bc++); continue; }
			
			boolean isS = true;
			Iterator<V> vs = n.getSkeleton().getVertices().iterator();
			while (vs.hasNext()) {
				V v = vs.next();
				if (n.getSkeleton().getEdges(v).size()!=2) {
					isS = false;
					break;
				}
			}
			
			if (isS) {
				n.setType(TCType.P);
				n.setName("P" + Pc++);
			}
			else {
				n.setType(TCType.R);
				n.setName("R" + Rc++);
			}
		}
	}
	
	private void constructTree() {
		GMLUtils<E, V> gmlV = new GMLUtils<E, V>();
		
		// get root node - a node with back edge
		Iterator<TCTreeNode<E,V>> i = this.nodes.iterator();
		while (i.hasNext()) {
			TCTreeNode<E,V> n = i.next();
			E e = n.getSkeleton().getEdge(this.backEdge.getV1(), this.backEdge.getV2());
			if (e!=null && n.getSkeleton().isVirtual(e))
			{				
				this.root = n;
				gmlV.serialize(n.getSkeleton(), "root.gml");
				break;
			}
		}
		if (this.root == null) return;
		this.addVertex(this.root);
		this.root.setBoundaryNodes(this.backEdge.getVertices());
		
		Iterator<E> esi = this.graph.getEdges(this.backEdge.getV1()).iterator();
		if (esi.hasNext()) {
			E es = esi.next();
			if (es instanceof IDirectedEdge<?>) {
				IDirectedEdge<V> de = (IDirectedEdge<V>) es;
				if (de.getSource().equals(this.backEdge.getV1())) this.root.setEntry(this.backEdge.getV1());
				if (de.getTarget().equals(this.backEdge.getV1())) this.root.setExit(this.backEdge.getV1());
			}
		}
		
		esi = this.graph.getEdges(this.backEdge.getV2()).iterator();
		if (esi.hasNext()) {
			E es = esi.next();
			if (es instanceof IDirectedEdge<?>) {
				IDirectedEdge<V> de = (IDirectedEdge<V>) es;
				if (de.getSource().equals(this.backEdge.getV2())) this.root.setEntry(this.backEdge.getV2());
				if (de.getTarget().equals(this.backEdge.getV2())) this.root.setExit(this.backEdge.getV2());
			}
		}
		
		Collection<TCTreeNode<E,V>> ns = new ArrayList<TCTreeNode<E,V>>(this.nodes);
		ns.remove(this.root);
		constructChildren(this.root,ns);
	}
	
	private void classifyBoundaryNode(TCTreeNode<E,V> n, V v) {
		if (n.getSkeleton() == null) return;
		
		Collection<E> sEdges = this.getFragment(n).getEdges(v);
		Collection<E> gEdges = this.graph.getEdges(v);
		
		if (sEdges.size()==0 || gEdges.size()==0) return;
		
		int sIn, sOut, gIn, gOut;
		sIn = sOut = gIn = gOut = 0;
		
		
		Iterator<E> i = sEdges.iterator();
		while (i.hasNext()) {
			E e = i.next();
			
			E ge = this.graph.getEdge(e.getV1(), e.getV2());
			if (!(ge instanceof IDirectedEdge<?>)) return;
			
			IDirectedEdge<V> de = (IDirectedEdge<V>) ge;
			
			if (de.getTarget().equals(v)) sIn++;
			else sOut++;
		}
		
		i = gEdges.iterator();
		while (i.hasNext()) {
			E e = i.next();
			if (!(e instanceof IDirectedEdge<?>)) return;
			
			IDirectedEdge<V> de = (IDirectedEdge<V>) e;
			if (de.getTarget().equals(v)) gIn++;
			else gOut++;
		}
		
		if (sIn == 0 || gOut-sOut == 0) n.setEntry(v);
		if (sOut == 0 || gIn-sIn == 0) n.setExit(v);
	}

	
	private void constructChildren(TCTreeNode<E,V> n, Collection<TCTreeNode<E,V>> ns)
	{
		Collection<TCTreeNode<E,V>> nss = new ArrayList<TCTreeNode<E,V>>(ns);
		Collection<TCTreeNode<E,V>> ncs = new ArrayList<TCTreeNode<E,V>>();
		Collection<TCTreeNode<E,V>> nps = new ArrayList<TCTreeNode<E,V>>();
		
		Iterator<E> i = n.getSkeleton().getVirtualEdges().iterator();
		while (i.hasNext()) {
			E ve = i.next();
			
			Iterator<TCTreeNode<E,V>> j = ns.iterator();
			while (j.hasNext()) {
				TCTreeNode<E,V> vn = j.next();
				
				Collection<E> ves = vn.getSkeleton().getEdges(ve.getV1(), ve.getV2());
				if (this.containsVirtual(vn, ves)) {
					
					vn.setBoundaryNodes(ve.getVertices());
					
					nss.remove(vn);
					
					if (vn.getType() == TCType.P)
						nps.add(vn);
					else {
						ncs.add(vn);
					}
				}
			}
		}
		
		// first work with Ps
		Collection<TCTreeNode<E,V>> cncs = new ArrayList<TCTreeNode<E,V>>(ncs);
		Iterator<TCTreeNode<E,V>> j = nps.iterator();
		while (j.hasNext()) {
			TCTreeNode<E,V> p = j.next();
			
			// look for children
			Collection<TCTreeNode<E,V>> npc = new ArrayList<TCTreeNode<E,V>>();
			
			Iterator<TCTreeNode<E,V>> k = cncs.iterator();
			while (k.hasNext()) {
				TCTreeNode<E,V> cc = k.next();
				
				Iterator<V> pi = p.getSkeleton().getVertices().iterator();
				
				Collection<E> ves = cc.getSkeleton().getEdges(pi.next(), pi.next());
				if (this.containsVirtual(cc, ves)) {
					npc.add(cc);
					nss.remove(cc);
					ncs.remove(cc);
				}
			}
			
			// call recursion
			this.addEdge(n,p);
			Collection<TCTreeNode<E,V>> u = new ArrayList<TCTreeNode<E,V>>(nss);
			u.addAll(npc);
			this.constructChildren(p,u);
		}
		
		// now rest
		j = ncs.iterator();
		while (j.hasNext()) {
			TCTreeNode<E,V> vn = j.next();
			this.addEdge(n,vn);
			this.constructChildren(vn,nss);
		}
	}
	
	private boolean containsVirtual(TCTreeNode<E,V> n, Collection<E> ves) {
		
		Iterator<E> i = ves.iterator();
		while (i.hasNext()) {
			if (n.getSkeleton().getVirtualEdges().contains(i.next()))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Get TCTree node containing vertex of original graph
	 * @param v Vertex of original graph
	 * @return TCTree node that contains vertex
	 */
	public TCTreeNode<E,V> getVertex(V v)
	{
		Iterator<TCTreeNode<E,V>> i = this.getVertices().iterator();
		
		while (i.hasNext()) {
			TCTreeNode<E,V> n = i.next();
			if (n.getSkeleton().contains(v))
				return n;
		}
		
		return null;
	}
	
	/**
	 * Check if TCTree node is root
	 * @param node Node of the TCTree
	 * @return <code>true</code> if node is the root of TCTree, <code>false</code> otherwise
	 */
	public boolean isRoot(TCTreeNode<E,V> node)
	{
		if (node == null) return false;
		return node.equals(this.root);
	}
	
	public TCTreeNode<E,V> getRoot()
	{
		return this.root;
	}
	
	/**
	 * Get parent of the TCTree node
	 * @param node TCTree node
	 * @return Parent of the node
	 */
	public TCTreeNode<E,V> getParent(TCTreeNode<E,V> node)
	{
		return this.getFirstPredecessor(node);
	}
	
	/**
	 * Get children of the TCTree node
	 * @param node TCTree node
	 * @return Children of the node
	 */
	public Collection<TCTreeNode<E,V>> getChildren(TCTreeNode<E,V> node)
	{
		return this.getSuccessors(node);
	}
	
	/**
	 * Get process graph fragment represented by TCTree node
	 * @param node TCTree node
	 * @return Process fragment
	 */
	public AbstractMultiGraphFragment<E,V> getFragment(TCTreeNode<E,V> node)
	{
		AbstractMultiGraphFragment<E,V> result = new AbstractMultiGraphFragment<E, V>(this.getGraph());
		this.getFragment(result, node);
		
		return result;
	}
	
	private void getFragment(AbstractMultiGraphFragment<E,V> frag, TCTreeNode<E,V> node)
	{
		Iterator<E> i = node.getSkeleton().getEdges().iterator();
		while (i.hasNext()) {
			E e = i.next();
			frag.addEdge(e.getV1(), e.getV2());
		}
		
		Iterator<TCTreeNode<E,V>> j = this.getChildren(node).iterator();
		while (j.hasNext()) {
			TCTreeNode<E,V> n = j.next();
			this.getFragment(frag, n);
		}
	}
	
	public AbstractMultiGraphFragment<E,V> getFragment(TCTreeNode<E,V> parent, TCTreeNode<E,V> child)
	{
		if (parent==null || child==null) return new AbstractMultiGraphFragment<E, V>(this.getGraph());
			
		Collection<TCTreeNode<E,V>> children = this.getChildren(parent);
		
		if (children.size() <= 1) return this.getFragment(parent);
		
		// combine 2 branches
		AbstractMultiGraphFragment<E,V> result = new AbstractMultiGraphFragment<E, V>(this.getGraph());
		
		int min = Integer.MAX_VALUE;
		TCTreeNode<E,V> minc = null; 
		
		Iterator<TCTreeNode<E,V>> i = children.iterator();
		while (i.hasNext()) {
			TCTreeNode<E,V> c = i.next();
			if (c.equals(child)) continue;
			
			int nv = this.getFragment(c).countVertices();
			if (nv < min)
			{
				min = nv;
				minc = c;
			}
		}
		
		if (minc!=null) {
			result = this.getFragment(minc);
			
			Iterator<E> j = child.getSkeleton().getEdges().iterator();
			while (j.hasNext()) {
				E e = j.next();
				result.addEdge(e.getV1(), e.getV2());
			}
		}
		
		return result;
	}
	
	/**
	 * Q or S
	 * @param node
	 * @param v
	 * @return
	 */
	public TriAbstractionStepInfo getFragment(TCTreeNode<E,V> node, V v)
	{
		TriAbstractionStepInfo result = new TriAbstractionStepInfo();
		
		AbstractMultiGraphFragment<E,V> frag = new AbstractMultiGraphFragment<E,V>(this.getGraph());
		
		Iterator<E> i = node.getSkeleton().getEdges(v).iterator();
		while (i.hasNext()) {
			E e = i.next();
			if (!node.getSkeleton().isVirtual(e) &&
					this.getGraph().getEdges(e.getV1()).size()==2 &&
					this.getGraph().getEdges(e.getV2()).size()==2) {
				E fe = frag.addEdge(e.getV1(), e.getV2());
				
				result.setFragment(this.getAllObjects(frag));
				result.setType(TCType.T);
				
				E oe = frag.getOriginal(fe);
				if (oe instanceof IDirectedEdge<?>) {
					IDirectedEdge<V> de = (IDirectedEdge<V>) oe;
					result.setEntry(de.getSource());
					result.setExit(de.getTarget());
				}
				
				return result;
			}
		}
		
		// TODO S-type abstraction fragment
		
		
		
		return result;
	}
	
	private Collection<IGObject> getAllObjects(AbstractMultiGraphFragment<E,V> frag)
	{
		Collection<IGObject> result = new ArrayList<IGObject>();
		
		result.addAll(frag.getVertices());
		result.addAll(frag.getOriginalEdges());
		
		return result;
	}
}
