package de.hpi.bpt.process.checks.structural;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.hpi.bpt.process.Node;
import de.hpi.bpt.process.Process;

/**
 * Checks whether every {@link Node} in the {@link Process} has a unique identifier. 
 * @author Christian Wiggert
 *
 */
public class UniqueIdCheck implements ICheck {

	@Override
	public List<String> check(Process process) {
		List<String> errors = new ArrayList<String>();
		HashSet<String> ids = new HashSet<String>();
		HashSet<String> duplicates = new HashSet<String>();
		for (Node node:process.getNodes()) {
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
