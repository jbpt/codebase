package de.hpi.bpt.process.petri.bp.construct;

import java.util.Collection;

import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.bp.BehaviouralProfile;

/**
 * Interface for all computations that derive a behavioural 
 * profile for a Petri net.
 * 
 * @author matthias.weidlich
 *
 */
public interface BPCreator {

	/**
	 * Returns the behavioural profile for the given Petri net. Depending on 
	 * the concrete creator, the profile is computed either for all transitions 
	 * or for all nodes.
	 * 
	 * @param Petri net
	 * @return the behavioural profile for the Petri net
	 */
	public BehaviouralProfile deriveBehaviouralProfile(PetriNet pn);

	/**
	 * Returns the behavioural profile for the given collection of nodes 
	 * of the Petri net. Whether this collection must comprise solely transitions 
	 * or is allowed to comprise places depends on the implementation of the creator.
	 * 
	 * @param Petri net
	 * @param a collection of nodes of the Petri net
	 * @return the behavioural profile for the Petri net
	 */
	public BehaviouralProfile deriveBehaviouralProfile(PetriNet pn, Collection<Node> nodes);

}
