package org.jbpt.pm.checks.structural;

import java.util.ArrayList;
import java.util.List;

import org.jbpt.pm.ProcessModel;


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
