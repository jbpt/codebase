package org.jbpt.pm.data;

import java.util.Collection;


/**
 * Interface for a general object life cycle model.
 * 
 * @author Andreas Meyer
 *
 * @param <E> Class for edges.
 * @param <D> Class for nodes representing data states being connected by <E>
 */
public interface IObjectLifeCycle<E extends IDataStateTransition<D>, D extends IDataState> {

	public String getName();
	
	/**
	 * Add edge to this object life cycle model.
	 * 
	 * @param from source data state
	 * @param to target data state
	 * 
	 * @return data state added to the {@link ObjectLifeCycle} model, <code>null</code> upon failure 
	 */
	public E addDataStateTransition(D from, D to);
	
	/**
	 * Add data state to this object life cycle model.
	 * 
	 * @param data state to add
	 * 
	 * @return data state that was added to the {@link ObjectLifeCycle} model, <code>null</code> upon failure
	 */
	public D addDataStateNode(D dataState);
	
	/**
	 * Get the edges connecting the data states of this data object life cycle model.
	 * 
	 * @return Collection of edges
	 */
	public Collection<E> getDataStateTransitions();
	
	/**
	 * Return all {@link DataState} which precede the given {@link DataState} in the {@link ObjectLifeCycle}.
	 * 
	 * @param fn {@link DataState} to start from
	 * 
	 * @return {@link Collection} containing all predecessors of the given {@link DataState}
	 */
	public Collection<DataState> getAllPredecessors(DataState ds);

	/**
	 * Return all {@link DataState} which succeed the given {@link DataState} in the {@link ObjectLifeCycle}.
	 * 
	 * @param fn {@link DataState} to start from
	 * 
	 * @return {@link Collection} containing all successors of the given {@link DataState}
	 */
	public Collection<DataState> getAllSuccessors(DataState ds);
}
