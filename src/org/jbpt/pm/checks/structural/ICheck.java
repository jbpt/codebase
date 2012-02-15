package org.jbpt.pm.checks.structural;

import java.util.List;

import org.jbpt.pm.ProcessModel;


public interface ICheck {
	public List<String> check(ProcessModel process);
}
