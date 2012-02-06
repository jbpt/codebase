package de.hpi.bpt.process.checks.structural;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.hpi.bpt.process.Node;
import de.hpi.bpt.process.Process;

/**
 * Checks whether every {@link Node} in a {@link Process} is positioned on a path
 * from a source node to a sink node. 
 * @author Christian Wiggert
 *
 */
public class PathCheck implements ICheck {

	private HashSet<Node> visited;
	
	@Override
	public List<String> check(Process process) {
		List<String> errors = new ArrayList<String>();
		
		HashSet<Node> sources = new HashSet<Node>();
		HashSet<Node> sinks = new HashSet<Node>();
		for (Node node:process.getVertices()) {
			if (process.getEdgesWithTarget(node).size() == 0)
				sources.add(node);
			if (process.getEdgesWithSource(node).size() == 0)
				sinks.add(node);
		}
		for (Node node:process.getVertices()) {
			visited = new HashSet<Node>();
			boolean isOnPath = true;
			if (!sources.contains(node)) {
				isOnPath = hasSource(process, node, sources);
			}
			visited = new HashSet<Node>();
			if (!sinks.contains(node)) {
				isOnPath = hasSink(process, node, sinks);
			}
			if (!isOnPath) 
				errors.add("Node " + node.getId() + " is not on a path from a source to a sink node.");
		}
		return errors;
	}
	
	private boolean hasSource(Process process, Node node, HashSet<Node> sources) {
		visited.add(node);
		if (sources.contains(node))
			return true;
		for (Node pred:process.getDirectPredecessors(node)) {
			if (!visited.contains(pred) && hasSource(process, pred, sources))
				return true;
		}
		return false;
	}
	
	private boolean hasSink(Process process, Node node, HashSet<Node> sinks) {
		visited.add(node);
		if (sinks.contains(node))
			return true;
		for (Node succ:process.getDirectSuccessors(node)) {
			if (!visited.contains(succ) && hasSink(process, succ, sinks))
				return true;
		}
		return false;
	}

}
