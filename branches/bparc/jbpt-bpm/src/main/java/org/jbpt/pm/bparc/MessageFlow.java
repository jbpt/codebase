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
public class MessageFlow<V extends IFlowNode> extends ControlFlow<V> implements IMessageFlow<V> {

	/**
	 * @param graph
	 * @param from
	 * @param to
	 */
	public MessageFlow(AbstractDirectedGraph<?, V> graph,V from, V to) {
		super(graph, from, to);
	}
}
