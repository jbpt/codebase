package de.hpi.bpt.process.checks.structural;

import java.util.ArrayList;
import java.util.List;

import de.hpi.bpt.process.Activity;
import de.hpi.bpt.process.ProcessModel;

/**
 * Checks whether the {@link ProcessModel} has at least one source and one sink node. 
 * @author Christian Wiggert
 *
 */
public class SourceAndSinkCheck implements ICheck {

	@Override
	public List<String> check(ProcessModel process) {
		List<String> errors = new ArrayList<String>();
		int source = 0, sink = 0;
		for (Activity task:process.getActivities()) {
			if (process.getEdgesWithSource(task).size() == 0)
				sink++;
			if (process.getEdgesWithTarget(task).size() == 0)
				source++;
		}
		if (source == 0)
			errors.add("Process " + process.getName() + " has no source task.");
		if (sink == 0)
			errors.add("Process " + process.getName() + " has no sink task.");
		return errors;
	}

}
