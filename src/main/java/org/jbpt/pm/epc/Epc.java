package org.jbpt.pm.epc;

import java.util.Collection;

import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.NonFlowNode;
import org.jbpt.pm.ProcessModel;


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

