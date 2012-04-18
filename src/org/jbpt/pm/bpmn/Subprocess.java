/**
 * 
 */
package org.jbpt.pm.bpmn;

import org.jbpt.pm.FlowNode;
import org.jbpt.pm.NonFlowNode;

/**
 * Subprocesses in BPMN invoke other processes within the current one, either by linking to another process 
 * model or directly modeling the subprocess. They can also be event-driven (triggered by a startevent and
 * ended by an end event). 
 * 
 * @author Cindy Fähnrich, Tobias Hoppe
 *
 */
public class Subprocess extends BpmnActivity implements ISubprocess {
	
	/**
	 * Boolean to mark whether this subprocess is collapsed
	 */
	
	/**
	 * Boolean to mark whether this subprocess is event-driven.
	 */
	private boolean isEventDriven = false;
	private boolean sequentialAdhocOrdering = false;
	private boolean parallelAdhocOrdering = false;
	private boolean isCollapsed = false;
	
	private Bpmn<BpmnControlFlow<FlowNode>, FlowNode> subprocess = new Bpmn<BpmnControlFlow<FlowNode>, FlowNode>();
	
	/**
	 * Url to process model containing the subprocess.
	 */
	private String processUrl = "";

	/**
	 * Constructor
	 */
	public Subprocess() {
		super();
	}

	/**
	 * @param name the name/title of this subprocess
	 * @param desc the description to this subprocess
	 */
	public Subprocess(String name, String desc) {
		super(name, desc);
	}

	/**
	 * @param name the name/title of this subprocess
	 */
	public Subprocess(String name) {
		super(name);
	}
	
	@Override
	public Subprocess clone() {
		Subprocess clone = (Subprocess) super.clone();
		clone.processUrl = new String(this.processUrl);
		clone.subprocess = this.subprocess.clone();
		return clone;
	}
	
	/**
	 * Set the url to the process model containing this subprocess.
	 * @param url
	 */
	@Override
	public void setProcessUrl(String url) {
		this.processUrl = url;
		
	}

	/**
	 * Returns whether this subprocess is event-driven or not.
	 * @return eventDriven
	 */
	@Override
	public boolean isEventDriven() {
		return this.isEventDriven;
	}

	/**
	 * Marks a subprocess as event-driven or not.
	 * @param driven
	 */
	@Override
	public void setEventDriven(boolean driven) {
		this.isEventDriven = driven;
		
	}

	/**
	 * Returns the url of the process model containing the subprocess.
	 * 
	 * @return process url
	 */
	@Override
	public String getProcessUrl() {
		return this.processUrl;
	}

	@Override
	public void setCollapsed(boolean collapsed) {
		this.isCollapsed = collapsed;
		
	}

	@Override
	public boolean isCollapsed() {
		return this.isCollapsed;
	}

	@Override
	public boolean isAdhoc() {
		return (this.parallelAdhocOrdering || this.sequentialAdhocOrdering);
	}


	@Override
	public String getAdhocOrder() {
		if (this.parallelAdhocOrdering){
			return "Parallel";
		} 
		if (this.sequentialAdhocOrdering){
			return "Sequential";
		}
		return "None"; //if no order is selected, it is no adhoc process
	}

	@Override
	public void setParallelAdhoc() {
		this.parallelAdhocOrdering = true;	
	}

	@Override
	public void setSequentialAdhoc() {
		this.sequentialAdhocOrdering = true;		
	}

	@Override
	public void resetAdhoc() {
		this.sequentialAdhocOrdering = false;
		this.parallelAdhocOrdering = false;
	}

	@Override
	public void addFlowNode(FlowNode n) {
		this.subprocess.addFlowNode(n);
	}

	@Override
	public void addNonFlowNode(NonFlowNode n) {
		this.subprocess.addNonFlowNode(n);		
	}

	@Override
	public BpmnControlFlow<FlowNode> addControlFlow(FlowNode from, FlowNode to, boolean defaultFlow) {
		return this.subprocess.addControlFlow(from, to, defaultFlow);
		
	}

	@Override
	public BpmnControlFlow<FlowNode> addControlFlow(FlowNode from, FlowNode to, String condition) {
		return this.subprocess.addControlFlow(from, to, condition);
	}

	@Override
	public BpmnControlFlow<FlowNode> addControlFlow(FlowNode from, FlowNode to,
			String condition, boolean defaultFlow) {
		return this.subprocess.addControlFlow(from, to, condition, defaultFlow);
	}

	@Override
	public BpmnMessageFlow addMessageFlow(Object from, Object to) {
		return this.subprocess.addMessageFlow(from, to);
	}

	@Override
	public Bpmn<BpmnControlFlow<FlowNode>, FlowNode> getSubProcess() {
		return this.subprocess;
	}
}
