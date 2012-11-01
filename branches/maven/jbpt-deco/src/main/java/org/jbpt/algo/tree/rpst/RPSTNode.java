package org.jbpt.algo.tree.rpst;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jbpt.algo.tree.tctree.TCTreeNode;
import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.Fragment;
import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.hypergraph.abs.Vertex;


/**
 * Implementation of the node of the RPST.
 * 
 * @author Artem Polyvyanyy
 *
 * @param <E> Edge template.
 * @param <V> Vertex template.
 */
public class RPSTNode<E extends IDirectedEdge<V>, V extends IVertex> extends Vertex implements IRPSTNode<E,V> {
	// fragment entry
	protected V entry = null;
	// fragment exit 
	protected V exit = null;
	// fragment
	protected Fragment<E,V> fragment = null;
	// type of the triconnected component which induces this fragment
	protected TCType type = TCType.UNDEFINED;
	// link to the triconnected component which induces this fragment
	private TCTreeNode<DirectedEdge,Vertex> tcnode = null;
	// link to the RPST
	private RPST<E,V> rpst = null;
	
	/**
	 * Protected constructor.
	 * 
	 * @param rpst Link to the RPST this node belongs.
	 * @param tcnode The triconnected component which induces this fragment.
	 */
	protected RPSTNode(RPST<E,V> rpst, TCTreeNode<DirectedEdge,Vertex> tcnode) {
		this.rpst = rpst;
		this.tcnode = tcnode;
		this.type = tcnode.getType();
		this.setName(tcnode.getName());
	}

	@Override
	public TCType getType() {
		return this.type;
	}
	
	@Override
	public V getEntry() {
		if (this.entry==null)
			this.classifyBoundaries();
		
		return this.entry;
	}
	
	@Override
	public V getExit() {
		if (this.exit==null)
			this.classifyBoundaries();
		
		return this.exit;
	}
	
	@Override
	public Fragment<E,V> getFragment() {
		if (this.fragment == null)
			this.constructFragment();
		
		return this.fragment;
	}

	private void classifyBoundaries() {
		Set<V> vertices = new HashSet<V>();
		
		for (E e : this.getFragment()) {
			vertices.add(e.getSource());
			vertices.add(e.getTarget());
		}
		
		int csrc=0;
		int csnk=0;
		boolean flag = false;
		boolean fflag = true;
		V vv = null;
		for (V v : vertices) {
			if (this.rpst.diGraph.getIncomingEdges(v).isEmpty()) { this.entry = v; csrc++; fflag=false; continue; }
			if (this.rpst.diGraph.getOutgoingEdges(v).isEmpty()) { this.exit = v; csnk++; fflag= false; continue; }
			if (this.getFragment().containsAll(this.rpst.diGraph.getEdges(v))) continue;
			
			if (flag) flag = false; 
			else if (!flag) { flag=true; vv=v; }
			
			if (this.getFragment().containsAll(this.rpst.diGraph.getOutgoingEdges(v)) ||
					this.areDisjoint(this.getFragment(),this.rpst.diGraph.getIncomingEdges(v)))
				this.entry = v;
			
			if (this.getFragment().containsAll(this.rpst.diGraph.getIncomingEdges(v)) ||
					this.areDisjoint(this.getFragment(),this.rpst.diGraph.getOutgoingEdges(v)))
				this.exit = v;
		}
		
		if (csrc>1) this.entry=null;
		if (csnk>1) this.exit=null;
		if (flag && fflag) this.entry = this.exit = vv;
	}
	
	private boolean areDisjoint(Collection<E> c1, Collection<E> c2) {
		for (E e : c2) 
			if (c1.contains(e))
				return false;
		
		return true;
	}

	private void constructFragment() {
		this.fragment = new Fragment<E,V>(this.rpst.diGraph);
		
		for (IRPSTNode<E,V> node : this.rpst.getChildren(this)) {
			this.fragment.addAll(node.getFragment());
		}
		
		for (DirectedEdge edge : this.tcnode.getSkeleton().getOriginalEdges()) {
			if (this.rpst.extraEdges.contains(edge)) continue;
			
			this.fragment.add(this.rpst.ne2oe.get(edge));
		}
	}
	
	@Override
	public String toString() {
		return String.format("%s: (%s,%s) - %s", this.getName(), this.getEntry(), this.getExit(), this.getFragment());
	}
}
