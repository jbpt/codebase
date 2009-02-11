/**
 * Copyright (c) 2008 Artem Polyvyanyy
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
package de.hpi.bpt.graph.algo.spqr;

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
 *
 * @param <E> template for edge (extends IEdge)
 * @param <V> template for vertex (extends IVertex)
 */
public class SPQRTree<E extends IEdge<V>, V extends IVertex> extends AbstractDirectedGraph<SPQRTreeEdge<E,V>, SPQRTreeNode<E,V>> {
	private IGraph<E,V> graph;
	
	private static int n = 0;
	
	private Collection<SPQRTreeNode<E,V>> nodes = new ArrayList<SPQRTreeNode<E,V>>();
	
	private E backEdge;
	
	private GraphAlgorithms<E,V> ga = new GraphAlgorithms<E,V>();
	
	private SPQRTreeNode<E,V> root = null;
	
	/*
	 * (non-Javadoc)
	 * @see de.hpi.bpt.graph.abs.AbstractDirectedGraph#addEdge(de.hpi.bpt.hypergraph.abs.IVertex, de.hpi.bpt.hypergraph.abs.IVertex)
	 */
	@Override
	public SPQRTreeEdge<E,V> addEdge(SPQRTreeNode<E,V> v1, SPQRTreeNode<E,V> v2) {
		if (v1 == null || v2 == null) return null;
		
		Collection<SPQRTreeNode<E,V>> ss = new ArrayList<SPQRTreeNode<E,V>>(); ss.add(v1);
		Collection<SPQRTreeNode<E,V>> ts = new ArrayList<SPQRTreeNode<E,V>>(); ts.add(v2);
		
		if (!this.checkEdge(ss, ts)) return null;
		
		return new SPQRTreeEdge<E,V>(this, v1, v2);
	}
	
	/**
	 * Constructor
	 * @param g
	 */
	public SPQRTree(IGraph<E,V> g) {
		GMLUtils<E, V> gmlV = new GMLUtils<E, V>();
		GMLUtils<SPQRTreeEdge<E,V>, SPQRTreeNode<E,V>> gmlT = new GMLUtils<SPQRTreeEdge<E,V>, SPQRTreeNode<E,V>>();
		
		
		
		if (g==null) return;
		this.graph = g;
		
		//graph must have one entry and one exit
		Collection<V> bs = ga.getBoundaryVertices(this.graph);
		if (bs.size()!=2) return;
		
		gmlV.serialize(this.getGraph(), "graph.gml");
		
		SPQRTreeSkeleton<E,V> sk = new SPQRTreeSkeleton<E,V>(this.graph);
		sk.copyOriginalGraph();
		Iterator<V> bi = bs.iterator();
		this.backEdge = sk.addVirtualEdge(bi.next(), bi.next());
		// graph must be biconnected
		if (!ga.isConnected(sk, 1)) return;

		// make first node
		this.root = new SPQRTreeNode<E,V>("0");
		root.setSkeleton(sk);

		// decompose
		this.decompose(root);
		
		// classify node types
		this.classifyNodes();
		
		Iterator<SPQRTreeNode<E,V>> ni = this.nodes.iterator();
		while (ni.hasNext()) {
			SPQRTreeNode<E,V> n = ni.next();
			/*System.out.println(n.getName());
			System.out.println(n.getSkeleton());
			System.out.println(n.getSkeleton().getVirtualEdges());
			System.out.println("-----------------------------------");*/
			gmlV.serialize(n.getSkeleton(), n.getName() + ".gml");
		}
		
		// construct tree
		this.constructTree();
		
		nodes.clear();
		nodes.addAll(this.getVertices());
		this.nodes.remove(this.root);
		
		ni = this.nodes.iterator();
		while (ni.hasNext()) {
			SPQRTreeNode<E,V> n = ni.next();
			List<V> bns = new ArrayList<V>(n.getBoundaryNodes());
			this.classifyBoundaryNode(n,bns.get(0));
			this.classifyBoundaryNode(n,bns.get(1));
		}
		
		gmlT.serialize(this,"tree.gml");
		
		this.nodes.clear();
	}
	
	/**
	 * Get original graph
	 * @return Graph
	 */
	public IGraph<E,V> getGraph() {
		return this.graph;
	}
	
