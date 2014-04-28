/**
 * 
 */
package org.jbpt.pm.bpa;

import java.util.Collection;

/**
 * @author Robert Breske and Marcin Hewelt
 *
 */
public interface IReceivingEvent extends IEvent {
	
	public Collection<ISendingEvent> getPreset();

}
