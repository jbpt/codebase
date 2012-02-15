package org.jbpt.pm.bpmn;

import org.jbpt.pm.FlowNode;
import org.jbpt.pm.NonFlowNode;

/**
 * This is the interface class for the BPMN subprocess class.
 * 
 * @author Cindy Fähnrich
 *
 */

public interface ISubprocess extends IBpmnActivity {
	
	/**
	 * Sets the process url of the linked subprocess
	 * @param url
	 */
	public void setProcessUrl(String url);
	/**
	 * @return the url (e.g. file path) of the linked subprocess
	 */
	public String getProcessUrl();
	/**
	 * @return whether this subprocess is event-driven
	 */
	public boolean isEventDriven();
	/**
	 * Sets whether this subprocess is event-driven
	 * @param driven
	 */
	public void setEventDriven(boolean driven);
	/**
	 * Sets whether this is a collapsed subprocess
	 * @param collapsed
	 */
	public void setCollapsed(boolean collapsed);
	/**
	 * @return whether this subprocess is collapsed
	 */
	public boolean isCollapsed();
	/**
	 * @return whether this subprocess is an adhoc one
	 */
	public boolean isAdhoc();
	/**
	 * Sets this subprocess to an adhoc process with 
	 * parallel execution
	 */
	public void setParallelAdhoc();
	/**
	 * Sets this subprocess to an adhoc process with 
	 * sequential execution
	 */
	public void setSequentialAdhoc();
	/**
	 * resets this process to be no adhoc process
	 */
	public void resetAdhoc();
	/**
	 * @return whether the adhoc order of the subprocess is sequential or
	 * parallel. Returns "None" if it is no adhoc subprocess at all
	 */
	public String getAdhocOrder();
	/**
	 * adds a {@link FlowNode} to the subprocess' flow nodes
	 * @param n flow node to add
	 */
	public void addFlowNode(FlowNode n);
	/**
	 * adds a {@link NonFlowNode} to the subprocess' non flow nodes
	 * @param n non flow node to add
	 */
	public void addNonFlowNode(NonFlowNode n);
	/**
	 * adds a {@link BpmnControlFlow} to the subprocess
	 * @return TODO
	 */
	public BpmnControlFlow<FlowNode> addControlFlow(FlowNode from, FlowNode to, boolean defaultFlow);
	/**
	 * adds a {@link BpmnControlFlow} to the subprocess
	 * @param from
	 * @param to
	 * @param condition
	 * @return
	 */
	public BpmnControlFlow<FlowNode> addControlFlow(FlowNode from, FlowNode to, String condition);
	/**
	 * adds a {@link BpmnControlFlow} to the subprocess
	 * @param from
	 * @param to
	 * @param condition
	 * @param defaultFlow
	 * @return
	 */
	public BpmnControlFlow<FlowNode> addControlFlow(FlowNode from, FlowNode to, String condition, boolean defaultFlow);
	
	/**
	 * adds a {@link BpmnMessageFlow} to the subprocess
	 * @param from
	 * @param to
	 * @return TODO
	 */
	public BpmnMessageFlow addMessageFlow(Object from, Object to);
}