	/**
	 * Decompose the skeleton
	 * @param node SPQRTreeNode that possesses skeleton to decompose
	 */
	private void decompose(SPQRTreeNode<E,V> node) {
		// get separation pair (a pair of vertices that makes skeleton disconnected)
		Collection<V> sepPair = ga.getSJSeparationSet(node.getSkeleton(), 2);
		
		if (sepPair != null && node.getSkeleton().countVertices()>2) { // further decomposition required?
			// get two disconnected parts
			SPQRTreeSkeleton<E,V> sk1 = this.getConnectedFragment(node.getSkeleton(), sepPair);
			SPQRTreeSkeleton<E,V> sk2 = sk1.getComplementary();
			
			// add virtual edge between separation vertices
			Iterator<V> i = sepPair.iterator();
			V sv1 = i.next(); V sv2 = i.next();
			sk1.addVirtualEdge(sv1,sv2);
			sk2.addVirtualEdge(sv1,sv2);
			
			// construct two subtasks
			SPQRTreeNode<E,V> newNode1 = new SPQRTreeNode<E,V>((new Integer(++SPQRTree.n)).toString());
			newNode1.setSkeleton(sk1);
			SPQRTreeNode<E,V> newNode2 = new SPQRTreeNode<E,V>((new Integer(++SPQRTree.n)).toString());
			newNode2.setSkeleton(sk2);
			
			// start recursion
			this.decompose(newNode1);
			this.decompose(newNode2);
		}
		else
			this.putNode(node); // decomposition done!
	}
	
