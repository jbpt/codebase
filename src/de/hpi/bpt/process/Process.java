package de.hpi.bpt.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import de.hpi.bpt.oryx.erdf.ERDFModel;

/**
 * Basic process model implementation
 * A very basic subset of BPMN
 * 
 * @author Artem Polyvyanyy
 */
public class Process extends ERDFModel<ControlFlow, Node> {
	public static String ERDF_TASK_TYPE			= "http://b3mn.org/stencilset/bpmn1.1#Task";
	public static String ERDF_XOR_TYPE			= "http://b3mn.org/stencilset/bpmn1.1#Exclusive_Databased_Gateway";
	public static String ERDF_AND_TYPE			= "http://b3mn.org/stencilset/bpmn1.1#AND_Gateway";
	public static String ERDF_OR_TYPE			= "http://b3mn.org/stencilset/bpmn1.1#OR_Gateway";
	public static String ERDF_SUBPROCESS_TYPE	= "http://b3mn.org/stencilset/bpmn1.1#CollapsedSubprocess";
	public static String ERDF_SEQUENCEFLOW_TYPE	= "http://b3mn.org/stencilset/bpmn1.1#SequenceFlow";
	
	protected static Collection<String> eTypes = new ArrayList<String>();
	protected static Collection<String> nTypes = new ArrayList<String>();
	
	private String name;
	
	static {
		eTypes.add(Process.ERDF_SEQUENCEFLOW_TYPE);
		
		nTypes.add(Process.ERDF_TASK_TYPE);
		nTypes.add(Process.ERDF_AND_TYPE);
		nTypes.add(Process.ERDF_OR_TYPE);
		nTypes.add(Process.ERDF_XOR_TYPE);
		nTypes.add(Process.ERDF_SUBPROCESS_TYPE);
	}
	
	public Process() {
		super(Process.eTypes, Process.nTypes);
		this.name = "";
	}
	
	public Process(String uName) {
		super(Process.eTypes, Process.nTypes);
		this.name = uName;
	}

	public ControlFlow addControlFlow(Node from, Node to) {
		if (from == null || to == null) return null;
		
		Collection<Node> ss = new ArrayList<Node>(); ss.add(from);
		Collection<Node> ts = new ArrayList<Node>(); ts.add(to);
		
		if (!this.checkEdge(ss, ts)) return null;
		
		return new ControlFlow(this, from, to);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.hpi.bpt.oryx.erdf.ERDFModel#createEdge(java.lang.String, de.hpi.bpt.oryx.erdf.ERDFNode, de.hpi.bpt.oryx.erdf.ERDFNode)
	 */
	@Override
	public ControlFlow createEdge(String type, Node s, Node t) {
		return this.addControlFlow(s, t);
	}

	/*
	 * (non-Javadoc)
	 * @see de.hpi.bpt.oryx.erdf.ERDFModel#createNode(java.lang.String)
	 */
	@Override
	public Node createNode(String type) {
		if (type.equals(Process.ERDF_TASK_TYPE))
			return new Task();
		else if (type.equals(Process.ERDF_SUBPROCESS_TYPE))
			return new SubProcess();
		else if (type.equals(Process.ERDF_AND_TYPE))
			return new Gateway(GatewayType.AND);
		else if (type.equals(Process.ERDF_OR_TYPE))
			return new Gateway(GatewayType.OR);
		else if (type.equals(Process.ERDF_XOR_TYPE))
			return new Gateway(GatewayType.XOR);
		return null;
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
	
	public void setName(String uName) {
		this.name = uName;
	}
}
