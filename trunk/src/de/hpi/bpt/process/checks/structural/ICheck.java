package de.hpi.bpt.process.checks.structural;

import java.util.List;

import de.hpi.bpt.process.Process;

public interface ICheck {
	public List<String> check(Process process);
}
