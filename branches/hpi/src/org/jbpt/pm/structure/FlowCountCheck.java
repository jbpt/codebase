package org.jbpt.pm.structure;

import java.util.ArrayList;
import java.util.List;

import org.jbpt.pm.Activity;
import org.jbpt.pm.Gateway;
import org.jbpt.pm.ProcessModel;


/**
 * Checks if a {@link ProcessModel} has only {@link Task}s with one incoming and one outgoing edge.
 * Furthermore {@link Gateway}s are checked whether they have at least one incoming and one outgoing edge
 * and in sum at least three connected edges. 
 * @author Christian Wiggert
 *
 */
public class FlowCountCheck implements ICheck {

	@Override
	public List<String> check(ProcessModel process) {
		List<String> errors = new ArrayList<String>();
		for (Activity task:process.getActivities()) {
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
