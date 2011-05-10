package de.hpi.bpt.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import de.hpi.bpt.graph.abs.AbstractDirectedGraph;

/**
 * Basic process model implementation
 * 
 * @author Artem Polyvyanyy
 */
public class Process extends AbstractDirectedGraph<ControlFlow, Node> {
	private String name;
	
	public Process() {
		this.name = "";
	}
	
	public Process(String name) {
		this.name = name;
	}

	public ControlFlow addControlFlow(Node from, Node to) {
		if (from == null || to == null) return null;
		
		Collection<Node> ss = new ArrayList<Node>(); ss.add(from);
		Collection<Node> ts = new ArrayList<Node>(); ts.add(to);
		
		if (!this.checkEdge(ss, ts)) return null;
		
		return new ControlFlow(this, from, to);
	}
	
	public Collection<Task> getTasks() {
		Collection<Task> result = new ArrayList<Task>();
		
		Collection<Node> nodes = this.getVertices();
		Iterator<Node> i = nodes.iterator();
		while (i.hasNext()) {
			Node obj = i.next();
			if (obj instanceof Task)
				result.add((Task)obj);
		}
		
		return result;
	}
	
	private Collection<Gateway> getGateways(Collection<GatewayType> types) {
		Collection<Gateway> result = new ArrayList<Gateway>();
		
		Collection<Node> nodes = this.getVertices();
		Iterator<Node> i = nodes.iterator();
		while (i.hasNext()) {
			Node obj = i.next();
			if (obj instanceof Gateway) {
				Gateway g = (Gateway) obj;
				if (types.contains(g.getGatewayType()))
					result.add(g);
			}		
		}
		
		return result;
	}
	
	public Collection<Gateway> getGateways() {
		Collection<GatewayType> types = new ArrayList<GatewayType>();
		types.add(GatewayType.AND);
		types.add(GatewayType.XOR);
		types.add(GatewayType.OR);
		types.add(GatewayType.UNDEFINED);
		return this.getGateways(types);
	}
	
	public Collection<Gateway> getGateways(GatewayType type) {
		Collection<GatewayType> types = new ArrayList<GatewayType>();
		types.add(type);
		return this.getGateways(types);
	}
	
	public Collection<ControlFlow> getControlFlow() {
		return this.getEdges();
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
