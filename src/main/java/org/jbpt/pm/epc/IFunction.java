package org.jbpt.pm.epc;

import org.jbpt.pm.IActivity;

/**
 * EPC function interface
 *
 * @author Artem Polyvyanyy
 */
public interface IFunction extends IActivity {
	
	/**
	 * Get function duration in milliseconds
	 * @return Function duration
	 */
	public long getDuration();
	
	/**
	 * Set function duration
	 * @param duration Duration in milliseconds
	 */
	public void setDuration(long duration);
}
