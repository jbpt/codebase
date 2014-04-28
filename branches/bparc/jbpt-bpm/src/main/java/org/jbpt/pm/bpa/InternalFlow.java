/**
 * 
 */
package org.jbpt.pm.bpa;

import org.jbpt.graph.abs.AbstractDirectedGraph;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.IFlowNode;

/**
 * @author Robert Breske and Marcin Hewelt
 *
 */
public class InternalFlow<V extends IFlowNode> extends ControlFlow<V> implements IInternalFlow<V> {

	/**
	 * @param graph
	 * @param from
	 * @param to
	 * @param probability
	 */
	public InternalFlow(AbstractDirectedGraph<?, V> graph, V from, V to,
			float probability) {
		super(graph, from, to, probability);
	}
	// TODO: implement validator
}
