package de.hpi.bpt.process.checks.structural;

import java.util.ArrayList;
import java.util.List;

import de.hpi.bpt.graph.algo.rpst.RPST;
import de.hpi.bpt.graph.algo.rpst.RPSTNode;
import de.hpi.bpt.graph.algo.tctree.TCType;
import de.hpi.bpt.process.ControlFlow;
import de.hpi.bpt.process.FlowNode;
import de.hpi.bpt.process.Gateway;
import de.hpi.bpt.process.OrGateway;
import de.hpi.bpt.process.ProcessModel;

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
