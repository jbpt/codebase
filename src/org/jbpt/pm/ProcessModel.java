package org.jbpt.pm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jbpt.graph.abs.AbstractDirectedGraph;
import org.jbpt.graph.algo.DirectedGraphAlgorithms;
import org.jbpt.graph.algo.GraphAlgorithms;
import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.hypergraph.abs.Vertex;


/**
 * Basic process model implementation
 * 
 * @author Artem Polyvyanyy, Tobias Hoppe, Cindy F�hnrich, Andreas Meyer
 */
public class ProcessModel extends AbstractDirectedGraph<ControlFlow<FlowNode>, FlowNode> implements IProcessModel<ControlFlow<FlowNode>, FlowNode, NonFlowNode> {

	private DirectedGraphAlgorithms<ControlFlow<FlowNode>, FlowNode> directedGraphAlgorithms = new DirectedGraphAlgorithms<ControlFlow<FlowNode>, FlowNode>();
	
	private GraphAlgorithms<ControlFlow<FlowNode>, FlowNode> graphAlgorithms = new GraphAlgorithms<ControlFlow<FlowNode>, FlowNode>();
	
	protected Set<NonFlowNode> nonFlowNodes = new HashSet<NonFlowNode>();

	/**
	 * Construct an empty process
	 */
	public ProcessModel() {
	}
	
	/**
	 * Construct an empty process with the given name
	 * @param name of the process
	 */
	public ProcessModel(String name) {
		setName(name);
	}

	@Override
	public ControlFlow<FlowNode> addControlFlow(FlowNode from, FlowNode to) {
		return addControlFlow(from, to, 1f);
	}
	
	@Override
	public ControlFlow<FlowNode> addControlFlow(FlowNode from, FlowNode to, float probability) {
		if (from == null || to == null) {
			return null;
		}
		from.setModel(this);
		to.setModel(this);
		
		Collection<FlowNode> ss = new ArrayList<FlowNode>();
		ss.add(from);
		Collection<FlowNode> ts = new ArrayList<FlowNode>();
		ts.add(to);
		
		if (!this.checkEdge(ss, ts)) return null;
		
		return new ControlFlow<FlowNode>(this, from, to, probability);
	}
	
	@Override
	public ControlFlow<FlowNode> addEdge(FlowNode from, FlowNode to) {
		return addControlFlow(from, to);
	}
	
	@Override
	public FlowNode addFlowNode(FlowNode obj) {
		obj.setModel(this);
		return super.addVertex(obj);
	}

	@Override
	@Deprecated
	public Gateway addGateway(Gateway gateway) {
		return (Gateway) this.addFlowNode(gateway);
	}

	@Override
	public NonFlowNode addNonFlowNode(NonFlowNode obj) {
		return (this.nonFlowNodes.add(obj)) ? obj : null;
	}

	/**
	 * Add activity to the process
	 * @param task {@link Activity} to add
	 * @return {@link Activity} that was added to the process, <code>null</code> upon failure
	 */
	@Deprecated
	public Activity addTask(Activity task) {
		return (Activity) this.addFlowNode(task);
	}
	
	@Override
	public ProcessModel clone(){
		ProcessModel clone = (ProcessModel) super.clone();
		
		// clear algorithm class
		clone.clearMembers();
		
		// workaround since abstract graph notifier is not cloned
		clone.removeVertices(clone.getVertices());
		clone.removeEdges(clone.getEdges());
		
		Map<FlowNode,FlowNode> nodeCopies = new HashMap<FlowNode, FlowNode>();
		
		for (FlowNode n : this.getVertices()) {
			FlowNode c = n.clone();
			clone.addFlowNode(c);
			nodeCopies.put(n, c);
		}
		
		for (ControlFlow<FlowNode> f : this.getControlFlow()) {
			FlowNode from = nodeCopies.get(f.getSource());
			FlowNode to = nodeCopies.get(f.getTarget());
			clone.addControlFlow(from, to);
		}
		
		clone.nonFlowNodes = new HashSet<NonFlowNode>();
		for (NonFlowNode node : this.nonFlowNodes){
			clone.nonFlowNodes.add((NonFlowNode) node.clone());
		}
		return clone;
	}
	
	@Override
	public boolean contains(Vertex modelElement){
		return this.vertices.containsKey(modelElement) || this.nonFlowNodes.contains(modelElement);
	}

