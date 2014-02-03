/**
 * 
 */
package org.jbpt.pm.bparc;

import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.IProcessModel;
import org.jbpt.pm.NonFlowNode;

/**
 * Interface for BParc landscapes
 * @author Robert Breske and Marcin Hewelt
 *
 */
public interface IBparc extends IProcessModel<ControlFlow<FlowNode>, FlowNode, NonFlowNode> {
	
	public ControlFlow<FlowNode> addControlFlow(FlowNode from, FlowNode to);
	
	public ControlFlow<FlowNode> addControlFlow(FlowNode from, FlowNode to, float probability);
	

}
