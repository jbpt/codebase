package de.hpi.bpt.process.epc;

import java.util.Collection;

import de.hpi.bpt.process.IControlFlow;
import de.hpi.bpt.process.IFlowNode;
import de.hpi.bpt.process.INonFlowNode;
import de.hpi.bpt.process.IProcessModel;

/**
 * A business process captured in the event-driven process chain (EPC) notation.
 * 
 * @author Artem Polyvyanyy, Tobias Hoppe
 */
public interface IEpc<CF extends IControlFlow<FN>, 
						FN extends IFlowNode,
						NFN extends INonFlowNode> extends IProcessModel<CF, FN, NFN> {
	
	/**
	 * @return a list of all {@link Function}s of an {@link Epc} model
	 */
	Collection<Function> getFunctions();

	/**
	 * Get all {@link ProcessInterface}s.
	 * @return Collection of all {@link ProcessInterface}s of this {@link Epc}
	 */
	Collection<ProcessInterface> getProcessInterfaces();
}