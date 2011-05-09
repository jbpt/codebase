package de.hpi.bpt.process.checks.structural;

import java.util.ArrayList;
import java.util.List;

import de.hpi.bpt.process.Gateway;
import de.hpi.bpt.process.Process;
import de.hpi.bpt.process.Task;

public class FlowCountCheck implements ICheck {

	@Override
	public List<String> check(Process process) {
		List<String> errors = new ArrayList<String>();
		for (Task task:process.getTasks()) {
			if (process.getEdgesWithSource(task).size() > 1)
				errors.add("Task " + task.getId() + " has more than one outgoing flow.");
			if (process.getEdgesWithTarget(task).size() > 1)
				errors.add("Task " + task.getId() + " has more than one incoming flow.");
		}
		for (Gateway gate:process.getGateways()) {
			int in = process.getEdgesWithTarget(gate).size();
			int out = process.getEdgesWithSource(gate).size();
			if (in == 0)
				errors.add("Gateway " + gate.getId() + " has no incoming flow.");
			if (out == 0)
				errors.add("Gateway " + gate.getId() + " has no outgoing flow.");
			if ((in + out) < 3)
				errors.add("Gateway " + gate.getId() + " has less than three flows.");
		}
		return errors;
	}

}
