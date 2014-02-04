/**
 * 
 */
package org.jbpt.pm.bparc;

import org.jbpt.graph.abs.AbstractDirectedGraph;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;

/**
 * @author Dexter
 *
 */
public class ExternalFlow<V extends FlowNode> extends ControlFlow<V> implements IExternalFlow<V> {

	/**
	 * @param graph
	 * @param from
	 * @param to
	 * @param probability
	 */
	public ExternalFlow(AbstractDirectedGraph<?, V> graph, V from, V to,
			float probability) {
		super(graph, from, to, probability);
		// TODO Auto-generated constructor stub
	}

}
