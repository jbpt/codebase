package org.jbpt.pm.bpmn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.jbpt.graph.abs.AbstractDirectedGraph;
import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.ProcessModel;


/**
 * Container class for Bpmn process models.
 * @author Cindy Fähnrich, Tobias Hoppe
 *
 * @param <E> {@link BpmnControlFlow} as edges between the flow nodes
 * @param <V> {@link FlowNode} which means Activities, Gateways, Events
 */
public class Bpmn<E extends BpmnControlFlow<V>, V extends FlowNode> extends ProcessModel implements IBpmn {

	/**
	 * Message flow attribute to have access on all message flows of this process model
	 */
	private Vector<BpmnMessageFlow> messageflows = new Vector<BpmnMessageFlow>();

	@Override
	public BpmnControlFlow<FlowNode> addControlFlow(FlowNode from, FlowNode to, String condition, boolean defaultFlow){
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
		BpmnControlFlow<FlowNode> flow = new BpmnControlFlow<FlowNode>(this, from, to);
		
		if (defaultFlow){
			flow.setDefault();
		}
		
		flow.setCondition(condition);
		
		return flow;
	}
	
	@Override
	public BpmnControlFlow<FlowNode> addControlFlow(FlowNode from, FlowNode to, String condition){
		return addControlFlow(from, to, condition, false);
	}
	
	@Override
	public BpmnControlFlow<FlowNode> addControlFlow(FlowNode from, FlowNode to, boolean defaultFlow){
		return addControlFlow(from, to, null, defaultFlow);
	}
	
	@Override
	public BpmnMessageFlow addMessageFlow(IVertex from, IVertex to){
		
		if (from == null || to == null) {
			return null;
		}
		BpmnMessageFlow flow = new BpmnMessageFlow((AbstractDirectedGraph<?,?>)this, from, to);
		this.messageflows.add(flow);
		
		return flow;
	}
	
	@Override
	public void addControlFlow(BpmnControlFlow<FlowNode> flow) {
		Set<FlowNode> set = new HashSet<FlowNode>();
		set.add(flow.getSource());
		set.add(flow.getTarget());
		this.edges.put(flow, set);
	}
	
	@Override
	public void addMessageFlow(BpmnMessageFlow flow) {
		this.messageflows.add(flow);		
	}
	
	@Override
	public Bpmn<BpmnControlFlow<FlowNode>, FlowNode> clone() {
		@SuppressWarnings("unchecked")
		Bpmn<BpmnControlFlow<FlowNode>, FlowNode> clone = (Bpmn<BpmnControlFlow<FlowNode>, FlowNode>) super.clone();
		clone.messageflows = new Vector<BpmnMessageFlow>();
		for (BpmnMessageFlow flow : this.messageflows) {
			clone.messageflows.add((BpmnMessageFlow) flow.clone());
		}
		return clone;
	}
	
	@Override
	public Collection<BpmnMessageFlow> getMessageflows() {
		return new ArrayList<BpmnMessageFlow>(this.messageflows);
	}
}
