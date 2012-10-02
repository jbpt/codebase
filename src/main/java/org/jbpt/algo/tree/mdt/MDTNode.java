package org.jbpt.algo.tree.mdt;

import java.util.Collection;

import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.hypergraph.abs.Vertex;

public class MDTNode<E extends IDirectedEdge<V>, V extends IVertex> extends Vertex implements IMDTNode<E, V> {
	private Collection<V> clan;
	private V proxy;
	private MDTType type;
	private int color;
	private MDT<E,V> mdt;

	public MDTNode(MDT<E,V> mdt, Collection<V> clan, V proxy) {
		this.clan = clan;
		this.proxy = proxy;
		this.type = MDTType.TRIVIAL;
		this.color = 0;
		this.mdt = mdt;
	}

	public V getProxy() {
		return proxy;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public Collection<V> getClan() {
		return clan;
	}
	
	public void setClan(Collection<V> clan) {
		this.clan = clan;
	}
	
	public MDTType getType() {
		return type;
	}

	public void setType(MDTType type) {
		this.type = type;
	}
	
	/**
	 * This method implements the visitor design pattern to enable a
	 * controlled traversal of the tree.
	 * 
	 * @param visitor
	 * @param obj
	 * @return
	 */
	public Object accept(MDTVisitor visitor, Object obj) {
		if (type == MDTType.TRIVIAL)
			return visitor.visitTrivial(this, obj);
		else if (type == MDTType.COMPLETE)
			return visitor.visitComplete(this, obj, color);
		else if (type == MDTType.LINEAR)
			return visitor.visitLinear(this, obj);
		else
			return visitor.visitPrimitive(this, obj);
	}
	
	public String toString() {
		if (type == MDTType.TRIVIAL)
			return clan.iterator().next().toString();
		else if (type == MDTType.COMPLETE)
			return type + "_" + color + mdt.getChildren(this);
		else
			return type.toString() + mdt.getChildren(this);
	}
}
