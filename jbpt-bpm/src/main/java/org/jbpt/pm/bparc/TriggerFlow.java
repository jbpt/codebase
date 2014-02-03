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
public class TriggerFlow<V extends IFlowNode> extends ControlFlow<V> implements ITriggerFlow<V> {

	/**
	 * @param graph
	 * @param from
	 * @param to
	 */
	public TriggerFlow(AbstractDirectedGraph<?, V> graph, V from, V to) {
		super(graph, from, to);
	}

}
