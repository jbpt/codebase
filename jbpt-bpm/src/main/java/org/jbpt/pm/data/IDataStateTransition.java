package org.jbpt.pm.data;

import org.jbpt.graph.abs.IDirectedEdge;


/**
 * Interface for data state transitions in object life cycles
 * 
 * @author Andreas Meyer
 */
public interface IDataStateTransition<D extends IDataState> extends IDirectedEdge<D> {
	
}
