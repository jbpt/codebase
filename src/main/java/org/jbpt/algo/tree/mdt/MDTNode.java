package org.jbpt.algo.tree.mdt;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jbpt.hypergraph.abs.Vertex;

public class MDTNode extends Vertex {
	public enum NodeType {TRIVIAL, COMPLETE, LINEAR, PRIMITIVE};
	private Collection<Vertex> value;
	private Vertex proxy;
	private NodeType type;
	private Set<MDTNode> children;
	private int color;

	public MDTNode(Collection<Vertex> value, Vertex proxy) {
		this.value = value;
		this.proxy = proxy;
		this.type = NodeType.TRIVIAL;
		this.color = 0;
		this.children = new HashSet<MDTNode>();
	}

	public Vertex getProxy() {
		return proxy;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public Set<MDTNode> getChildren() {
		return children;
	}

	public void addChild(MDTNode child) {
		children.add(child);
	}

	public void addChildren(Set<MDTNode> children) {
		this.children.addAll(children);
	}

	public Collection<Vertex> getValue() {
		return value;
	}
	
	public void setValue(Collection<Vertex> value) {
		this.value = value;
	}
	
	public NodeType getType() {
		return type;
	}

	public void setType(NodeType type) {
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
		if (type == NodeType.TRIVIAL)
			return visitor.visitTrivial(this, obj);
		else if (type == NodeType.COMPLETE)
			return visitor.visitComplete(this, obj, color);
		else if (type == NodeType.LINEAR)
			return visitor.visitLinear(this, obj);
		else
			return visitor.visitPrimitive(this, obj);
	}
	
	public String toString() {
		if (type == NodeType.TRIVIAL)
			return value.iterator().next().toString();
		else if (type == NodeType.COMPLETE)
			return type + "_" + color + children;
		else
			return type.toString() + children;
	}
}
