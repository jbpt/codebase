package org.jbpt.pm.epc;

import java.util.Collection;

import org.jbpt.pm.IControlFlow;
import org.jbpt.pm.IFlowNode;
import org.jbpt.pm.INonFlowNode;
import org.jbpt.pm.IProcessModel;


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