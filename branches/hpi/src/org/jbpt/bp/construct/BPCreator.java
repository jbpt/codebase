package org.jbpt.bp.construct;

import java.util.Collection;

import org.jbpt.alignment.IEntity;
import org.jbpt.alignment.IEntityModel;
import org.jbpt.bp.BehaviouralProfile;


/**
 * Interface for all computations that derive a behavioural 
 * profile for a model.
 * 
 * @author matthias.weidlich
 *
 */
public interface BPCreator<M extends IEntityModel<N>,N extends IEntity> {

	/**
	 * Returns the behavioural profile for the given model. Depending on 
	 * the concrete creator, the profile relations are computed for a distinguished set 
	 * of entities. For instance, if the model is a Petri net, the behavioural 
	 * profile may be derived for all nodes or all transitions.
	 * 
	 * @param model, a model
	 * @return the behavioural profile of the model
	 */
	public BehaviouralProfile<M, N> deriveBehaviouralProfile(M model);

	/**
	 * Returns the behavioural profile for the given collection of entities 
	 * of the model. Creator classes may implicitly restrict this set.
	 * For instance, if the model is a Petri net, some creators may
	 * requires the set of entities to contain only transitions.
	 * 
	 * @param model, a model
	 * @param a collection of entities of the model
	 * @return the behavioural profile of the model
	 */
	public BehaviouralProfile<M, N> deriveBehaviouralProfile(M model, Collection<N> entities);

}
