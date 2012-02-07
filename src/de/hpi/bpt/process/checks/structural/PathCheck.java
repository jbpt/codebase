package de.hpi.bpt.process.checks.structural;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.hpi.bpt.process.FlowNode;
import de.hpi.bpt.process.ProcessModel;

/**
 * Checks whether every {@link FlowNode} in a {@link ProcessModel} is positioned on a path
 * from a source node to a sink node. 
 * @author Christian Wiggert
 *
 */
public class PathCheck implements ICheck {

	private HashSet<FlowNode> visited;
	
	@Override
	public List<String> check(ProcessModel process) {
		List<String> errors = new ArrayList<String>();
		
		HashSet<FlowNode> sources = new HashSet<FlowNode>();
		HashSet<FlowNode> sinks = new HashSet<FlowNode>();
		for (FlowNode node:process.getVertices()) {
			if (process.getEdgesWithTarget(node).size() == 0)
				sources.add(node);
			if (process.getEdgesWithSource(node).size() == 0)
				sinks.add(node);
		}
		for (FlowNode node:process.getVertices()) {
			visited = new HashSet<FlowNode>();
			boolean isOnPath = true;
			if (!sources.contains(node)) {
				isOnPath = hasSource(process, node, sources);
			}
			visited = new HashSet<FlowNode>();
			if (!sinks.contains(node)) {
				isOnPath = hasSink(process, node, sinks);
			}
			if (!isOnPath) 
				errors.add("Node " + node.getId() + " is not on a path from a source to a sink node.");
		}
		return errors;
	}
	
	private boolean hasSource(ProcessModel process, FlowNode node, HashSet<FlowNode> sources) {
		visited.add(node);
		if (sources.contains(node))
			return true;
		for (FlowNode pred:process.getDirectPredecessors(node)) {
			if (!visited.contains(pred) && hasSource(process, pred, sources))
				return true;
		}
		return false;
	}
	
	private boolean hasSink(ProcessModel process, FlowNode node, HashSet<FlowNode> sinks) {
		visited.add(node);
		if (sinks.contains(node))
			return true;
		for (FlowNode succ:process.getDirectSuccessors(node)) {
			if (!visited.contains(succ) && hasSink(process, succ, sinks))
				return true;
		}
		return false;
	}

}