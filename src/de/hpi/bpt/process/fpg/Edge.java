package de.hpi.bpt.process.fpg;

import de.hpi.bpt.hypergraph.abs.AbstractDirectedHyperEdge;
import de.hpi.bpt.hypergraph.abs.AbstractMultiDirectedHyperGraph;

public class Edge extends AbstractDirectedHyperEdge<Activity> {
	private EdgeType type = EdgeType.AND;
	
	@SuppressWarnings("rawtypes")
	protected Edge(AbstractMultiDirectedHyperGraph g) {
		super(g);
	}
	
	@SuppressWarnings("rawtypes")
	public Edge(AbstractMultiDirectedHyperGraph g, EdgeType type) {
		super(g);
		setType(type);
	}

	public EdgeType getType() {
		return type;
	}

	public void setType(EdgeType type) {
		this.type = type;
	}
}
