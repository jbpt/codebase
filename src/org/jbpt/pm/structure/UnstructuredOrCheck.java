package org.jbpt.pm.structure;

import java.util.ArrayList;
import java.util.List;

import org.jbpt.graph.algo.rpst.RPST;
import org.jbpt.graph.algo.rpst.RPSTNode;
import org.jbpt.graph.algo.tctree.TCType;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.Gateway;
import org.jbpt.pm.OrGateway;
import org.jbpt.pm.ProcessModel;


/**
 * Checks whether the {@link ProcessModel} contains unstructered OR {@link Gateway}s.
 * @author Christian Wiggert
 *
 */
public class UnstructuredOrCheck implements ICheck {

	@Override
	public List<String> check(ProcessModel process) {
		List<String> errors = new ArrayList<String>();
		RPST<ControlFlow<FlowNode>, FlowNode> rpst = new RPST<ControlFlow<FlowNode>, FlowNode>(process);
		for (RPSTNode<ControlFlow<FlowNode>, FlowNode> rigid:rpst.getVertices(TCType.R)) 
			for (FlowNode node:rigid.getSkeleton().getVertices()) 
				if (node instanceof OrGateway)
					errors.add("Gateway " + node.getId() + " is an unstructured OR-Gateway.");
		
		return errors;
	}

}
