package de.hpi.bpt.process.petri.bp;

import java.util.Collection;
import java.util.List;

import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;


/**
 * Captures the behavioural profile of a Petri net for a given
 * set of nodes. 
 * 
 * @author matthias.weidlich
 *
 */
public class BehaviouralProfile extends RelSet {
	
	public BehaviouralProfile(PetriNet pn, List<Node> nodes) {
		super(pn, nodes);
		super.lookAhead = RELATION_FAR_LOOKAHEAD;
	}

	public BehaviouralProfile(int size) {
		super(size);
		super.lookAhead = RELATION_FAR_LOOKAHEAD;
	}

	public BehaviouralProfile(PetriNet pn, Collection<Node> nodes) {
		super(pn, nodes);
		super.lookAhead = RELATION_FAR_LOOKAHEAD;
	}

	
}
