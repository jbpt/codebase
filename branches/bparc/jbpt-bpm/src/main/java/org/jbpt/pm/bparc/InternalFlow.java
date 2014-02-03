/**
 * 
 */
package org.jbpt.pm.bparc;

import org.jbpt.graph.abs.AbstractDirectedGraph;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.IFlowNode;

/**
 * @author Robert Breske and Marcin Hewelt
 *
 */
public class InternalFlow<V extends IFlowNode> extends ControlFlow<V> implements IInternalFlow<V> {
	// TODO: implement validator
	
	/**
	 * @param graph
	 * @param from
	 * @param to
	 */
	public InternalFlow(AbstractDirectedGraph<?, V> graph, V from, V to) {
		super(graph, from, to);
	}

}
