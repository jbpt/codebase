package org.jbpt.bp.construct;

import java.util.Collection;

import org.jbpt.alignment.IEntity;
import org.jbpt.alignment.IEntityModel;
import org.jbpt.bp.BehaviouralProfile;
import org.jbpt.bp.CausalBehaviouralProfile;


/**
 * Interface for all computations that derive a causal behavioural 
 * profile for a model.
 * 
 * @author matthias.weidlich
 *
 */
public interface CBPCreator<M extends IEntityModel<N>,N extends IEntity> {

	/**
	 * Returns the causal behavioural profile for all entities of the given model.
	 * Whether the profile is computed for all entities or only a subset of entities 
	 * depends on the actual implementation of the creator.
	 * 
	 * @param model, a model
	 * @return the causal behavioural profile of the model
	 */
	public CausalBehaviouralProfile<M, N> deriveCausalBehaviouralProfile(M model);

	/**
	 * Returns the causal behavioural profile for the given collection of entities of the 
	 * model. Whether this collection must comprise solely entities of a certain type 
	 * depends on the implementation of the creator.
	 * 
	 * @param model, a model
	 * @param collection of entities of the model
	 * @return the causal behavioural profile of the model
	 */
	public CausalBehaviouralProfile<M, N> deriveCausalBehaviouralProfile(M model, Collection<N> entities);

	/**
	 * Returns the causal behavioural profile for the given behavioural profile. This method
	 * may be used if the behavioural profile has been computed already and the respective
	 * creator is used to add only the co-occurrence relation to yield the causal behavioural
	 * profile.
	 * 
	 * @param profile the behavioural profile 
	 * @return the causal behavioural profile for the model to which the given behavioural profile belongs
	 */
	public CausalBehaviouralProfile<M, N> deriveCausalBehaviouralProfile(BehaviouralProfile<M, N> profile);

}