	@Override
	public boolean containsAllTypes(Collection<Class<?>> classes) {
		if (classes == null) {
			return false;
		}
		for (Class<?> clazz : classes) {
			boolean result = containsType(clazz);
			if (!result) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean containsType(Class<?> clazz) {
		if (clazz == null) {
			return false;
		}
		for (IVertex flowNode : this.vertices.keySet()) {
			if (clazz.isInstance(flowNode)) {
				return true;
			}
		}
		for (IVertex nonFlowNode : this.nonFlowNodes) {
			if (clazz.isInstance(nonFlowNode)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Collection<? extends IVertex> filter(Class<?> clazz) {
		Collection<IVertex> result = new ArrayList<IVertex>();
		for (IVertex flowNode : this.vertices.keySet()){	
			if (clazz.isInstance(flowNode)){
				result.add((Vertex) flowNode);
			}
		}
		for (IVertex nonFlowNode : this.nonFlowNodes){
			if (clazz.isInstance(nonFlowNode)){
				result.add((Vertex) nonFlowNode);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Activity> getActivities() {
		return (Collection<Activity>) this.filter(Activity.class);
	}

	@Override
	public Collection<ControlFlow<FlowNode>> getControlFlow() {
		return this.getEdges();
	}

	@Override
	public Collection<DataNode> getDataNodes() {
		Collection<DataNode> dataNodes = new ArrayList<DataNode>();
		for (NonFlowNode node : this.nonFlowNodes){
			if (node instanceof DataNode){
				dataNodes.add((DataNode) node);
			}
		}
		return dataNodes;
	}

	@Override
	public Collection<FlowNode> getEntries() {
		return (Collection<FlowNode>) directedGraphAlgorithms.getInputVertices(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Event> getEvents() {
		return (Collection<Event>) this.filter(Event.class);
	}

	@Override
	public Collection<FlowNode> getExits() {
		return (Collection<FlowNode>) directedGraphAlgorithms.getOutputVertices(this);
	}

	@Override
	public Collection<FlowNode> getFlowNodes() {
		return (Collection<FlowNode>) super.getVertices();
	}

	@Override
	public Collection<FlowNode> getFlowNodes(NonFlowNode obj) {
		Set<FlowNode> result = new HashSet<FlowNode>();
		result.addAll(this.getInputFlowNodes(obj));
		result.addAll(this.getOutputFlowNodes(obj));
		return new ArrayList<FlowNode>(result);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Gateway> getGateways() {
		return (Collection<Gateway>) this.filter(Gateway.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Gateway> getGateways(Class<?> type) {
		return (Collection<Gateway>) this.filter(type);		
	}

	@Override
	public Collection<ControlFlow<FlowNode>> getIncomingControlFlow(FlowNode obj) {
		return super.getIncomingEdges(obj);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<FlowNode> getInputFlowNodes(NonFlowNode obj) {
		Set<FlowNode> result = new HashSet<FlowNode>();
		//given node is part of this graph
		if (this.nonFlowNodes.contains(obj)){
			if (obj instanceof IDataNode){
				result.addAll((Collection<? extends FlowNode>) ((IDataNode) obj).getWritingFlowNodes());
				result.addAll((Collection<? extends FlowNode>) ((IDataNode) obj).getUnspecifiedFlowNodes());
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<NonFlowNode> getInputNonFlowNodes(FlowNode obj) {
		Set<NonFlowNode> result = new HashSet<NonFlowNode>();
		result.addAll((Collection<? extends NonFlowNode>) obj.getReadDocuments());
		result.addAll((Collection<? extends NonFlowNode>) obj.getUnspecifiedDocuments());
		result.addAll((Collection<? extends NonFlowNode>) obj.getResources());
		return result;
	}

	@Override
	public Collection<NonFlowNode> getNonFlowNodes() {
		return new ArrayList<NonFlowNode>(this.nonFlowNodes);
	}

	@Override
	public Collection<NonFlowNode> getNonFlowNodes(FlowNode obj) {
		Set<NonFlowNode> result = new HashSet<NonFlowNode>();
		result.addAll(this.getInputNonFlowNodes(obj));
		result.addAll(this.getOutputNonFlowNodes(obj));
		return new ArrayList<NonFlowNode>(result);
	}

	@Override
	public Collection<ControlFlow<FlowNode>> getOutgoingControlFlow(FlowNode obj) {
		return this.getOutgoingEdges(obj);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<FlowNode> getOutputFlowNodes(NonFlowNode obj) {
		Set<FlowNode> result = new HashSet<FlowNode>();
		//given node part of this graph?
		if (this.nonFlowNodes.contains(obj)){
			if (obj instanceof IDataNode){
				result.addAll((Collection<? extends FlowNode>) ((IDataNode) obj).getReadingFlowNodes());
				result.addAll((Collection<? extends FlowNode>) ((IDataNode) obj).getUnspecifiedFlowNodes());
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<NonFlowNode> getOutputNonFlowNodes(FlowNode obj) {
		Set<NonFlowNode> result = new HashSet<NonFlowNode>();	
		result.addAll((Collection<? extends NonFlowNode>) obj.getWriteDocuments());
		result.addAll((Collection<? extends NonFlowNode>) obj.getUnspecifiedDocuments());
		result.addAll((Collection<? extends NonFlowNode>) obj.getResources());
		return result;
	}

	@Override
	public boolean isConnected(){
		return graphAlgorithms.isConnected(this);
	}

	@Override
	public ControlFlow<FlowNode> removeControlFlow(ControlFlow<FlowNode> flow) {
		return this.removeEdge(flow)!=null ? flow : null;
	}
	
	@Override
	public Collection<ControlFlow<FlowNode>> removeControlFlows(Collection<ControlFlow<FlowNode>> flows) {
		return this.removeEdges(flows);
	}
	
	@Override
	public FlowNode removeFlowNode(FlowNode obj) {
		return super.removeVertex(obj);
	}

	@Override
	@Deprecated
	public Gateway removeGateway(Gateway gateway) {
		return (Gateway) this.removeFlowNode(gateway);
	}
	
	@Override
	public NonFlowNode removeNonFlowNode(NonFlowNode obj) {
		return this.nonFlowNodes.remove(obj) ? obj : null;
	}

	@Override
	@Deprecated
	public Activity removeTask(Activity task) {
		return (Activity) this.removeFlowNode(task);
	}
	
	@Override
	public String toDOT() {
		String result = "";
		if (this == null) {
			return result;
		}
		
		result += "digraph G {\n";
		result += "rankdir=LR \n"; //rankdir=LR for left to right graph; rankdir=TD for top to down graph
		
		for (Event e : this.getEvents()) {
			result += String.format("  n%s[shape=ellipse,label=\"%s\"];\n", e.getId().replace("-", ""), e.getName());
		}
		result+="\n";
		
		for (Activity a : this.getActivities()) {
			result += String.format("  n%s[shape=box,label=\"%s\"];\n", a.getId().replace("-", ""), a.getName());
		}
		result+="\n";
		
		for (Gateway g : this.getGateways(AndGateway.class)) {
			result += String.format("  n%s[shape=diamond,label=\"%s\"];\n", g.getId().replace("-", ""), "AND");
		}
		for (Gateway g : this.getGateways(XorGateway.class)) {
			result += String.format("  n%s[shape=diamond,label=\"%s\"];\n", g.getId().replace("-", ""), "XOR");
		}
		for (Gateway g : this.getGateways(OrGateway.class)) {
			result += String.format("  n%s[shape=diamond,label=\"%s\"];\n", g.getId().replace("-", ""), "OR");
		}
		result+="\n";
		
		for (DataNode d : this.getDataNodes()) {
			result += String.format("  n%s[shape=note,label=\"%s\"];\n", d.getId().replace("-", ""), d.getName().concat(" [" + d.getState() + "]"));
		}
		result+="\n";
		
		for (ControlFlow<FlowNode> cf: this.getControlFlow()) {
			if (cf.getLabel()!=null && cf.getLabel()!="")
				result += String.format("  n%s->n%s[label=\"%s\"];\n", cf.getSource().getId().replace("-", ""), cf.getTarget().getId().replace("-", ""), cf.getLabel());
			else
				result += String.format("  n%s->n%s;\n", cf.getSource().getId().replace("-", ""), cf.getTarget().getId().replace("-", ""));
		}
		result+="\n";
		
		for (Activity a : this.getActivities()) {
			for (IDataNode d : a.getReadDocuments()) {
				result += String.format("  n%s->n%s;\n", d.getId().replace("-", ""), a.getId().replace("-", ""));
			}
			for (IDataNode d : a.getWriteDocuments()) {
				result += String.format("  n%s->n%s;\n", a.getId().replace("-", ""), d.getId().replace("-", ""));
			}
		}
		result += "}";
		
		return result;
	}
	
	@Override
	public Collection<FlowNode> getAllPredecessors(FlowNode fn) {
		Set<FlowNode> result = new HashSet<FlowNode>();
		
		Set<FlowNode> temp = new HashSet<FlowNode>();
		temp.addAll(getDirectPredecessors(fn));
		result.addAll(temp);
		while(!(temp.isEmpty())) {
			Set<FlowNode> temp2 = new HashSet<FlowNode>();
			for (FlowNode flowNode : temp) {
				temp2.addAll(getDirectPredecessors(flowNode));
			}
			temp = temp2;
			Set<FlowNode> temp3 = new HashSet<FlowNode>();
			for (FlowNode flowNode : temp) {
				if(!(result.contains(flowNode))) {
					result.add(flowNode);
				} else {
					temp3.add(flowNode);
				}
			}
			for (FlowNode flowNode : temp3) {
				temp.remove(flowNode);
			}
		}
		
		return result;
	}
	
	@Override
	public Collection<FlowNode> getAllSuccessors(FlowNode fn) {
		Set<FlowNode> result = new HashSet<FlowNode>();
		
		Set<FlowNode> temp = new HashSet<FlowNode>();
		temp.addAll(getDirectSuccessors(fn));
		result.addAll(temp);
		while(!(temp.isEmpty())) {
			Set<FlowNode> temp2 = new HashSet<FlowNode>();
			for (FlowNode flowNode : temp) {
				temp2.addAll(getDirectSuccessors(flowNode));
			}
			temp = temp2;
			Set<FlowNode> temp3 = new HashSet<FlowNode>();
			for (FlowNode flowNode : temp) {
				if(!(result.contains(flowNode))) {
					result.add(flowNode);
				} else {
					temp3.add(flowNode);
				}
			}
			for (FlowNode flowNode : temp3) {
				temp.remove(flowNode);
			}
		}
		
		return result;
	}
}
