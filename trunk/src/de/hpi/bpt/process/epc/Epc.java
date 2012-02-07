package de.hpi.bpt.process.epc;

import java.util.Collection;

import de.hpi.bpt.process.ControlFlow;
import de.hpi.bpt.process.FlowNode;
import de.hpi.bpt.process.NonFlowNode;
import de.hpi.bpt.process.ProcessModel;

/**
 * EPC implementation
 * 
 * @author Artem Polyvyanyy, Tobias Hoppe, Cindy Fähnrich
 */
public class Epc extends ProcessModel implements IEpc<ControlFlow<FlowNode>, FlowNode, NonFlowNode>
{
	/* (non-Javadoc)
	 * @see de.hpi.bpt.process.epc.IEpc#getFunctions()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection<Function> getFunctions() {
		return (Collection<Function>) this.filter(Function.class);
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpt.process.epc.IEpc#getProcessInterfaces()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection<ProcessInterface> getProcessInterfaces() {
		return (Collection<ProcessInterface>) this.filter(ProcessInterface.class);
	}
}

