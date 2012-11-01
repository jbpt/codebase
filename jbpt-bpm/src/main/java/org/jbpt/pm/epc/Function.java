package org.jbpt.pm.epc;

import org.jbpt.pm.Activity;

/**
 * EPC function implementation
 * @author Artem Polyvyanyy
 *
 */
public class Function extends Activity implements IFunction {

	private long duration = 0;
	
	public Function() {
		super();
	}

	public Function(String name, String desc) {
		super(name, desc);
	}

	public Function(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * @see de.hpi.bpt.process.epc.flow.IFunction#getDuration()
	 */
	public long getDuration() {
		return this.duration;
	}

	/*
	 * (non-Javadoc)
	 * @see de.hpi.bpt.process.epc.flow.IFunction#setDuration(long)
	 */
	public void setDuration(long d) {
		this.duration = d;
	}
}
