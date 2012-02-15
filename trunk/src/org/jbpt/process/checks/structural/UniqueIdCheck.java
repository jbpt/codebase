package de.hpi.bpt.process.checks.structural;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.hpi.bpt.process.FlowNode;
import de.hpi.bpt.process.ProcessModel;

/**
 * Checks whether every {@link FlowNode} in the {@link ProcessModel} has a unique identifier. 
 * @author Christian Wiggert
 *
 */
public class UniqueIdCheck implements ICheck {

	@Override
	public List<String> check(ProcessModel process) {
		List<String> errors = new ArrayList<String>();
		HashSet<String> ids = new HashSet<String>();
		HashSet<String> duplicates = new HashSet<String>();
		for (FlowNode node:process.getVertices()) {
			if (!ids.contains(node.getId()))
				ids.add(node.getId());
			else
				duplicates.add(node.getId());
		}
		for (String id:duplicates) {
			if (id.equals(""))
				errors.add("Some nodes have no ID.");
			else
				errors.add("The ID " + id + " occurs multiple times.");
		}
		return errors;
	}

}