	/**
	 * Add SPQR-tree node, if it was not added before
	 * @param node Node to add
	 */
	private void putNode(SPQRTreeNode<E,V> node) {
		Iterator<SPQRTreeNode<E,V>> i = this.nodes.iterator();
		
		Collection<V> vs1 = node.getSkeleton().getVertices();
		boolean flag = true;
		while (i.hasNext()) {
			SPQRTreeNode<E,V> n = i.next();
			
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
	public Collection<SPQRTreeNode<E,V>> getVertices(SPQRType type) {
		Collection<SPQRTreeNode<E,V>> result = new ArrayList<SPQRTreeNode<E,V>>();
		
		Iterator<SPQRTreeNode<E,V>> i = this.getVertices().iterator();
		while (i.hasNext()) {
			SPQRTreeNode<E,V> n = i.next();
			if (n.getType()==type)
				result.add(n);
		}
		
		return result;
	}
	
	/**
	 * Classify SPQRTree nodes on types: S,P,R
	 */
	private void classifyNodes() {
		int Sc, Pc, Rc;
		Sc = Pc = Rc = 0;
		
		Iterator<SPQRTreeNode<E,V>> i = this.nodes.iterator();
		while (i.hasNext()) {
			SPQRTreeNode<E,V> n = i.next();
			
			if (n.getSkeleton().countVertices()==2) { n.setType(SPQRType.P); n.setName("P" + Pc++); continue; }
			
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
				n.setType(SPQRType.S);
				n.setName("S" + Sc++);
			}
			else {
				n.setType(SPQRType.R);
				n.setName("R" + Rc++);
			}
		}
	}
	
	private void constructTree() {
		GMLUtils<E, V> gmlV = new GMLUtils<E, V>();
		
		// get root node - a node with back edge
		Iterator<SPQRTreeNode<E,V>> i = this.nodes.iterator();
		while (i.hasNext()) {
			SPQRTreeNode<E,V> n = i.next();
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
			if (es instanceof IDirectedEdge) {
				IDirectedEdge<V> de = (IDirectedEdge<V>) es;
				if (de.getSource().equals(this.backEdge.getV1())) this.root.setEntry(this.backEdge.getV1());
				if (de.getTarget().equals(this.backEdge.getV1())) this.root.setExit(this.backEdge.getV1());
			}
		}
		
		esi = this.graph.getEdges(this.backEdge.getV2()).iterator();
		if (esi.hasNext()) {
			E es = esi.next();
			if (es instanceof IDirectedEdge) {
				IDirectedEdge<V> de = (IDirectedEdge<V>) es;
				if (de.getSource().equals(this.backEdge.getV2())) this.root.setEntry(this.backEdge.getV2());
				if (de.getTarget().equals(this.backEdge.getV2())) this.root.setExit(this.backEdge.getV2());
			}
		}
		
		Collection<SPQRTreeNode<E,V>> ns = new ArrayList<SPQRTreeNode<E,V>>(this.nodes);
		ns.remove(this.root);
		constructChildren(this.root,ns);
	}
	
	private void classifyBoundaryNode(SPQRTreeNode<E,V> n, V v) {
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
			if (!(ge instanceof IDirectedEdge)) return;
			
			IDirectedEdge<V> de = (IDirectedEdge<V>) ge;
			
			if (de.getTarget().equals(v)) sIn++;
			else sOut++;
		}
		
		i = gEdges.iterator();
		while (i.hasNext()) {
			E e = i.next();
			if (!(e instanceof IDirectedEdge)) return;
			
			IDirectedEdge<V> de = (IDirectedEdge<V>) e;
			if (de.getTarget().equals(v)) gIn++;
			else gOut++;
		}
		
		if (sIn == 0 || gOut-sOut == 0) n.setEntry(v);
		if (sOut == 0 || gIn-sIn == 0) n.setExit(v);
	}

	
	private void constructChildren(SPQRTreeNode<E,V> n, Collection<SPQRTreeNode<E,V>> ns)
	{
		Collection<SPQRTreeNode<E,V>> nss = new ArrayList<SPQRTreeNode<E,V>>(ns);
		Collection<SPQRTreeNode<E,V>> ncs = new ArrayList<SPQRTreeNode<E,V>>();
		Collection<SPQRTreeNode<E,V>> nps = new ArrayList<SPQRTreeNode<E,V>>();
		
		Iterator<E> i = n.getSkeleton().getVirtualEdges().iterator();
		while (i.hasNext()) {
			E ve = i.next();
			
			Iterator<SPQRTreeNode<E,V>> j = ns.iterator();
			while (j.hasNext()) {
				SPQRTreeNode<E,V> vn = j.next();
				
				Collection<E> ves = vn.getSkeleton().getEdges(ve.getV1(), ve.getV2());
				if (this.containsVirtual(vn, ves)) {
					
					vn.setBoundaryNodes(ve.getVertices());
					
					nss.remove(vn);
					
					if (vn.getType() == SPQRType.P)
						nps.add(vn);
					else {
						ncs.add(vn);
					}
				}
			}
		}
		
		// first work with Ps
		Collection<SPQRTreeNode<E,V>> cncs = new ArrayList<SPQRTreeNode<E,V>>(ncs);
		Iterator<SPQRTreeNode<E,V>> j = nps.iterator();
		while (j.hasNext()) {
			SPQRTreeNode<E,V> p = j.next();
			
			// look for children
			Collection<SPQRTreeNode<E,V>> npc = new ArrayList<SPQRTreeNode<E,V>>();
			
			Iterator<SPQRTreeNode<E,V>> k = cncs.iterator();
			while (k.hasNext()) {
				SPQRTreeNode<E,V> cc = k.next();
				
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
			Collection<SPQRTreeNode<E,V>> u = new ArrayList<SPQRTreeNode<E,V>>(nss);
			u.addAll(npc);
			this.constructChildren(p,u);
		}
		
		// now rest
		j = ncs.iterator();
		while (j.hasNext()) {
			SPQRTreeNode<E,V> vn = j.next();
			this.addEdge(n,vn);
			this.constructChildren(vn,nss);
		}
	}
	
	private boolean containsVirtual(SPQRTreeNode<E,V> n, Collection<E> ves) {
		
		Iterator<E> i = ves.iterator();
		while (i.hasNext()) {
			if (n.getSkeleton().getVirtualEdges().contains(i.next()))
				return true;
		}
		
		return false;
	}
	
	
	/**
	 * Get connected fragment of the graph
	 * @param g Graph
	 * @param vs Vertices that separate connected fragment
	 * @return Connected fragment
	 */
	private SPQRTreeSkeleton<E,V> getConnectedFragment(SPQRTreeSkeleton<E,V> g, Collection<V> vs) {
		SPQRTreeSkeleton<E,V> result = new SPQRTreeSkeleton<E,V>(g);
		
		// get any edge of the graph
		E x = g.getEdges().iterator().next();
		
		Collection<E> L = new ArrayList<E>();	// already visited
		Collection<E> K = new ArrayList<E>();	// to visit
		K.add(x);								// start with edge 'x'
		
		while (K.size()>0) { // if there are edges to visit
			// pop edge
			E y = K.iterator().next();
			K.remove(y);
			E newE = result.addEdge(y.getV1(), y.getV2());
			newE.setDescription(y.getDescription());
			
			// mark as visited
			L.add(y);
			
			// get vertices of current edge
			V v1 = y.getV1();
			V v2 = y.getV2();
			
			// if v1 not in vs, add incident edges (not yet visited) to the K list
			if (!vs.contains(v1)) {
				Collection<E> es = g.getEdges(v1);
				Iterator<E> v1i = es.iterator();
				while (v1i.hasNext()) {
					E e = v1i.next();
					if (!L.contains(e) && !K.contains(e))
						K.add(e);
				}
			}

			// if v2 not in vs, add incident edges (not yet visited) to the K list
			if (!vs.contains(v2)) {
				Collection<E> es = g.getEdges(v2);
				Iterator<E> v2i = es.iterator();
				while (v2i.hasNext()) {
					E e = v2i.next();
					if (!L.contains(e) && !K.contains(e))
						K.add(e);
				}
			}
		}
		
		return result;
	}
	
	
	

	
	
	
	
	
	
	
	
	/**
	 * Get SPQRTree node containing vertex of original graph
	 * @param v Vertex of original graph
	 * @return SPQRTree node that contains vertex
	 */
	public SPQRTreeNode<E,V> getVertex(V v)
	{
		Iterator<SPQRTreeNode<E,V>> i = this.getVertices().iterator();
		
		while (i.hasNext()) {
			SPQRTreeNode<E,V> n = i.next();
			if (n.getSkeleton().contains(v))
				return n;
		}
		
		return null;
	}
	
	/**
	 * Check if SPQRTree node is root
	 * @param node Node of the SPQRTree
	 * @return <code>true</code> if node is the root of SPQRTree, <code>false</code> otherwise
	 */
	public boolean isRoot(SPQRTreeNode<E,V> node)
	{
		if (node == null) return false;
		return node.equals(this.root);
	}
	
	public SPQRTreeNode<E,V> getRoot()
	{
		return this.root;
	}
	
	/**
	 * Get parent of the SPQRTree node
	 * @param node SPQRTree node
	 * @return Parent of the node
	 */
	public SPQRTreeNode<E,V> getParent(SPQRTreeNode<E,V> node)
	{
		return this.getFirstPredecessor(node);
	}
	
	/**
	 * Get children of the SPQRTree node
	 * @param node SPQRTree node
	 * @return Children of the node
	 */
	public Collection<SPQRTreeNode<E,V>> getChildren(SPQRTreeNode<E,V> node)
	{
		return this.getSuccessors(node);
	}
	
	/**
	 * Get process graph fragment represented by SPQRTree node
	 * @param node SPQRTree node
	 * @return Process fragment
	 */
	public AbstractMultiGraphFragment<E,V> getFragment(SPQRTreeNode<E,V> node)
	{
		AbstractMultiGraphFragment<E,V> result = new AbstractMultiGraphFragment<E, V>(this.getGraph());
		this.getFragment(result, node);
		
		return result;
	}
	
	private void getFragment(AbstractMultiGraphFragment<E,V> frag, SPQRTreeNode<E,V> node)
	{
		Iterator<E> i = node.getSkeleton().getEdges().iterator();
		while (i.hasNext()) {
			E e = i.next();
			frag.addEdge(e.getV1(), e.getV2());
		}
		
		Iterator<SPQRTreeNode<E,V>> j = this.getChildren(node).iterator();
		while (j.hasNext()) {
			SPQRTreeNode<E,V> n = j.next();
			this.getFragment(frag, n);
		}
	}
	
	public AbstractMultiGraphFragment<E,V> getFragment(SPQRTreeNode<E,V> parent, SPQRTreeNode<E,V> child)
	{
		if (parent==null || child==null) return new AbstractMultiGraphFragment<E, V>(this.getGraph());
			
		Collection<SPQRTreeNode<E,V>> children = this.getChildren(parent);
		
		if (children.size() <= 1) return this.getFragment(parent);
		
		// combine 2 branches
		AbstractMultiGraphFragment<E,V> result = new AbstractMultiGraphFragment<E, V>(this.getGraph());
		
		int min = Integer.MAX_VALUE;
		SPQRTreeNode<E,V> minc = null; 
		
		Iterator<SPQRTreeNode<E,V>> i = children.iterator();
		while (i.hasNext()) {
			SPQRTreeNode<E,V> c = i.next();
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
	public TriAbstractionStepInfo getFragment(SPQRTreeNode<E,V> node, V v)
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
				result.setType(SPQRType.Q);
				
				E oe = frag.getOriginal(fe);
				if (oe instanceof IDirectedEdge) {
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
