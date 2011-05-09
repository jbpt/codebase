package de.hpi.bpt.process.checks.structural;

import java.util.ArrayList;
import java.util.List;

import de.hpi.bpt.process.Process;
import de.hpi.bpt.process.Task;

public class SourceAndSinkCheck implements ICheck {

	@Override
	public List<String> check(Process process) {
		List<String> errors = new ArrayList<String>();
		int source = 0, sink = 0;
		for (Task task:process.getTasks()) {
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
