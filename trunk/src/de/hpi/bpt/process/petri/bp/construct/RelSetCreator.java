package de.hpi.bpt.process.petri.bp.construct;

import java.util.Collection;

import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.bp.RelSet;

/**
 * Interface for all computations that derive a relation 
 * set of a Petri net.
 * 
 * @author matthias.weidlich
 *
 */
public interface RelSetCreator {

	/**
	 * Returns the relation set for the given Petri net. Depending on 
	 * the concrete creator, the relations are computed either for all transitions 
	 * or for all nodes.
	 * 
	 * @param Petri net
	 * @return the relation set of the Petri net
	 */
	public RelSet deriveRelationSet(PetriNet pn);

	/**
	 * Returns the relation set for the given collection of nodes 
	 * of the Petri net. Whether this collection must comprise solely transitions 
	 * or is allowed to comprise places depends on the implementation of the creator.
	 * 
	 * @param Petri net
	 * @param a collection of nodes of the Petri net
	 * @return the relation set of the Petri net
	 */
	public RelSet deriveRelationSet(PetriNet pn, Collection<Node> nodes);

}
