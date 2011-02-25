package de.hpi.bpt.process;

import de.hpi.bpt.graph.abs.AbstractMultiDirectedGraph;
import de.hpi.bpt.oryx.erdf.ERDFEdge;

/**
 * 
 * @author Artem Polyvyanyy
 *
 */
public class ControlFlow extends ERDFEdge<Node> {

	@SuppressWarnings("unchecked")
	protected ControlFlow(AbstractMultiDirectedGraph g, Node source, Node target) {
		super(g, source, target);
	}

}
