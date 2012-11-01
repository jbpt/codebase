package org.jbpt.pm.epc.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.NonFlowNode;
import org.jbpt.pm.epc.Epc;
import org.jbpt.pm.epc.IEpc;


/**
 * It might be the case that one EPC model actually contains two EPC processes
 * that are not connected. The EPCSplitter class checks whether this is the case
 * and splits the model into multiple models if necessary.
 * 
 * @author gero.decker, matthias.weidlich
 *
 */
public class EPCSplitter {

	protected IEpc<ControlFlow<FlowNode>, FlowNode, NonFlowNode> model;
	protected List<Set<FlowNode>> nodeSets = null;

	public EPCSplitter(IEpc<ControlFlow<FlowNode>, FlowNode, NonFlowNode> model) {
		this.model = model;
	}
	
	/**
	 * Decide whether the EPC model actually contains more than one EPC process
	 * and therefore needs to be split up.
	 * 
	 * @return true, if the model contains more than one process
	 */
	public boolean needsSplitting() {
		Set<FlowNode> nodes = new HashSet<FlowNode>();
	
		FlowNode n = (FlowNode) model.getFlowNodes().toArray()[0];
		nodes.add(n);
		checkIsConnected(nodes, n);
		
		if (nodes.size() == model.getFlowNodes().size()) {
			return false;
		} else {
			nodeSets = new ArrayList<Set<FlowNode>>();
			nodeSets.add(nodes);
			int count = nodes.size();
			while (count < model.getFlowNodes().size()) {
				nodes = new HashSet<FlowNode>();
				FlowNode n2 = getNextNode();
				nodes.add(n2);
				checkIsConnected(nodes, n2);
				count += nodes.size();
				nodeSets.add(nodes);
			}
			return true;
		}
	}

	protected FlowNode getNextNode() {
		for (FlowNode n: model.getFlowNodes()) {
			boolean found = false;
			for (Set<FlowNode> nodes: nodeSets)
				if (nodes.contains(n)) {
					found = true;
					break;
				}
			if (!found)
				return n;
		}
		return null;
	}

	/**
	 * Checks whether a set of nodes is connected to a given flow object.
	 * 
	 * @param nodes
	 * @param n
	 */
	protected void checkIsConnected(Set<FlowNode> nodes, FlowNode n) {
		for (ControlFlow<FlowNode> e: model.getControlFlow()) {
			if (e.getTarget().equals(n)) {
				FlowNode n2 = e.getSource();
				if (!nodes.contains(n2)) {
					nodes.add(n2);
					checkIsConnected(nodes, n2);
				}
			}
		}
		for (ControlFlow<FlowNode> e: model.getControlFlow()) {
			if (e.getSource().equals(n)) {
				FlowNode n2 = e.getTarget();
				if (!nodes.contains(n2)) {
					nodes.add(n2);
					checkIsConnected(nodes, n2);
				}
			}
		}
	}

	/**
	 * Splits up an EPC model into multiple EPC models, each containing exactly one
	 * EPC process.
	 * 
	 * @return a list of EPC models
	 */
	public List<IEpc<ControlFlow<FlowNode>, FlowNode, NonFlowNode>> splitModel() {
		List<IEpc<ControlFlow<FlowNode>, FlowNode, NonFlowNode>> models = new ArrayList<IEpc<ControlFlow<FlowNode>, FlowNode, NonFlowNode>>(nodeSets.size());
		int i=1;
		for (Set<FlowNode> nodes: nodeSets) {
			IEpc<ControlFlow<FlowNode>, FlowNode, NonFlowNode> newm = new Epc();
			newm.setId(model.getId());
			newm.setName(model.getName()+"_"+i);
			models.add(newm);
			
			for (FlowNode n: nodes)
				newm.addFlowNode(n);
			for (ControlFlow<FlowNode> e: model.getControlFlow())
				if (nodes.contains(e.getSource())) {
					newm.addControlFlow(e.getSource(), e.getTarget());
				}
			
			i++;
		}
		return models;
	}

}


