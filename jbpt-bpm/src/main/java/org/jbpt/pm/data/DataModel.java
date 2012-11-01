package org.jbpt.pm.data;

import java.util.ArrayList;
import java.util.Collection;

import org.jbpt.graph.abs.AbstractDirectedGraph;
import org.jbpt.pm.DataNode;


/**
 * basic data model implementation
 * 
 * @author Andreas Meyer
 *
 */
public class DataModel extends AbstractDirectedGraph<DataConnection<DataNode>, DataNode> implements IDataModel<DataConnection<DataNode>, DataNode> {
	
	private String name;
	
	/**
	 * Construct an empty data model
	 */
	public DataModel() {
		this.name = "";
	}
	
	/**
	 * Construct an empty data model with name
	 */
	public DataModel(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public DataConnection<DataNode> addDataConnection(DataNode from, DataNode to, DataConnectionType type) {
		if (from == null || to == null) {
			return null;
		}
		
		from.setModel(this);
		to.setModel(this);
		
		Collection<DataNode> ss = new ArrayList<DataNode>();
		ss.add(from);
		Collection<DataNode> ts = new ArrayList<DataNode>();
		ts.add(to);
		
		if (!this.checkEdge(ss, ts)) return null;
		
		return new DataConnection<DataNode>(this, from, to, type);
	}
	
	@Override
	public DataConnection<DataNode> addGeneralization(DataNode from, DataNode to) {
		if (from == null || to == null) {
			return null;
		}
		
		from.setModel(this);
		to.setModel(this);
		
		Collection<DataNode> ss = new ArrayList<DataNode>();
		ss.add(from);
		Collection<DataNode> ts = new ArrayList<DataNode>();
		ts.add(to);
		
		if (!this.checkEdge(ss, ts)) return null;
		
		return new DataConnection<DataNode>(this, from, to, DataConnectionType.G);
	}
	
	@Override
	public DataConnection<DataNode> addAggregation(DataNode from, DataNode to) {
		if (from == null || to == null) {
			return null;
		}
		
		from.setModel(this);
		to.setModel(this);
		
		Collection<DataNode> ss = new ArrayList<DataNode>();
		ss.add(from);
		Collection<DataNode> ts = new ArrayList<DataNode>();
		ts.add(to);
		
		if (!this.checkEdge(ss, ts)) return null;
		
		return new DataConnection<DataNode>(this, from, to, DataConnectionType.Agg);
	}

	@Override
	public DataConnection<DataNode> addAssociation(DataNode from, DataNode to) {
		if (from == null || to == null) {
			return null;
		}
		
		from.setModel(this);
		to.setModel(this);
		
		Collection<DataNode> ss = new ArrayList<DataNode>();
		ss.add(from);
		Collection<DataNode> ts = new ArrayList<DataNode>();
		ts.add(to);
		
		if (!this.checkEdge(ss, ts)) return null;
		
		return new DataConnection<DataNode>(this, from, to, DataConnectionType.Ass);
	}

	@Override
	public DataNode addDataNode(DataNode dataNode) {
		dataNode.setModel(this);
		return (DataNode) super.addVertex((DataNode) dataNode);
	}

	@Override
	public Collection<DataConnection<DataNode>> getDataConnections() {
		return this.getEdges();
	}
	
	@Override
	public Collection<DataConnection<DataNode>> getDataConnections(DataConnectionType type) {
		Collection<DataConnection<DataNode>> result = new ArrayList<DataConnection<DataNode>>();
		
		for (DataConnection<DataNode> e : this.getEdges()) {
			if(e.getDataConnectionType().equals(type)) {
				result.add(e);
			}
		}
		
		return result;
	}
	
	@Override
	public Collection<DataNode> getDataNodes() {
		Collection<DataNode> dataNodes = new ArrayList<DataNode>();
		
		for (DataNode node : this.vertices.keySet()){
			if (node instanceof DataNode){
				dataNodes.add((DataNode) node);
			}
		}
		
		return dataNodes;
	}

	@Override
	public String toDOT() {
		String result = "";
		
		if (this == null) {
			return result;
		}
		
		result += "digraph G {\n";
		result += "rankdir=TD \n"; //rankdir=LR for left to right graph; rankdir=TD for top to down graph
		
		for (DataNode d : this.getDataNodes()) {
			result += String.format("  n%s[shape=box,label=\"%s\"];\n", d.getId().replace("-", ""), d.getName());
		}
		result+="\n";
		
		for (DataConnection<DataNode> dc: this.getDataConnections(DataConnectionType.Agg)) {
			result += String.format("  n%s->n%s[dir=\"both\",arrowhead=\"none\",arrowtail=\"ediamond\"];\n", dc.getSource().getId().replace("-", ""), dc.getTarget().getId().replace("-", ""));
		}
		result+="\n";
		
		for (DataConnection<DataNode> dc: this.getDataConnections(DataConnectionType.Ass)) {
			result += String.format("  n%s->n%s[dir=\"both\",arrowhead=\"none\",arrowtail=\"none\"];\n", dc.getSource().getId().replace("-", ""), dc.getTarget().getId().replace("-", ""));
		}
		result+="\n";
		
		for (DataConnection<DataNode> dc: this.getDataConnections(DataConnectionType.G)) {
			result += String.format("  n%s->n%s[dir=\"both\",arrowhead=\"none\",arrowtail=\"empty\"];\n", dc.getSource().getId().replace("-", ""), dc.getTarget().getId().replace("-", ""));
		}
		result += "}";
		
		return result;
	}
}
