/**
 * 
 */
package org.jbpt.pm.bpa;

import java.util.Collection;

import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.IProcessModel;
import org.jbpt.pm.NonFlowNode;

/**
 * Interface for BPA
 * @author Robert Breske and Marcin Hewelt
 *
 */
public interface IBpa extends IProcessModel<ControlFlow<FlowNode>, FlowNode, NonFlowNode> {
	
	public ControlFlow<FlowNode> addControlFlow(FlowNode from, FlowNode to);
	
	public ControlFlow<FlowNode> addControlFlow(FlowNode from, FlowNode to, float probability);
	
	public Collection<BpaProcess> getAllProcesses();
	
	public Collection<Event> getAllEvents();

}
