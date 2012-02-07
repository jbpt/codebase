package de.hpi.bpt.process.checks.structural;

import java.util.List;

import de.hpi.bpt.process.ProcessModel;

public interface ICheck {
	public List<String> check(ProcessModel process);
}
