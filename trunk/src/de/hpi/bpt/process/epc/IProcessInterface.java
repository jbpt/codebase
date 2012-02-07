package de.hpi.bpt.process.epc;

import de.hpi.bpt.process.ControlFlow;
import de.hpi.bpt.process.FlowNode;
import de.hpi.bpt.process.IActivity;
import de.hpi.bpt.process.NonFlowNode;


/**
 * EPC process interface interface :)
 * 
 * @author Artem Polyvyanyy
 */
public interface IProcessInterface extends IActivity {
	
	/**
	 * Link a process interface to an EPC 
	 * @param epc EPC to link
	 */
	public void setProcess(IEpc<ControlFlow<FlowNode>, FlowNode, NonFlowNode> epc);
	
	/**
	 * Get linked EPC
	 * @return EPC linked to this process interface, null if no process is linked
	 */
	public IEpc<ControlFlow<FlowNode>, FlowNode, NonFlowNode> getProcess();
}
