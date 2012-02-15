package de.hpi.bpt.process.checks.structural;

import java.util.ArrayList;
import java.util.List;

import de.hpi.bpt.process.ProcessModel;

/**
 * Checks if a {@link ProcessModel} contains any {@link Task}
 * @author Christian Wiggert
 *
 */
public class ContainsTaskCheck implements ICheck {

	@Override
	public List<String> check(ProcessModel process) {
		List<String> errors = new ArrayList<String>();
		if (process.getActivities().size() == 0)
			errors.add("Process " + process.getName() + " contains no task");
		return errors;
	}

}
