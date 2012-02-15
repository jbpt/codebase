package org.jbpt.pm.epc;

import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.IProcessModel;
import org.jbpt.pm.NonFlowNode;
import org.jbpt.pm.ProcessModel;


/**
 * EPC process interface implementation
 * @author Artem Polyvyanyy, Cindy Fähnrich, Tobias Hoppe
 *
 */
public class ProcessInterface extends FlowNode implements IProcessInterface {
	
	private IEpc<ControlFlow<FlowNode>, FlowNode, NonFlowNode> epc = null;

	private String entry = ""; //the reference to the referred process
	
	public ProcessInterface() {
		super();
	}

	public ProcessInterface(String name, String desc) {
		super(name, desc);
	}

	public ProcessInterface(String name) {
		super(name);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ProcessInterface clone() {
		ProcessInterface clone = (ProcessInterface) super.clone();
		clone.epc = (IEpc<ControlFlow<FlowNode>, FlowNode, NonFlowNode>) ((ProcessModel) this.epc).clone();
		return clone;
	}

	/*
	 * (non-Javadoc)
	 * @see de.hpi.bpt.process.epc.flow.IProcessInterface#getProcess()
	 */
	@Override
	public IEpc<ControlFlow<FlowNode>, FlowNode, NonFlowNode> getProcess() {
		return epc;
	}

	/*
	 * (non-Javadoc)
	 * @see de.hpi.bpt.process.epc.flow.IProcessInterface#setProcess(de.hpi.bpt.process.epc.IEPC)
	 */
	@Override
	public void setProcess(IEpc<ControlFlow<FlowNode>, FlowNode, NonFlowNode> epc) {
		this.epc = epc;
	}
	
	/**
	 * Sets the reference uri of the referred process.
	 * @param entry
	 */
	public void setEntry(String entry){
		this.entry = entry;
	}
	
	/**
	 * @return the reference uri of the referred proccess
	 */
	public String getEntry(){
		return this.entry;
	}
}
