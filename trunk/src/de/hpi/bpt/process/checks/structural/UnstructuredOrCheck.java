package de.hpi.bpt.process.checks.structural;

import java.util.ArrayList;
import java.util.List;

import de.hpi.bpt.graph.algo.rpst.RPST;
import de.hpi.bpt.graph.algo.rpst.RPSTNode;
import de.hpi.bpt.graph.algo.tctree.TCType;
import de.hpi.bpt.process.ControlFlow;
import de.hpi.bpt.process.Gateway;
import de.hpi.bpt.process.Node;

import de.hpi.bpt.process.Process;

/**
 * Checks whether the {@link Process} contains unstructered OR {@link Gateway}s.
 * @author Christian Wiggert
 *
 */
public class UnstructuredOrCheck implements ICheck {

	@Override
	public List<String> check(Process process) {
		List<String> errors = new ArrayList<String>();
		RPST<ControlFlow, Node> rpst = new RPST<ControlFlow, Node>(process);
		for (RPSTNode<ControlFlow, Node> rigid:rpst.getVertices(TCType.R)) 
			for (Node node:rigid.getSkeleton().getVertices()) 
				if (node instanceof Gateway && ((Gateway) node).isOR())
					errors.add("Gateway " + node.getId() + " is an unstructured OR-Gateway.");
		
		return errors;
	}

}
