package org.jbpt.bp.construct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jbpt.bp.RelSet;
import org.jbpt.bp.RelSetType;
import org.jbpt.petri.Flow;
import org.jbpt.petri.Marking;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.petri.behavior.ProjectedStateSpace;


/**
 * 
 * Implemented as a singleton, use <code>getInstance()</code>.
 * 
 * @author matthias.weidlich
 *
 */
public class ProjTARCreatorStateSpace extends AbstractRelSetCreator implements RelSetCreator<NetSystem, Node> {

	private static ProjTARCreatorStateSpace eInstance;
	
	public static ProjTARCreatorStateSpace getInstance() {
		if (eInstance == null)
			eInstance  = new ProjTARCreatorStateSpace();
		return eInstance;
	}
	
	private ProjTARCreatorStateSpace() {
		
	}
	
	// capture the projected state space
	protected ProjectedStateSpace<Flow, Node, Place, Transition, Marking> space;
		
	protected void clear() {
		this.space = null;
	}

	@Override
	public RelSet<NetSystem, Node> deriveRelationSet(NetSystem pn) {
		return deriveRelationSet(pn, new ArrayList<Node>(pn.getTransitions()));
	}
	
	@Override
	public RelSet<NetSystem, Node> deriveRelationSet(NetSystem pn,
			Collection<Node> nodes) {
		
		// clear internal data structures
		clear();
		
		/*
		 * Select projection set (only transitions)
		 */
		Set<Transition> selectedTransitions = new HashSet<>();
		for (Node t : nodes)
			if (t instanceof Transition)
				selectedTransitions.add((Transition)t);
		
		/*
		 * Make sure RelSet is defined only over transitions
		 */
		nodes.retainAll(selectedTransitions);

		/*
		 * Derive projected state space
		 */
		this.space = new ProjectedStateSpace<Flow, Node, Place, Transition, Marking>(pn,selectedTransitions);
		this.space.create();
		
		/*
		 * Init rel set
		 */
		RelSet<NetSystem, Node> rs = new RelSet<NetSystem, Node>(pn,nodes,1);
		RelSetType[][] matrix = rs.getMatrix();
		
		for(Node t1 : rs.getEntities()) {
			int index1 = rs.getEntities().indexOf(t1);
			for (Node t2 : rs.getEntities()) {
				int index2 = rs.getEntities().indexOf(t2);
				
				/*
				 * The behavioural profile matrix is symmetric. Therefore, we 
				 * need to traverse only half of the entries.
				 */
				if (index2 > index1)
					continue;
				
				if (this.space.isStep(t1,t2) && this.space.isStep(t2,t1))
					super.setMatrixEntry(matrix, index1, index2, RelSetType.Interleaving);
				else if (this.space.isStep(t1,t2))
					super.setMatrixEntryOrder(matrix, index1, index2);
				else if (this.space.isStep(t2,t1))
					super.setMatrixEntryOrder(matrix, index2, index1);
				else
					super.setMatrixEntry(matrix, index1, index2, RelSetType.Exclusive);
			}
		}		
		
		return rs;
	}
		
}
