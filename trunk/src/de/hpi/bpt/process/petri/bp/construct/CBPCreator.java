package de.hpi.bpt.process.petri.bp.construct;

import java.util.Collection;

import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.bp.BehaviouralProfile;
import de.hpi.bpt.process.petri.bp.CausalBehaviouralProfile;

/**
 * Interface for all computations that derive a causal behavioural 
 * profile for a Petri net.
 * 
 * @author matthias.weidlich
 *
 */
public interface CBPCreator {

	/**
	 * Returns the causal behavioural profile for all transitions or all nodes of 
	 * the given Petri net. Whether the profile is computed for all transitions or all 
	 * nodes depends on the actual implementation of the creator.
	 * 
	 * @param Petri net
	 * @return the causal behavioural profile for the Petri net
	 */
	public CausalBehaviouralProfile deriveCausalBehaviouralProfile(PetriNet pn);

	/**
	 * Returns the causal behavioural profile for the given collection of nodes of the 
	 * Petri net. Whether this collection must comprise solely transitions or is allowed to 
	 * comprise places depends on the implementation of the creator.
	 * 
	 * @param Petri net
	 * @param collection of nodes of the Petri net
	 * @return the causal behavioural profile for the Petri net
	 */
	public CausalBehaviouralProfile deriveCausalBehaviouralProfile(PetriNet pn, Collection<Node> nodes);

	/**
	 * Returns the causal behavioural profile for the given behavioural profile. This method
	 * may be used if the behavioural profile has been computed already and the respective
	 * creator is used to add only the co-occurrence relation to yield the causal behavioural
	 * profile.
	 * 
	 * @param profile the behavioural profile 
	 * @return the causal behavioural profile for the Petri net to which the given behavioural profile belongs
	 */
	public CausalBehaviouralProfile deriveCausalBehaviouralProfile(BehaviouralProfile profile);

}
