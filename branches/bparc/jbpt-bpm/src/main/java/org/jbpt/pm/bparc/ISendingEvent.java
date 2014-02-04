/**
 * 
 */
package org.jbpt.pm.bparc;

import java.util.Collection;

/**
 * @author Robert Breske and Marcin Hewelt
 *
 */
public interface ISendingEvent extends IEvent {
	
	public Collection<IReceivingEvent> getPostset(); 
	
	public Collection<IReceivingEvent> getConflictSet();

}
