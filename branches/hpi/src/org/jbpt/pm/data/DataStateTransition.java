package org.jbpt.pm.data;

import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.graph.abs.AbstractDirectedGraph;


/**
 * The data state transition relation, i.e. the connection
 * between two {@link DataState}s in the {@link ObjectLifeCycle}
 * 
 * @author Andreas Meyer
 *
 */
public class DataStateTransition<D extends IDataState> extends AbstractDirectedEdge<D> implements IDataStateTransition<D> {
	public DataStateTransition(AbstractDirectedGraph<?, D> g, D source, D target) {
		super(g, source, target);
	}
	
	public DataStateTransition(D source, D target) {
		super(null, source, target);
	}
}
