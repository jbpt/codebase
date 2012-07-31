package org.jbpt.pm.bpmn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.NonFlowNode;
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
	public BpmnControlFlow<FlowNode> addControlFlow(FlowNode from, FlowNode to){
		return addControlFlow(from, to, null, true);
	}
	
	public BpmnControlFlow<FlowNode> addControlFlow(FlowNode from, FlowNode to, BpmnEvent event) {
		BpmnControlFlow<FlowNode> edge = (BpmnControlFlow<FlowNode>) addControlFlow(from, to);
		edge.attachEvent(event);
		return edge;
	}
	
	@Override
	public void addControlFlow(BpmnControlFlow<FlowNode> flow) {
		Set<FlowNode> set = new HashSet<FlowNode>();
		set.add(flow.getSource());
		set.add(flow.getTarget());
		this.edges.put(flow, set);
	}

	@Override
	public BpmnMessageFlow addMessageFlow(IVertex from, IVertex to){
		
		if (from == null || to == null) {
			return null;
		}
		BpmnMessageFlow flow = new BpmnMessageFlow(null, from, to);
		this.addMessageFlow(flow);
		
		return flow;
	}
	
	@Override
	public void addMessageFlow(BpmnMessageFlow flow) {
		this.messageflows.add(flow);
		IVertex source = flow.getSource();
		if (source instanceof NonFlowNode) {
			this.nonFlowNodes.add((NonFlowNode) source);			
		} else {
			this.addFlowNode((FlowNode) source);
		}	
		IVertex target = flow.getTarget();
		if (target instanceof NonFlowNode) {
			this.nonFlowNodes.add((NonFlowNode) target);			
		} else {
			this.addFlowNode((FlowNode) target);
		}
	}
	
	@Override
	public Bpmn<BpmnControlFlow<FlowNode>, FlowNode> clone() {
		@SuppressWarnings("unchecked")
		Bpmn<BpmnControlFlow<FlowNode>, FlowNode> clone = (Bpmn<BpmnControlFlow<FlowNode>, FlowNode>) super.clone();
		
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
			if (f instanceof BpmnControlFlow) {
				BpmnEvent event = ((BpmnControlFlow<?>) f).getAttachedEvent();
				clone.addControlFlow(from, to, event);
			} else {
				clone.addControlFlow(from, to);
			}
		}
		
		clone.messageflows = new Vector<BpmnMessageFlow>();
		for (BpmnMessageFlow flow : this.messageflows) {
			clone.messageflows.add((BpmnMessageFlow) flow.clone());
		}
		return clone;
	}
	
	@Override
	public Collection<BpmnMessageFlow> getMessageFlowEdges() {
		return new ArrayList<BpmnMessageFlow>(this.messageflows);
	}
}
